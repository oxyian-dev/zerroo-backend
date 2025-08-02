package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.CompositePK;

@CompositePK({ "combo_id", "combo_group_id" })
public class ComboGroupMapping extends Model {
    public ComboGroupMapping() {
    }

    public ComboGroupMapping(long combo, long group, int quantity) {
        set("combo_id", combo);
        set("combo_group_id", group);
        set("quantity", quantity);
    }
}
