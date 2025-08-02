package com.hionstudios.zerroo.flow.cutoff;

import com.hionstudios.zerroo.Constants;
import com.hionstudios.zerroo.model.Distributor;
import com.hionstudios.zerroo.model.IncomeWalletTransactionType;

public class IncomeCalculator {

    public static void pairMatchIncome(Distributor distributor, long cutoffId) {
        double carryLeftPv = distributor.getDouble("carry_left_pv");
        double carryRightPv = distributor.getDouble("carry_right_pv");

        double cutoffLeftPv = distributor.getDouble("cutoff_left_pv");
        double cutoffRightPv = distributor.getDouble("cutoff_right_pv");

        double left = carryLeftPv + cutoffLeftPv;
        double right = carryRightPv + cutoffRightPv;

        final double pairMatchPv = Math.min(left, right);
        double selfPv = distributor.getDouble("self_pv");
        if (selfPv > 0) {
            final int pairsMatched = (int) pairMatchPv / Constants.MIN_PAIR_MATCH;
            final int pairMatchedPv = pairsMatched * Constants.MIN_PAIR_MATCH;

            if (pairsMatched > 0) {
                if (selfPv >= Constants.ACTIVATION_PV) {
                    double ceiling = Constants.CEILING;
                    double income = Math.min(ceiling, pairsMatched * Constants.PAIR_MATCH_INCOME);
                    IncomeTransaction.addIncome(distributor, income,
                            IncomeWalletTransactionType.PAIR_MATCH_INCOME);
                }

                distributor.set("carry_left_pv", left - pairMatchedPv);
                distributor.set("carry_right_pv", right - pairMatchedPv);

                RankUpdate.update(distributor, cutoffId);
            } else {
                distributor.set("carry_left_pv", left);
                distributor.set("carry_right_pv", right);
            }
        }
    }

    public static void spIncome(Distributor distributor, long cutoffId, double sp_pv) {
        if (sp_pv > 0) {
            double spIncome = sp_pv * Constants.SELF_PURCHASE_INCOME;
            IncomeTransaction.addIncome(distributor, spIncome,
                    IncomeWalletTransactionType.SELF_PURCHASE_INCOME);
            RankUpdate.update(distributor, cutoffId);
        }
    }
}
