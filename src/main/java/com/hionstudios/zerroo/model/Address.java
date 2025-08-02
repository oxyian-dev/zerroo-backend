package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

public class Address extends Model {
    public static Address construct(
            Address a,
            String name,
            String firstname,
            String lastname,
            String phone,
            String altPhone,
            String email,
            String address1,
            String address2,
            String postcode,
            String landmark,
            String city,
            String state,
            boolean isDefault) {
        a.set("saved_name", name);
        a.set("firstname", firstname);
        a.set("lastname", lastname);
        a.set("phone", phone);
        a.set("alt_phone", altPhone);
        a.set("email", email);
        a.set("address_1", address1);
        a.set("address_2", address2);
        a.set("postcode", postcode);
        if ("".equals(landmark)) {
            a.set("landmark", landmark);
        }
        a.set("city", city);
        a.set("state", state);
        a.set("is_default", isDefault);
        return a;
    }
}
