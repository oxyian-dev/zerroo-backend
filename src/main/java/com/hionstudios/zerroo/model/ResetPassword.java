package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

import com.hionstudios.time.TimeUtil;

public class ResetPassword extends Model {
    public ResetPassword() {
    }

    public ResetPassword(String username) {
        set("username", username);
        set("expiry", TimeUtil.currentTime() + 25 * 60 * 1000);
    }
}
