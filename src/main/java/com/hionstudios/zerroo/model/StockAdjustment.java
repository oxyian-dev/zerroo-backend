package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

import com.hionstudios.iam.UserUtil;
import com.hionstudios.time.TimeUtil;

public class StockAdjustment extends Model {
    public StockAdjustment() {
    }

    public StockAdjustment(String description, String reason, long inventory) {
        set("description", description);
        set("reason", reason);
        set("inventory_id", inventory);
        set("time", TimeUtil.currentTime());
        set("owner_id", UserUtil.getUserid());
    }
}
