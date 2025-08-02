package com.hionstudios.zerroo.flow.sale;

import java.util.HashMap;
import java.util.List;

import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.Handler;
import com.hionstudios.db.SqlCriteria;
import com.hionstudios.db.SqlQuery;
import com.hionstudios.db.SqlUtil;
import com.hionstudios.zerroo.model.ForwardShipment;
import com.hionstudios.zerroo.model.ForwardShipmentItem;
import com.hionstudios.zerroo.model.Invoice;
import com.hionstudios.zerroo.model.InvoiceItem;
import com.hionstudios.zerroo.model.SaleOrder;
import com.hionstudios.zerroo.model.SaleOrderItem;
import com.hionstudios.zerroo.model.SaleOrderItemStatuses;
import com.hionstudios.zerroo.model.SaleOrderShippingStatus;

public class SaleOrderTransaction {
        public MapResponse saleOrders(DataGridParams params) {
                String sql = "Select Sale_Orders.Id, Sale_Orders.Time, sale_Orders.Order_Id \"Order Id\", Users.Username ZID, Users.Firstname Customer, Sale_Order_Shipping_Statuses.Status, Sum(Sale_Order_Items.Price) Price, Sale_Orders.Shipping_Fee \"Shipping Fee\", Sum(Sale_Order_Items.Pv) PV From Sale_Orders Join Sale_Order_Shipping_Statuses On Sale_Order_Shipping_Statuses.Id = Sale_Orders.Shipping_Status_Id Join Sale_Order_Items On Sale_Order_Items.Order_Id = Sale_Orders.Id Join Users On Users.Id = Sale_Orders.User_Id";
                String count = "Select Count(*) From (Select Count(Distinct Sale_Orders.Id) From Sale_Orders Join Sale_Order_Items On Sale_Order_Items.Order_Id = Sale_Orders.Id Join Users On Users.Id = Sale_Orders.User_Id Group By Sale_Orders.Id) Count";
                String[] columns = {
                                "Time",
                                "Order Id",
                                "ZID",
                                "Customer",
                                "Status",
                                "Price",
                                "Shipping Fee",
                                "PV"
                };
                HashMap<String, String> mapping = new HashMap<>(3);
                mapping.put("ZID", "Users.Username");
                mapping.put("Order Id", "Sale_Orders.Order_Id");
                mapping.put("Shipping Status", "Sale_Order_Shipping_Statuses.Status");
                mapping.put("Customer", "Users.Firstname");
                for (int i = 0; i < params.filterColumn.length; i++) {
                        params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
                }
                String search = params.getSearch();
                SqlCriteria customCriteria = search == null ? null
                                : new SqlCriteria("(Sale_Orders.Order_Id iLike ?)", search);
                SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true,
                                "Group By 1, 2, 3, 4, 5, 6");
                SqlCriteria filter = SqlUtil.constructCriteria(params, null, false);
                return Handler.toDataGrid(
                                new SqlQuery(sql, criteria),
                                new SqlQuery(count, filter),
                                columns);
        }

        public MapResponse unShippedSaleOrders(DataGridParams params) {
                String sql = "Select Sale_Orders.Id, Sale_Orders.Id \"Action\", Sale_Orders.Time, sale_Orders.Order_Id \"Order Id\", Users.Username ZID, Users.Firstname Customer, Sum(Sale_Order_Items.Price) Price, Sale_Orders.Shipping_Fee \"Shipping Fee\", Sum(Sale_Order_Items.Pv) PV From Sale_Orders Join Sale_Order_Items On Sale_Order_Items.Order_Id = Sale_Orders.Id Join Sale_Order_Shipping_Statuses On Sale_Order_Shipping_Statuses.Id = Sale_Orders.Shipping_Status_Id  Join Users On Users.Id = Sale_Orders.User_Id";
                String count = "Select Count(Distinct Sale_Orders.Id) From Sale_Orders Join Sale_Order_Items On Sale_Order_Items.Order_Id = Sale_Orders.Id Join Sale_Order_Shipping_Statuses On Sale_Order_Shipping_Statuses.Id = Sale_Orders.Shipping_Status_Id  Join Users On Users.Id = Sale_Orders.User_Id";
                String[] columns = {
                                "Action",
                                "Time",
                                "Order Id",
                                "ZID",
                                "Customer",
                                "Price",
                                "Shipping Fee",
                                "PV"
                };
                HashMap<String, String> mapping = new HashMap<>(2);
                mapping.put("ZID", "Users.Username");
                mapping.put("Order Id", "Sale_Orders.Order_Id");
                mapping.put("Customer", "Users.Firstname");
                for (int i = 0; i < params.filterColumn.length; i++) {
                        params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
                }
                String search = params.getSearch();
                SqlCriteria customCriteria = search == null
                                ? new SqlCriteria("Sale_Order_Shipping_Statuses.Status = ?",
                                                SaleOrderShippingStatus.UN_SHIPPED)
                                : new SqlCriteria(
                                                "(Sale_Order_Shipping_Statuses.Status = ? And Sale_Orders.Order_Id iLike ?)",
                                                SaleOrderShippingStatus.UN_SHIPPED, search);
                SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true,
                                "Group By 1, 2, 3, 4, 5, 6");
                SqlCriteria filter = SqlUtil.constructCriteria(params, customCriteria, false);
                return Handler.toDataGrid(
                                new SqlQuery(sql, criteria),
                                new SqlQuery(count, filter),
                                columns);
        }

        public MapResponse shippedSaleOrders(DataGridParams params) {
                String sql = "Select Sale_Orders.Id, Sale_Orders.Time, Sale_Orders.Order_Id \"Order Id\", Users.Username ZID, Users.Firstname Customer, Sum(Sale_Order_Items.Price) Price, Sale_Orders.Shipping_Fee \"Shipping Fee\", Sum(Sale_Order_Items.Pv) PV From Sale_Orders Join Sale_Order_Shipping_Statuses On Sale_Order_Shipping_Statuses.Id = Sale_Orders.Shipping_Status_Id Join Sale_Order_Items On Sale_Order_Items.Order_Id = Sale_Orders.Id Join Users On Users.Id = Sale_Orders.User_Id Join Forward_Shipments On Forward_Shipments.Id = Sale_Orders.Shipment_Id";
                String count = "Select Count(Distinct Sale_Orders.Id) From Sale_Orders Join Sale_Order_Items On Sale_Order_Items.Order_Id = Sale_Orders.Id Join Sale_Order_Shipping_Statuses On Sale_Order_Shipping_Statuses.Id = Sale_Orders.Shipping_Status_Id Join Users On Users.Id = Sale_Orders.User_Id Join Forward_Shipments On Forward_Shipments.Id = Sale_Orders.Shipment_Id";
                String[] columns = {
                                "Time",
                                "Order Id",
                                "ZID",
                                "Customer",
                                "Price",
                                "Shipping Fee",
                                "PV"
                };
                HashMap<String, String> mapping = new HashMap<>(3);
                mapping.put("Order Id", "Sale_Orders.Order_Id");
                mapping.put("ZID", "Users.Username");
                mapping.put("Customer", "Users.Firstname");
                for (int i = 0; i < params.filterColumn.length; i++) {
                        params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
                }
                String search = params.getSearch();
                SqlCriteria customCriteria = search == null
                                ? new SqlCriteria("Sale_Order_Shipping_Statuses.Status = ?",
                                                SaleOrderShippingStatus.SHIPPED)
                                : new SqlCriteria(
                                                "(Sale_Order_Shipping_Statuses.Status = ? And Sale_Orders.Order_Id iLike ?)",
                                                SaleOrderShippingStatus.SHIPPED, search);
                SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true,
                                "Group By 1, 2, 3, 4, 5");
                SqlCriteria filter = SqlUtil.constructCriteria(params, customCriteria, false);

                return Handler.toDataGrid(
                                new SqlQuery(sql, criteria),
                                new SqlQuery(count, filter),
                                columns);
        }

        public MapResponse saleOrders(long id, DataGridParams params) {
                String sql = "Select Sale_Orders.Id, Sale_Orders.Time, sale_Orders.Order_Id \"Order Id\", Sale_Order_Shipping_Statuses.Status \"Shipping Status\", Sum(Sale_Order_Items.Price) Price, Sale_Orders.Shipping_Fee \"Shipping Fee\", Sum(Sale_Order_Items.Pv) PV From Sale_Orders Join Sale_Order_Shipping_Statuses On Sale_Order_Shipping_Statuses.Id = Sale_Orders.Shipping_Status_Id Join Sale_Order_Items On Sale_Order_Items.Order_Id = Sale_Orders.Id";
                String count = "Select Count(*) From Sale_Orders Join Sale_Order_Items On Sale_Order_Items.Order_Id = Sale_Orders.Id";
                String[] columns = {
                                "Time",
                                "Order Id",
                                "Shipping Status",
                                "Price",
                                "Shipping Fee",
                                "PV"
                };
                HashMap<String, String> mapping = new HashMap<>(4);
                mapping.put("Order Id", "Sale_Orders.Order_Id");
                mapping.put("Shipping Status", "Sale_Order_Shipping_Statuses.Status");
                for (int i = 0; i < params.filterColumn.length; i++) {
                        params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
                }

                SqlCriteria customCriteria = new SqlCriteria("(Sale_Orders.User_Id = ?)", id);
                SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true,
                                "Group By 1, 2, 3, 4");
                SqlCriteria filter = SqlUtil.constructCriteria(params, null, false, "Group By Sale_Orders.*");
                return Handler.toDataGrid(
                                new SqlQuery(sql, criteria),
                                new SqlQuery(count, filter),
                                columns);
        }

        public MapResponse get(long id) {
                String sql = "Select Sale_Orders.Id, Sale_Orders.Order_Id, Sale_Orders.Billing_Firstname, Sale_Orders.Billing_Lastname, Sale_Orders.Billing_Email, Sale_Orders.Billing_Phone, Sale_Orders.Billing_Address_1, Sale_Orders.Billing_Address_2, Sale_Orders.Billing_City, Sale_Orders.Billing_Postcode, Sale_Orders.Billing_State, Sale_Orders.Billing_Country, Sale_Orders.Shipping_FirstName, Sale_Orders.Shipping_Lastname, Sale_Orders.Shipping_Email, Sale_Orders.Shipping_Phone, Sale_Orders.Shipping_Address_1, Sale_Orders.Shipping_Address_2, Sale_Orders.Shipping_City, Sale_Orders.Shipping_Postcode, Sale_Orders.Shipping_State, Sale_Orders.Shipping_Country, Sale_Orders.Shipping_Fee, Sale_Orders.Time From Sale_Orders Where Sale_Orders.Id = ?";

                String items = "Select (Select Images.Image From Images Where Images.List_Id = Items.Image_Id Order by Index Asc Limit 1) Image, Items.Title, Items.Sku, Sale_Order_Items.Mrp, Sale_Order_Items.Price, Round(((Sale_Order_Items.Mrp - Sale_Order_Items.Price) / Sale_Order_Items.Mrp * 100)) Discount, Count(*) quantity From Sale_Order_Items Join Items On Items.Id = Sale_Order_Items.Item_Id Where Sale_Order_Items.Order_Id = ? Group By 1, 2, 3, 4, 5, 6";

                MapResponse response = Handler.findFirst(sql, id);
                response.put("items", Handler.findAll(items, id));
                return response;
        }

        public MapResponse ship(long id) {
                String sql = "Select * From Sale_Orders Where Id = ?";
                MapResponse order = Handler.findFirst(sql, id);
                ForwardShipment forwardShipment = new ForwardShipment(order);
                if (!forwardShipment.insert()) {
                        return MapResponse.failure("Try again");
                } else {
                        SaleOrder saleOrder = SaleOrder.findById(id);
                        Invoice invoice = new Invoice(order, saleOrder);
                        if (!invoice.insert()) {
                                return MapResponse.failure("Try again");
                        }
                        long invoiceId = invoice.getLongId();
                        forwardShipment.set("invoice_id", invoiceId);
                        forwardShipment.saveIt();
                        sql = "Select * From Sale_Order_Items Where Order_Id = ?";
                        List<MapResponse> items = Handler.findAll(sql, id);
                        long shipmentId = forwardShipment.getLongId();
                        for (int i = 0; i < items.size(); i++) {
                                MapResponse item = items.get(i);
                                ForwardShipmentItem shipmentItem = new ForwardShipmentItem(item, shipmentId);
                                shipmentItem.insert();
                                InvoiceItem invoiceItem = new InvoiceItem(item, invoiceId);
                                invoiceItem.insert();
                        }
                        SaleOrder.update(
                                        "Shipping_Status_Id = (Select Id From Sale_Order_Shipping_Statuses Where Sale_Order_Shipping_Statuses.Status = ?), Shipment_Id = ?",
                                        "Id = ?",
                                        SaleOrderShippingStatus.SHIPPED, shipmentId, id);
                        SaleOrderItem.update("Status_Id = (Select Id From Sale_Order_Item_Statuses Where Status = ?)",
                                        "Order_Id = ?",
                                        SaleOrderItemStatuses.SHIPPED, id);
                        return MapResponse.success();
                }
        }

        public MapResponse count() {
                String sql = "Select Count(*) Count, Sale_Order_Shipping_Statuses.Status From Sale_Orders Join Sale_Order_Shipping_Statuses On Sale_Order_Shipping_Statuses.Id = Sale_Orders.Shipping_Status_Id Group By Sale_Order_Shipping_Statuses.Status";
                return Handler.toKeyValue(sql, "status", "count");
        }
}
