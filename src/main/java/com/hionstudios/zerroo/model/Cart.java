package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

public class Cart extends Model {
    public Cart() {
    }

    public Cart(long user, long item, int quantity, long time) {
        set("user_id", user);
        set("item_id", item);
        set("quantity", quantity);
        set("time", time);
    }

    public Cart(String unique, long combo, long group, long user, long item, long time) {
        set("unique_id", unique);
        set("combo_id", combo);
        set("combo_group_id", group);
        set("user_id", user);
        set("item_id", item);
        set("quantity", 1);
        set("time", time);
    }
}
