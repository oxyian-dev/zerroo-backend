package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

public class TransferItem extends Model {
    public TransferItem() {
    }

    public TransferItem(long id, long item, int quantity, long from, long to) {
        set("transfer_id", id);
        set("item_id", item);
        set("quantity", quantity);
        set("from_inventory", from);
        set("to_inventory", to);
    }
}
