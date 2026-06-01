package com.hionstudios.zerroo.flow.cutoff;

import com.hionstudios.db.Handler;
import com.hionstudios.zerroo.Constants;
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
        double tdsAmount = 0;
        double adminAmount = 0;

        if (income < 0) {
            if (IncomeWalletTransactionType.PAYOUT.equals(type)) {
                amountAfterDeduction = income;
            } else {
                double baseAmount = Math.abs(income);
                tdsAmount = baseAmount * Constants.INCOME_TDS_PERCENT / 100;
                adminAmount = baseAmount * Constants.INCOME_ADMIN_PERCENT / 100;
                amountAfterDeduction = income + tdsAmount + adminAmount;
            }
        } else if (IncomeWalletTransactionType.PAYOUT.equals(type)) {
            amountAfterDeduction = income;
        } else {
            tdsAmount = income * Constants.INCOME_TDS_PERCENT / 100;
            adminAmount = income * Constants.INCOME_ADMIN_PERCENT / 100;
            amountAfterDeduction = income - tdsAmount - adminAmount;
        }
        closingAmount = currentIncomeWallet + amountAfterDeduction;

        new IncomeWalletTransaction(
                distributorId,
                currentIncomeWallet,
                income,
                amountAfterDeduction,
                closingAmount,
                tdsAmount,
                adminAmount,
                type).insert();

        DistributorFinancials.reconcileFinancialState(distributor);
    }
}
