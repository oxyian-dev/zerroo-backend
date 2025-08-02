package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

import com.hionstudios.time.TimeUtil;

public class Otp extends Model {
    private static final long _10_MINUTES = 10 * 60 * 1000;

    public Otp() {
    }

    public Otp(long userid, String otp, String type) {
        long time = TimeUtil.currentTime();
        set("otp", otp);
        set("distributor_id", userid);
        set("type_id", OtpType.getId(type));
        set("time", time);
        set("expiry", time + _10_MINUTES);
    }
}
