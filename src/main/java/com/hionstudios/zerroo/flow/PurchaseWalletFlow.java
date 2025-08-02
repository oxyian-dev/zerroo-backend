package com.hionstudios.zerroo.flow;

import com.hionstudios.zerroo.model.Distributor;
import com.hionstudios.zerroo.model.PurchaseWalletTransaction;

public class PurchaseWalletFlow {
    public static void minus(long userid, double amount, String type) {
        add(userid, -amount, type);
    }

    public static void add(long userid, double amount, String type) {
        new PurchaseWalletTransaction(userid, amount, type).insert();
        Distributor.update("Purchase_Wallet = Purchase_Wallet + ?", "Id = ?", amount, userid);
    }
}
