package com.hionstudios.zerroo.flow.cutoff;

import com.hionstudios.db.Handler;
import com.hionstudios.zerroo.model.Distributor;
import com.hionstudios.zerroo.model.IncomeWalletTransaction;
import com.hionstudios.zerroo.model.IncomeWalletTransactionType;

public class IncomeTransaction {

    public static void addIncome(Distributor distributor, double income, String type) {
        long distributorId = distributor.getLongId();
        double currentIncomeWallet = Handler.getDouble(
                "Select Coalesce(Sum(Income_Wallet_Transactions.Actual_Amount), 0) From Income_Wallet_Transactions Where Income_Wallet_Transactions.Distributor_Id = ?",
                distributorId);
        double amountAfterDeduction, closingAmount;
        double tdsAdmin = 0;

        if (income < 0) {
            if (IncomeWalletTransactionType.PAYOUT.equals(type)) {
                amountAfterDeduction = income;
            } else {
                tdsAdmin = Math.abs(income) * 5 / 100;
                amountAfterDeduction = income * 0.9;
            }
        } else if (IncomeWalletTransactionType.PAYOUT.equals(type)) {
            amountAfterDeduction = income;
        } else {
            tdsAdmin = income * 5 / 100;
            amountAfterDeduction = income - tdsAdmin - tdsAdmin;
        }
        closingAmount = currentIncomeWallet + amountAfterDeduction;

        new IncomeWalletTransaction(
                distributorId,
                currentIncomeWallet,
                income,
                amountAfterDeduction,
                closingAmount,
                tdsAdmin,
                type).insert();

        DistributorFinancials.reconcileFinancialState(distributor);
    }
}
