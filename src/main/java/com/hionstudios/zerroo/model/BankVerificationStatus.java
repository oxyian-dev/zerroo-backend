package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Cached;

@Cached
public class BankVerificationStatus extends Model {
    public static final String PENDING = "Pending";
    public static final String VERIFIED = "Verified";
    public static final String REJECTED = "Rejected";

    public static final int getId(String type) {
        return BankVerificationStatus.findFirst("status = ?", type)
                .getInteger("id");
    }
}
