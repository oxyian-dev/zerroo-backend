package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

public class PurchaseWalletRequestStatus extends Model {
    public static final String PENDING = "Pending";
    public static final String APPROVED = "Approved";
    public static final String REJECTED = "Rejected";

    public static int getId(String status) {
        return PurchaseWalletRequestStatus.findFirst("status = ?", status)
                .getInteger("id");
    }
}
