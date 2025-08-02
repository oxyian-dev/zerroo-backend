package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.CompositePK;

@CompositePK({ "list_id", "image" })
public class Image extends Model {
    public Image() {
    }

    public Image(long list, String img) {
        set("list_id", list);
        set("image", img);
    }
}
