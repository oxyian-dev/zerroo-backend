package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Cached;

@Cached
public class IncomeWalletTransactionType extends Model {
    public static final String PAIR_MATCH_INCOME = "Pair Match Income";
    public static final String COMPANY = "Company";
    public static final String PAYOUT = "Payout";
    public static final String OPENING_BALANCE = "Opening Balance";
    public static final String SELF_PURCHASE_INCOME = "Sp Income";

    public static int getId(String type) {
        return IncomeWalletTransactionType.findFirst("type = ?", type)
                .getInteger("id");
    }
}
