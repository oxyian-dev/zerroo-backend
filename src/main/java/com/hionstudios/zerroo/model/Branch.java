package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Cached;

import com.hionstudios.db.Handler;

@Cached
public class Branch extends Model {
    public static final String GSTIN_TAMIL_NADU = "33AADFZ7502M1ZX";

    public static int getIdFromGstin(String gstin) {
        Branch branch = Branch.findFirst("gstin = ?", gstin);
        return branch != null ? branch.getInteger("id") : getDefaultId();
    }

    public static int getDefaultId() {
        Long id = Handler.getLong("Select Id From Branches Order By Id Limit 1");
        if (id == null) {
            throw new IllegalStateException("No branches configured");
        }
        return id.intValue();
    }

    public Branch() {
    }

    public Branch(
            String branch,
            String sourceOfSupply,
            String phone,
            String email,
            String address1,
            String address2,
            String postcode,
            String landmark,
            String city,
            String state,
            String country,
            String gstin) {
        set("branch", branch);
        set("source_of_supply", sourceOfSupply);
        set("phone", phone);
        set("email", email);
        set("address_1", address1);
        set("address_2", address2);
        set("postcode", postcode);
        set("landmark", landmark);
        set("city", city);
        set("state", state);
        set("country", country);
        set("gstin", gstin);
    }
}
