package com.hionstudios.zerroo.flow.sale;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.hionstudios.ListResponse;
import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.Handler;
import com.hionstudios.db.SqlCriteria;
import com.hionstudios.db.SqlQuery;
import com.hionstudios.db.SqlUtil;
import com.hionstudios.iam.UserUtil;
import com.hionstudios.zerroo.model.ForwardShipment;
import com.hionstudios.zerroo.model.ForwardShipmentHistory;
import com.hionstudios.zerroo.model.ForwardShipmentItem;
import com.hionstudios.zerroo.model.ForwardShipmentStatus;
import com.hionstudios.zerroo.model.Invoice;
import com.hionstudios.zerroo.model.InvoiceItem;
import com.hionstudios.zerroo.model.SaleOrder;
import com.hionstudios.zerroo.model.SaleOrderItem;
import com.hionstudios.zerroo.model.SaleOrderItemStatuses;
import com.hionstudios.zerroo.model.SaleOrderShippingStatus;
import com.hionstudios.time.TimeUtil;

public class ShipmentTransaction {
    public MapResponse allShipments(DataGridParams params) {
        String sql = "Select Forward_Shipments.Id, Users.Username ZID, Users.Firstname \"Name\", Forward_Shipments.Time, Forward_Shipment_Statuses.Status \"Shipping Status\", Transporters.Transporter, Couriers.Courier, Replace(Couriers.Tracking_Url, '${awb}', Forward_Shipments.awb) Track From Forward_Shipments Join Forward_Shipment_Statuses On Forward_Shipment_Statuses.Id = Forward_Shipments.Status_Id Join Users On Users.Id = Forward_Shipments.User_Id Left Join Transporters On Transporters.Id = Forward_Shipments.Transporter_Id Left Join Couriers On Couriers.Id = Forward_Shipments.Courier_Id";

        String count = "Select Count(*) From Forward_Shipments Join Forward_Shipment_Statuses On Forward_Shipment_Statuses.Id = Forward_Shipments.Status_Id Left Join Transporters On Transporters.Id = Forward_Shipments.Transporter_Id Left Join Couriers On Couriers.Id = Forward_Shipments.Courier_Id";

        String[] columns = {
                "ZID",
                "Name",
                "Time",
                "Shipping Status",
                "Transporter",
                "Courier",
                "Track"
        };
        HashMap<String, String> mapping = new HashMap<>(2);
        mapping.put("ZID", "Users.Username");
        mapping.put("Name", "Users.Firstname");
        mapping.put("Shipping Status", "Forward_Shipment_Statuses.Status");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
        }
        SqlCriteria criteria = SqlUtil.constructCriteria(params, null, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }

    public MapResponse statusShipments(String status, DataGridParams params) {
        String sql = "Select Forward_Shipments.Id, Users.Username ZID, Users.Firstname \"Name\", Forward_Shipments.Time, Forward_Shipment_Statuses.Status \"Shipping Status\", Transporters.Transporter, Couriers.Courier, Replace(Couriers.Tracking_Url, '${awb}', Forward_Shipments.awb) Track From Forward_Shipments Join Forward_Shipment_Statuses On Forward_Shipment_Statuses.Id = Forward_Shipments.Status_Id Left Join Transporters On Transporters.Id = Forward_Shipments.Transporter_Id Left Join Couriers On Couriers.Id = Forward_Shipments.Courier_Id Join Users On Users.Id = Forward_Shipments.User_Id";

        String count = "Select Count(*) From Forward_Shipments Join Forward_Shipment_Statuses On Forward_Shipment_Statuses.Id = Forward_Shipments.Status_Id Left Join Transporters On Transporters.Id = Forward_Shipments.Transporter_Id Left Join Couriers On Couriers.Id = Forward_Shipments.Courier_Id Join Users On Users.Id = Forward_Shipments.User_Id";

        String[] columns = {
                "ZID",
                "Name",
                "Time",
                "Shipping Status",
                "Transporter",
                "Courier",
                "Track"
        };

        HashMap<String, String> mapping = new HashMap<>(3);
        mapping.put("ZID", "Users.Username");
        mapping.put("Name", "Users.Firstname");
        mapping.put("Shipping Status", "Forward_Shipment_Statuses.Status");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
        }
        SqlCriteria customCriteria = new SqlCriteria("Forward_Shipment_Statuses.Status = ?", status);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params,
                new SqlCriteria("Forward_Shipment_Statuses.Status = ?", status));
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }

    public MapResponse shipments(String status, DataGridParams params) {
        switch (status) {
            case "pending":
                return pending(params);
            case "processing":
                return statusShipments(ForwardShipmentStatus.PROCESSING, params);
            case "picked-up":
                return statusShipments(ForwardShipmentStatus.PICKED_UP, params);
            case "dispatched":
                return statusShipments(ForwardShipmentStatus.DISPATCHED, params);
            case "delivered":
                return statusShipments(ForwardShipmentStatus.DELIVERED, params);
            case "rto-pending":
                return statusShipments(ForwardShipmentStatus.RTO_PENDING, params);
            case "rto-returned":
                return statusShipments(ForwardShipmentStatus.RTO_RETURNED, params);
            case "lost":
                return statusShipments(ForwardShipmentStatus.LOST, params);
            case "exception":
                return statusShipments(ForwardShipmentStatus.EXCEPTION, params);
            case "error":
                return statusShipments(ForwardShipmentStatus.ERROR, params);
            default:
                return statusShipments(status, params);

        }
    }

    private MapResponse pending(DataGridParams params) {
        String sql = "Select Invoices.Id, Forward_Shipments.Id Dispatch, Invoices.Id Download, Forward_Shipments.Time, Users.Username ZID, Forward_Shipments.Firstname Customer, Forward_Shipments.City, Forward_Shipments.State From Forward_Shipments Join Forward_Shipment_Statuses On Forward_Shipment_Statuses.Id = Forward_Shipments.Status_Id Join Invoices On Invoices.Id = Forward_Shipments.Invoice_Id Join Users On Users.Id = Forward_Shipments.User_Id";

        String count = "Select Count(*) From Forward_Shipments Join Forward_Shipment_Statuses On Forward_Shipment_Statuses.Id = Forward_Shipments.Status_Id Join Invoices On Invoices.Id = Forward_Shipments.Invoice_Id Join Users On Users.Id = Forward_Shipments.User_Id";

        String[] columns = {
                "Dispatch",
                "Download",
                "Time",
                "ZID",
                "Customer",
                "City",
                "State"
        };
        HashMap<String, String> mapping = new HashMap<>(1);
        mapping.put("Customer", "Forward_Shipments.Firstname");
        mapping.put("ZID", "Users.Username");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
        }
        SqlCriteria customCriteria = new SqlCriteria("Forward_Shipment_Statuses.Status = ?",
                ForwardShipmentStatus.PENDING);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params,
                new SqlCriteria("Forward_Shipment_Statuses.Status = ?", ForwardShipmentStatus.PENDING));
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }

    public MapResponse count() {
        String sql = "Select Count(*) Count, Forward_Shipment_Statuses.Status From Forward_Shipments Join Forward_Shipment_Statuses On Forward_Shipment_Statuses.Id = Forward_Shipments.Status_Id Group By Forward_Shipment_Statuses.Status";
        return Handler.toKeyValue(sql, "status", "count");
    }

    /**
     * Generate Shipmets for
     * 1. Orders having combo
     * 2. Orders who have paid 100 for the Shipping Fee
     * 
     * These orders will be merged in a single Shipment
     * 
     * @return status & Count
     */
    public MapResponse generate() {
        String sql = "Select Array_Agg(Sale_Orders.Id) Sale_Order_Id, Sale_Orders.User_Id, Sale_Orders.Shipping_Firstname, Sale_Orders.Shipping_Lastname, Sale_Orders.Shipping_Email, Sale_Orders.Shipping_Phone, Sale_Orders.Shipping_Alt_Phone, Sale_Orders.Shipping_Address_1, Sale_Orders.Shipping_Address_2, Sale_Orders.Shipping_Landmark, Sale_Orders.Shipping_Postcode, Sale_Orders.Shipping_City, Sale_Orders.Shipping_State, Sale_Orders.Shipping_Country, Sale_Orders.Shipping_Latitude, Sale_Orders.Shipping_Longitude, Array_Agg(Json_Build_Object('item_id', Sale_Order_Items.Item_Id, 'combo_id', Sale_Order_Items.Combo_Id, 'mrp', Sale_Order_Items.Mrp, 'price', Sale_Order_Items.Price, 'cost', Sale_Order_Items.Cost, 'gst_percent', Sale_Order_Items.Gst_Percent, 'basic', Sale_Order_Items.Basic, 'gst', Sale_Order_Items.Gst, 'c_gst', Sale_Order_Items.C_Gst, Sale_Order_Items.s_gst, Sale_Order_Items.S_Gst, 'i_gst', Sale_Order_Items.I_Gst, 'pv', Sale_Order_Items.Pv)) Items, Sum(Sale_Orders.Shipping_Fee) Shipping_Fee, Sum(Sale_Orders.Shipping_Basic) Shipping_Basic, Sum(Sale_Orders.Shipping_Gst_Percent) Shipping_Gst_Percent, Sum(Sale_Orders.Shipping_Gst) Shipping_Gst, Sum(Sale_Orders.Shipping_C_Gst) Shipping_C_Gst, Sum(Sale_Orders.Shipping_S_Gst) Shipping_S_Gst, Sum(Sale_Orders.Shipping_I_Gst) Shipping_I_Gst, Min(Sale_Orders.Shipping_Sac) Shipping_Sac From Sale_Orders Join Users On Users.Id = Sale_Orders.User_Id Join Sale_Order_Items On Sale_Order_Items.Order_Id = Sale_Orders.Id Join Sale_Order_Shipping_Statuses On Sale_Order_Shipping_Statuses.Id = Sale_Orders.Shipping_Status_Id And Sale_Order_Shipping_Statuses.Status = ? Group By Users.Id, Sale_Orders.Shipping_Firstname, Sale_Orders.Shipping_Lastname, Sale_Orders.Shipping_Email, Sale_Orders.Shipping_Phone, Sale_Orders.Shipping_Alt_Phone, Sale_Orders.Shipping_Address_1, Sale_Orders.Shipping_Address_2, Sale_Orders.Shipping_Landmark, Sale_Orders.Shipping_Postcode, Sale_Orders.Shipping_City, Sale_Orders.Shipping_State, Sale_Orders.Shipping_Country, Sale_Orders.Shipping_Latitude, Sale_Orders.Shipping_Longitude Having Max(Sale_Orders.Shipping_Fee) > 0";

        List<MapResponse> orders = Handler.findAll(sql, SaleOrderShippingStatus.UN_SHIPPED);
        for (MapResponse order : orders) {
            ForwardShipment forwardShipment = new ForwardShipment(order);
            boolean status = forwardShipment.insert();
            if (status) {
                ListResponse items = order.getList("items");
                long shipmentId = forwardShipment.getLongId();
                ListResponse orderIds = order.getList("sale_order_id");
                long firstOrderId = orderIds.getLong(0);

                SaleOrder saleOrder = SaleOrder.findById(firstOrderId);
                Invoice invoice = new Invoice(order, saleOrder);
                if (!invoice.insert()) {
                    return MapResponse.failure("Try again");
                }
                long invoiceId = invoice.getLongId();
                forwardShipment.set("invoice_id", invoiceId);
                forwardShipment.saveIt();
                for (int i = 0; i < items.size(); i++) {
                    MapResponse item = items.getMap(i);
                    ForwardShipmentItem shipmentItem = new ForwardShipmentItem(item, shipmentId);
                    shipmentItem.insert();
                    InvoiceItem invoiceItem = new InvoiceItem(item, invoiceId);
                    invoiceItem.insert();
                }

                HashSet<?> uniqueOrderIds = new HashSet<>(orderIds);
                for (Object orderId : uniqueOrderIds) {
                    SaleOrder.update(
                            "Shipping_Status_Id = (Select Id From Sale_Order_Shipping_Statuses Where Sale_Order_Shipping_Statuses.Status = ?), Shipment_Id = ?",
                            "Id = ?",
                            SaleOrderShippingStatus.SHIPPED, shipmentId, orderId);
                    SaleOrderItem.update("Status_Id = (Select Id From Sale_Order_Item_Statuses Where Status = ?)",
                            "Order_Id = ?",
                            SaleOrderItemStatuses.SHIPPED, orderId);
                }
            }
        }
        MapResponse response = MapResponse.success();
        response.put("count", orders.size());
        return response;
    }

    public MapResponse get(long id) {
        String sql = "Select Forward_Shipments.Id, Forward_Shipments.Transporter_id Transporter, Forward_Shipments.Courier_Id Courier, Forward_Shipments.Awb, Forward_Shipments.Weight, Forward_Shipments.Length, Forward_Shipments.Breadth, Forward_Shipments.Height From Forward_Shipments Where Forward_Shipments.Id = ?";
        return Handler.findFirst(sql, id);
    }

    public MapResponse dispatch(
            long id,
            Long transporter,
            Long courier,
            String awb,
            Double weight,
            Double length,
            Double breadth,
            Double height) {
        long userid = UserUtil.getUserid();
        long time = TimeUtil.currentTime();
        ForwardShipment forwardShipment = ForwardShipment.findById(id);

        Long oldTransporter = forwardShipment.getLong("transporter_id");
        if (!transporter.equals(oldTransporter)) {
            addHistory(id, "Transporter", oldTransporter, transporter, userid, null, time);
            forwardShipment.set("transporter_id", transporter);
        }

        Long oldCourier = forwardShipment.getLong("courier_id");
        if (!courier.equals(oldCourier)) {
            addHistory(id, "Courier", oldCourier, courier, userid, null, time);
            forwardShipment.set("courier_id", courier);
        }

        String oldAwb = forwardShipment.getString("awb");
        if (!awb.equals(oldAwb)) {
            addHistory(id, "AWB", oldAwb, awb, userid, null, time);
            forwardShipment.set("awb", awb);
        }

        forwardShipment.set("weight", weight);
        forwardShipment.set("length", length);
        forwardShipment.set("breadth", breadth);
        forwardShipment.set("height", height);

        Integer oldStatusId = forwardShipment.getInteger("status_id");
        Integer status = ForwardShipmentStatus.getId(ForwardShipmentStatus.DISPATCHED);

        if (!status.equals(oldStatusId)) {
            addHistory(id, "Status", oldStatusId, status, userid, null, time);
            forwardShipment.set("status_id", status);
        }

        SaleOrderItem.update("Status_Id = (Select Id From Sale_Order_Item_Statuses Where Status = ?)",
                "Order_Id In (Select Id From Sale_Orders Where Shipment_Id = ?)",
                SaleOrderItemStatuses.DISPATCHED,
                id);
        forwardShipment.saveIt();
        return MapResponse.success();
    }

    private void addHistory(
            long id,
            String field,
            Object oldValue,
            Object newValue,
            long userid,
            String reason,
            long time) {
        ForwardShipmentHistory history = new ForwardShipmentHistory();
        history.set("shipment_id", id);
        history.set("field", field);
        history.set("old_value", String.valueOf(oldValue));
        history.set("new_value", String.valueOf(newValue));
        history.set("owner_id", userid);
        history.set("reason", reason);
        history.set("time", time);
        history.insert();
    }
}
