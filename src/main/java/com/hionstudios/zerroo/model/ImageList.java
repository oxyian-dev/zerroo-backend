package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

public class ImageList extends Model {
    public ImageList(String name) {
        set("name", name);
    }

    public ImageList() {
    }
}
