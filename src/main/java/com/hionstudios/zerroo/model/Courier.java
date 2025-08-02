package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

public class Courier extends Model {
    public Courier() {
    }

    public Courier(String courier, String display, String tracking) {
        set("courier", courier);
        set("display", display);
        set("tracking_url", tracking);
    }
}
