package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

import com.hionstudios.time.TimeUtil;

public class IncomeWalletTransaction extends Model {
    public IncomeWalletTransaction() {
    }

    // For every transaction in the Income Wallet, the ledger is noted
    // There are 2 scenarios
    // 1. For every income to the Wallet can be considered for TDS
    // 2. For the Transacion from the Income Wallet to the Payout, the TDS can be
    // deduced

    public IncomeWalletTransaction(
            long distributorId,
            double currentIncomeWallet,
            double income,
            double actualAmount,
            double closingAmount,
            double tdsAdmin,
            String type) {
        set("distributor_id", distributorId);
        set("opening_amount", currentIncomeWallet);
        set("full_amount", income);
        set("actual_amount", income);
        set("tds_amount", tdsAdmin);
        set("admin_amount", tdsAdmin);
        set("actual_amount", actualAmount);
        set("closing_amount", closingAmount);
        set("type_id", IncomeWalletTransactionType.getId(type));
        set("time", TimeUtil.currentTime());
    }
}
