package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

public class Color extends Model {
    public static Color construct(String color, String hex, Color c) {
        hex = hex.replace("#", "");
        c.set("color", color);
        c.set("hex", hex);
        return c;
    }
}
