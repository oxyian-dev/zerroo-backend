package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

import com.hionstudios.CommonUtil;

public class SaleOrderItem extends Model {
    public SaleOrderItem() {
    }

    public SaleOrderItem(
            long saleOrderId,
            long itemId,
            Long comboId,
            double mrp,
            double price,
            double cost,
            int gstPercent,
            double pv,
            boolean isTn) {
        set("order_id", saleOrderId);
        set("item_id", itemId);
        set("combo_id", comboId);
        set("mrp", mrp);
        set("price", price);
        set("cost", cost);
        set("gst_percent", gstPercent);
        double basic = CommonUtil.removeGst(price, gstPercent);
        set("basic", basic);
        double gst = price - basic;
        set("gst", price - basic);
        if (isTn) {
            double halfGst = gst / 2;
            set("c_gst", halfGst);
            set("s_gst", halfGst);
        } else {
            set("i_gst", gst);
        }
        set("pv", pv);
        set("status_id", SaleOrderItemStatuses.getId(SaleOrderItemStatuses.UN_SHIPPED));
    }
}
