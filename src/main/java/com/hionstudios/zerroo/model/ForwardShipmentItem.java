package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

import com.hionstudios.MapResponse;

public class ForwardShipmentItem extends Model {
    public ForwardShipmentItem() {
    }

    public ForwardShipmentItem(MapResponse item, long shipmentId) {
        set("shipment_id", shipmentId);
        set("item_id", item.get("item_id"));
        set("combo_id", item.get("combo_id"));
        set("mrp", item.get("mrp"));
        set("price", item.get("price"));
        set("cost", item.get("cost"));
        set("gst_percent", item.get("gst_percent"));
        set("basic", item.get("basic"));
        set("gst", item.get("gst"));
        set("c_gst", item.get("c_gst"));
        set("s_gst", item.get("s_gst"));
        set("i_gst", item.get("i_gst"));
        set("pv", item.get("pv"));
    }
}
