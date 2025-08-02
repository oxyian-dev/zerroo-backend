package com.hionstudios.zerroo.flow.cutoff;

import java.util.Arrays;

import com.hionstudios.zerroo.Constants;
import com.hionstudios.zerroo.model.Distributor;
import com.hionstudios.zerroo.model.IncomeWalletTransaction;
import com.hionstudios.zerroo.model.IncomeWalletTransactionType;

public class IncomeTransaction {

    public static void addIncome(Distributor distributor, double income, String type) {
        double currentIncomeWallet = distributor.getDouble("income_wallet");
        double currentTotalIncome = distributor.getDouble("total_income");
        double currentPairMatchIncome = distributor.getDouble("pair_match_income");
        double currentSpIncome = distributor.getDouble("sp_income");
        long distributorId = distributor.getLongId();

        if (!IncomeWalletTransactionType.PAYOUT.equals(type)) {
            distributor.set("total_income", currentTotalIncome + income);

            if (IncomeWalletTransactionType.PAIR_MATCH_INCOME.equals(type)) {
                distributor.set("pair_match_income", currentPairMatchIncome + income);
            } else if (IncomeWalletTransactionType.SELF_PURCHASE_INCOME.equals(type)) {
                distributor.set("sp_income", currentSpIncome + income);
            }
        }
        double amountAfterDeduction, closingAmount;
        double tdsAdmin = 0;

        if (Arrays.asList(Constants.TDS).contains(type)) {
            amountAfterDeduction = income;
        } else {
            tdsAdmin = income * 5 / 100;
            amountAfterDeduction = income - tdsAdmin - tdsAdmin;
        }
        distributor.set("income_wallet", currentIncomeWallet + amountAfterDeduction);
        closingAmount = currentTotalIncome + amountAfterDeduction;

        new IncomeWalletTransaction(
                distributorId,
                currentIncomeWallet,
                income,
                amountAfterDeduction,
                closingAmount,
                tdsAdmin,
                type).insert();
    }
}
