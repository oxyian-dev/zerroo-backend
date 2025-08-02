package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Cached;

@Cached
public class Transporter extends Model {
    public static final String ZERROO = "Zerroo";

    public Transporter() {
    }

    public Transporter(String transporter, long inventory) {
        set("transporter", transporter);
        set("inventory_id", inventory);
    }

    public static long getId(String transporterName) {
        Transporter transporter = Transporter.findFirst("transporter = ?", transporterName);
        return transporter.getLongId();
    }
}
