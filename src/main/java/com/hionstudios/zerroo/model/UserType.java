package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Cached;

@Cached
public class UserType extends Model {
    public static final String DISTRIBUTOR = "Distributor";
    public static final String ORGANISATION_USER = "Organisation User";

    public static final int getId(String type) {
        return UserType.findFirst("type = ?", type).getInteger("id");
    }
}
