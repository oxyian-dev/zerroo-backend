package com.hionstudios.zerroo.model;

import com.hionstudios.iam.UserUtil;
import com.hionstudios.time.TimeUtil;

import org.javalite.activejdbc.Model;

public class ItemHistory extends Model {
    public ItemHistory() {
    }

    public ItemHistory(long id, String field, Object oldValue, Object newValue, String reason) {
        set("item_id", id);
        set("field", field);
        set("old_value", String.valueOf(oldValue));
        set("new_value", String.valueOf(newValue));
        set("reason", reason);
        set("time", TimeUtil.currentTime());
        set("owner_id", UserUtil.getUserid());
    }
}
