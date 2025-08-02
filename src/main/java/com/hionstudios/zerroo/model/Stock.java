package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

public class Stock extends Model {
    public Stock() {
    }

    public Stock(long sku, long inventory, int quantity) {
        set("item_id", sku);
        set("inventory_id", inventory);
        set("quantity", quantity);
    }
}
