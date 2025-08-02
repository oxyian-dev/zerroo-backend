package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Cached;

@Cached
public class OtpType extends Model {
    public static final String SMS = "SMS";
    public static final String EMAIL = "Email";

    public static int getId(String type) {
        return OtpType.findFirst("type = ?", type)
                .getInteger("id");
    }
}
