package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.CompositePK;

@CompositePK({ "group_id", "item_group_id" })
public class ComboGroupItem extends Model {
    public ComboGroupItem() {
    }

    public ComboGroupItem(long comboGroupId, long itemGroupId) {
        set("group_id", comboGroupId);
        set("item_group_id", itemGroupId);
    }
}
