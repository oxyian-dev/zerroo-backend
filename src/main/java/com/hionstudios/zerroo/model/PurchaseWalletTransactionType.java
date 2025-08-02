package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Cached;

@Cached
public class PurchaseWalletTransactionType extends Model {
    public static final String PURCHASE = "Purchase";
    public static final String WALLET_REQUEST = "Wallet Request";
    public static final String FROM_COMPANY = "From Company";
    public static final String TO_COMPANY = "To Company";
    public static final String COMPANY = "Company";
    public static final String OPENING_BALANCE = "Opening Balance";

    public static final int getId(String type) {
        return PurchaseWalletTransactionType.findFirst("type = ?", type)
                .getInteger("id");
    }
}
