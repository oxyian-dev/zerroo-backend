package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

public class PriceList extends Model {
    public PriceList() {
    }

    public static void construct(
            String name,
            String description,
            double mrp,
            double price,
            double cost,            
            Double pv,
            int gst,
            PriceList priceList) {
        priceList.set("name", name);
        priceList.set("description", description);
        priceList.set("mrp", mrp);
        priceList.set("price", price);
        priceList.set("cost", cost);
        priceList.set("pv", pv);
        priceList.set("gst_percent", gst);
    }
}
