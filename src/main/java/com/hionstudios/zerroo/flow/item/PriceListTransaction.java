package com.hionstudios.zerroo.flow.item;

import java.util.HashMap;

import com.hionstudios.CachedSelect;
import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.Handler;
import com.hionstudios.db.SqlCriteria;
import com.hionstudios.db.SqlQuery;
import com.hionstudios.db.SqlUtil;
import com.hionstudios.iam.UserUtil;
import com.hionstudios.zerroo.model.PriceList;
import com.hionstudios.time.TimeUtil;

public class PriceListTransaction {
    public MapResponse view(long id) {
        return Handler.findFirst("Select * From Price_Lists Where Id = ?", id);
    }

    public MapResponse view(DataGridParams params) {
        String sql = "Select Price_Lists.Id, Price_Lists.Id \"Action\", Price_Lists.Name, Price_Lists.Mrp, Price_Lists.Price, Price_Lists.Cost, Price_Lists.Gst_Percent \"Gst\", Price_Lists.Pv \"PV\", (Select Count(*) From Items Where Items.Price_Id = Price_Lists.Id) Items From Price_Lists";
        String count = "Select Count(*) From Price_Lists";

        HashMap<String, String> mapping = new HashMap<>(6);
        mapping.put("Name", "Price_Lists.Name");
        mapping.put("Mrp", "Price_Lists.Mrp");
        mapping.put("Price", "Price_Lists.Price");
        mapping.put("Cost", "Price_Lists.Cost");
        mapping.put("Gst", "Price_Lists.Gst_Percent");
        mapping.put("PV", "Price_Lists.Pv");

        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
        }

        SqlCriteria criteria = SqlUtil.constructCriteria(params, null, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params);
        String[] columns = {
                "Action",
                "Name",
                "Mrp",
                "Price",
                "Cost",
                "Gst",
                "PV",
                "Items"
        };
        return Handler.toDataGrid(new SqlQuery(sql, criteria), new SqlQuery(count, filter), columns);
    }

    public MapResponse add(
            String name,
            String description,
            double mrp,
            double price,
            double cost,
            int gst,
            Double pv) {
        PriceList priceList = new PriceList();
        PriceList.construct(name, description, mrp, price, cost, pv, gst, priceList);
        priceList.set("created_time", TimeUtil.currentTime());
        priceList.set("created_by", UserUtil.getUserid());
        CachedSelect.dropCache("price");
        return priceList.insert() ? MapResponse.success() : MapResponse.failure();
    }

    public MapResponse edit(
            long id,
            String name,
            String description,
            double mrp,
            double price,
            double cost,
            int gst,
            Double pv) {
        PriceList priceList = PriceList.findById(id);
        PriceList.construct(name, description, mrp, price, cost, pv, gst, priceList);
        priceList.set("last_modified_time", TimeUtil.currentTime());
        priceList.set("last_modified_by", UserUtil.getUserid());
        CachedSelect.dropCache("price");
        return priceList.saveIt() ? MapResponse.success() : MapResponse.failure();
    }
}
