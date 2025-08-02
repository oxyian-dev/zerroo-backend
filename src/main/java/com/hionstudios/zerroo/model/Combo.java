package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

public class Combo extends Model {
    public Combo() {
    }

    public Combo(String name, long category, String description, String image) {
        set("name", name);
        set("category_id", category);
        set("description", description);
        set("image", image);
    }
}
