package com.hionstudios.zerroo.flow;

import java.sql.PreparedStatement;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.javalite.activejdbc.Base;
import org.springframework.web.multipart.MultipartFile;

import com.hionstudios.CachedSelect;
import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.Handler;
import com.hionstudios.db.SqlCriteria;
import com.hionstudios.db.SqlQuery;
import com.hionstudios.db.SqlUtil;
import com.hionstudios.zerroo.model.Courier;
import com.hionstudios.zerroo.model.ServiceablePostcode;
import com.hionstudios.zerroo.model.Transporter;
import com.hionstudios.zerroo.model.TransporterCourierMapping;

public class TransporterTransaction {
    public MapResponse view(DataGridParams params) {
        String sql = "Select Transporters.Id Id, Transporters.Id \"Action\", Transporters.Transporter, Inventories.Inventory, (Select Count(*) From Transporter_Courier_Mappings Where Transporter_Courier_Mappings.Transporter_Id = Transporters.Id) Couriers From Transporters Join Inventories On Inventories.Id = Transporters.Inventory_Id";
        String count = "Select Count(*) From Transporters Join Inventories On Inventories.Id = Transporters.Inventory_Id";
        String[] columns = { "Action", "Transporter", "Inventory", "Couriers" };
        SqlCriteria criteria = SqlUtil.constructCriteria(params, null, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }

    public MapResponse view(long id) {
        String sql = "Select Transporter, Inventory_Id, Array(Select Transporter_Courier_Mappings.Courier_Id From transporter_courier_mappings Where Transporter_Courier_Mappings.Transporter_Id = Transporters.Id) couriers From Transporters Join Inventories On Inventories.Id = Transporters.Inventory_Id Where Transporters.Id = ?";
        return Handler.findFirst(sql, id);
    }

    public MapResponse add(String transporter, long[] couriers, long inventory) {
        Transporter t = new Transporter(transporter, inventory);
        boolean status = t.insert();
        if (!status) {
            return MapResponse.failure();
        }
        long id = t.getLongId();
        for (long courier : couriers) {
            new TransporterCourierMapping(id, courier).insert();
        }
        CachedSelect.dropCache("transporter");
        return MapResponse.success();
    }

    public MapResponse edit(long id, String transporter, long[] couriers, long inventory) {
        Transporter t = Transporter.findById(id);
        TransporterCourierMapping.delete("transporter_id = ?", id);
        for (long courier : couriers) {
            new TransporterCourierMapping(id, courier).insert();
        }
        String oldName = t.getString("transporter");
        long oldInventory = t.getLong("inventory_id");
        if (!oldName.equals(transporter) || oldInventory != inventory) {
            t.set("transporter", transporter);
            t.set("inventory_id", inventory);
            boolean status = t.saveIt();
            if (!status) {
                return MapResponse.failure("Check the name");
            }
        }
        CachedSelect.dropCache("transporter");
        return MapResponse.success();
    }

    public MapResponse couriers(DataGridParams params) {
        String sql = "Select Id, Id \"Action\", Courier, Display, Tracking_Url \"Tracking URL\", (Select Count(*) From Serviceable_Postcodes Where Courier_Id = Couriers.Id And Forward) \"Forward Serviceable\", (Select Count(*) From Serviceable_Postcodes Where Courier_Id = Couriers.Id And Return) \"Return Serviceable\" From Couriers";
        String count = "Select Count(*) From Couriers";
        String[] columns = {
                "Action",
                "Courier",
                "Display",
                "Tracking URL",
                "Forward Serviceable",
                "Return Serviceable" };
        SqlCriteria criteria = SqlUtil.constructCriteria(params, null, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }

    public MapResponse courier(long id) {
        String sql = "Select Couriers.Id, Couriers.Courier, Couriers.Display, Couriers.Tracking_Url Tracking From Couriers Where Couriers.Id = ?";
        return Handler.findFirst(sql, id);
    }

    public MapResponse serviceablePostcodes(long id, MultipartFile excel) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(excel.getInputStream())) {
            XSSFSheet worksheet = workbook.getSheetAt(0);
            ServiceablePostcode.delete("courier_id = ?", id);
            PreparedStatement ps = Base.startBatch(
                    "Insert Into Serviceable_Postcodes (Postcode, Courier_Id, Forward, Return) Values (?, ?, ?, ?)");
            for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
                XSSFRow row = worksheet.getRow(i);
                if (row == null) {
                    break;
                }
                XSSFCell cell = row.getCell(0);
                if (cell == null || cell.getCellType() == CellType.BLANK) {
                    break;
                }
                long postcode = (long) cell.getNumericCellValue();
                boolean isForward = row.getCell(1).getBooleanCellValue();
                boolean isReturn = row.getCell(2).getBooleanCellValue();
                Base.addBatch(ps, postcode, id, isForward, isReturn);
            }
            Base.executeBatch(ps);
            ps.close();
            return MapResponse.success();
        } catch (Exception e) {
            e.printStackTrace();
            return MapResponse.failure(e.getMessage());
        }
    }

    public MapResponse courier(String courier, String display, String tracking) {
        CachedSelect.dropCache("courier");
        Courier c = new Courier(courier, display, tracking);
        return c.insert() ? MapResponse.success() : MapResponse.failure();
    }

    public MapResponse courier(long id, String courier, String display, String tracking) {
        CachedSelect.dropCache("courier");
        Courier c = Courier.findById(id);
        c.set("courier", courier);
        c.set("display", display);
        c.set("tracking_url", tracking);
        return c.saveIt() ? MapResponse.success() : MapResponse.failure();
    }

    public static MapResponse getServiceable(long saleOrderId) {
        return null;
    }
}
