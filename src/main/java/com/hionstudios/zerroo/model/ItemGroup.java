package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

public class ItemGroup extends Model {
    public ItemGroup() {
    }

    public ItemGroup(String name, long category, long brand, Long specification) {
        set("name", name);
        set("category_id", category);
        set("brand_id", brand);
        set("specification_id", specification);
    }
}
