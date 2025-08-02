package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

import com.hionstudios.db.Handler;
import com.hionstudios.iam.UserUtil;
import com.hionstudios.time.TimeUtil;

public class StockLedger extends Model {
    public StockLedger() {
    }

    public StockLedger(long item, long inventory, int quantity, String type) {
        set("item_id", item);
        set("inventory_id", inventory);
        set("quantity", quantity);
        Integer openingQuantity = Handler.getInt("Select Quantity From Stocks Where Item_Id = ? And Inventory_Id = ?",
                item, inventory);
        if (openingQuantity == null) {
            openingQuantity = 0;
        }
        set("opening_quantity", openingQuantity);
        set("closing_quantity", openingQuantity + quantity);
        set("type_id", StockLedgerType.getId(type));
        set("time", TimeUtil.currentTime());
        set("owner_id", UserUtil.getUserid());
    }
}
