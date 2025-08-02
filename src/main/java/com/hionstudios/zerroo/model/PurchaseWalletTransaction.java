package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

import com.hionstudios.db.Handler;
import com.hionstudios.time.TimeUtil;

public class PurchaseWalletTransaction extends Model {
    public PurchaseWalletTransaction() {
    }

    public PurchaseWalletTransaction(long distributor, double amount, String type) {
        double currentAmount = Handler.getDouble("Select Purchase_Wallet From Distributors Where Id = ?", distributor);
        set("opening_amount", currentAmount);
        set("closing_amount", currentAmount + amount);
        set("distributor_id", distributor);
        set("amount", amount);
        set("type_id", PurchaseWalletTransactionType.getId(type));
        set("time", TimeUtil.currentTime());
    }
}
