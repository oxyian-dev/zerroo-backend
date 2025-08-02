package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

public class AdjustmentItem extends Model {
    public AdjustmentItem() {
    }

    public AdjustmentItem(long id, long item, long initial, int finalQuantity, long adjustment) {
        set("adjustment_id", id);
        set("item_id", item);
        set("initial", initial);
        set("final", finalQuantity);
        set("adjustment", adjustment);
    }
}
