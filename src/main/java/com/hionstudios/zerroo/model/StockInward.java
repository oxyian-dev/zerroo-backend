package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

import com.hionstudios.iam.UserUtil;
import com.hionstudios.time.TimeUtil;

public class StockInward extends Model {
    public StockInward() {
    }

    public StockInward(String description, String ref) {
        set("description", description);
        set("ref_id", ref);
        set("owner_id", UserUtil.getUserid());
        set("time", TimeUtil.currentTime());
    }
}
