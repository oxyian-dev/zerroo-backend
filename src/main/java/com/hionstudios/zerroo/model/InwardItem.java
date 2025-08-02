package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

public class InwardItem extends Model {
    public InwardItem() {
    }

    public InwardItem(long item, long inward, int quantity, long inventory) {
        set("item_id", item);
        set("inward_id", inward);
        set("quantity", quantity);
        set("inventory_id", inventory);
    }
}
