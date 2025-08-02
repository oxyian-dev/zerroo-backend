package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

import com.hionstudios.iam.UserUtil;
import com.hionstudios.time.TimeUtil;

public class StockTransfer extends Model {
    public StockTransfer() {
    }

    public StockTransfer(String description, String reason) {
        set("description", description);
        set("reason", reason);
        set("time", TimeUtil.currentTime());
        set("owner_id", UserUtil.getUserid());
    }
}
