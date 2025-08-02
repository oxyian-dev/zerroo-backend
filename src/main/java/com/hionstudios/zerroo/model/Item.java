package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

public class Item extends Model {
    public static void constructItem(
            long group,
            String sku,
            String hsn,
            String title,
            String description,
            long price,
            Long image,
            Long size,
            Long color,
            Double weight,
            Double length,
            Double breadth,
            Double height,
            Item item) {
        item.set("group_id", group);
        item.set("sku", sku);
        item.set("title", title);
        item.set("description", description);
        item.set("hsn", hsn);
        item.set("price_id", price);
        item.set("size_id", size);
        item.set("color_id", color);
        item.set("weight", weight);
        item.set("length", length);
        item.set("breadth", breadth);
        item.set("height", height);
        item.set("image_id", image);
    }
}
