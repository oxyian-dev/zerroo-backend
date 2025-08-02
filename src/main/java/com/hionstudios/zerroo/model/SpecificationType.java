package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

public class SpecificationType extends Model {
    public SpecificationType() {
    }

    public SpecificationType(String type) {
        set("specification", type);
    }
}
