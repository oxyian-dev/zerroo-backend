package com.hionstudios.zerroo;

import com.hionstudios.zerroo.model.IncomeWalletTransactionType;

public class Constants {
    public static final String FY = "24-25";
    public static final String SHIPPING_SAC = "996812";
    public static final double MIN_PAYOUT = 100;
    public static final long ACTIVATION_PV = 5;
    public static final long MAX_SELF_PV = 5;
    public static final int MIN_PAIR_MATCH = 5;
    public static final int PAIR_MATCH_INCOME = 1_00;
    public static final int SELF_PURCHASE_INCOME = 20;
    public static final int CEILING = 6_250;

    public static String[] TDS = { IncomeWalletTransactionType.PAYOUT };
}
