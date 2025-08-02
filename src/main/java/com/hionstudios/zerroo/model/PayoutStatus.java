package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Cached;

@Cached
public class PayoutStatus extends Model {
    public static final String PENDING = "Pending";
    public static final String APPROVED = "Approved";

    public static int getId(String status) {
        return PayoutStatus.findFirst("status = ?", status)
                .getInteger("id");
    }
}
