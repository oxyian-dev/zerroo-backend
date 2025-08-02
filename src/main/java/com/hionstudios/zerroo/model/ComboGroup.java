package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

public class ComboGroup extends Model {
    public ComboGroup() {
    }

    public ComboGroup(String name, String description, long price) {
        set("name", name);
        set("description", description);
        set("price_id", price);
    }
}
