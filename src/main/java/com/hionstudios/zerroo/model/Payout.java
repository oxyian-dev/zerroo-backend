package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

import com.hionstudios.iam.UserUtil;
import com.hionstudios.time.TimeUtil;

public class Payout extends Model {
    public Payout() {
        set("created_time", TimeUtil.currentTime());
        set("created_by", UserUtil.getUserid());
        set("status_id", PayoutStatus.getId(PayoutStatus.PENDING));
    }
}
