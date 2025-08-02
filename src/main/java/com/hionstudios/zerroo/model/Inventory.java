package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Cached;

@Cached
public class Inventory extends Model {
    public static final String ZERROO_CHENNAI = "Zerroo Chennai";

    public Inventory() {
    }

    public Inventory(
            String inventory,
            String contact,
            String phone,
            String address1,
            String address2,
            String postcode,
            String landmark,
            String city,
            String state,
            long branch) {
        set("inventory", inventory);
        set("contact_name", contact);
        set("phone", phone);
        set("address_1", address1);
        set("address_2", address2);
        set("postcode", postcode);
        set("landmark", landmark);
        set("city", city);
        set("state", state);
        set("branch_id", branch);
    }

    public static long getId(String inventoryName) {
        Inventory inventory = Inventory.findFirst("inventory = ?", inventoryName);
        return inventory.getLongId();
    }

    public static long getDefaultInventory() {
        return getId(ZERROO_CHENNAI);
    }
}
