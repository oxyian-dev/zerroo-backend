package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.CompositePK;

@CompositePK({"user_id", "role_id"})
public class UserRole extends Model {
    public UserRole() {
    }

    public UserRole(long user, long role) {
        set("user_id", user);
        set("role_id", role);
    }
}
