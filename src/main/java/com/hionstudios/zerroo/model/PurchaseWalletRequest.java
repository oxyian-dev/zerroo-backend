package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

import com.hionstudios.time.TimeUtil;

public class PurchaseWalletRequest extends Model {
    public PurchaseWalletRequest() {
    }

    public PurchaseWalletRequest(
            long distributorId,
            double amount,
            long date,
            String method,
            String depositor,
            String transactionId,
            String proof) {
        set("distributor_id", distributorId);
        set("amount", amount);
        set("date", date);
        set("bank", "Axis Bank");
        set("method", method);
        set("depositor", depositor);
        set("transaction_id", transactionId);
        set("proof", proof);
        set("status_id", PurchaseWalletRequestStatus.getId(PurchaseWalletRequestStatus.PENDING));
        set("time", TimeUtil.currentTime());
    }
}
