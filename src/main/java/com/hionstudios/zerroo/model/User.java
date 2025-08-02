package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

import com.hionstudios.db.Handler;
import com.hionstudios.time.TimeUtil;

public class User extends Model {
    public User() {
        set("created_time", TimeUtil.currentTime());
    }

    public User(String firstname, String lastname, String phone, String email, String password) {
        this();
        long id = Handler.getLong(
                "Select Count(Users.id) + 1 From Users Join User_Types On User_Types.Id = Users.Type_Id And User_Types.Type = ?",
                UserType.DISTRIBUTOR);
        set("id", id);
        set("username", generate(id));
        set("firstname", firstname);
        set("lastname", lastname);
        set("phone", phone);
        set("email", email);
        set("password", password);
        set("type_id", UserType.getId(UserType.DISTRIBUTOR));
    }

    private static String generate(long id) {
        return "ZERR00" + id;
    }
}
