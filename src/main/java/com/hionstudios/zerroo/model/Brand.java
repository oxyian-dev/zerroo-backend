package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

public class Brand extends Model {
    public Brand() {
    }

    public Brand(String brand) {
        set("brand", brand);
    }
}
