package com.hionstudios.zerroo.flow.cutoff;

import com.hionstudios.zerroo.Constants;
import com.hionstudios.zerroo.model.Distributor;
import com.hionstudios.zerroo.model.IncomeWalletTransactionType;

public class IncomeCalculator {

    public static void pairMatchIncome(Distributor distributor, long cutoffId) {
        if (distributor.getDouble("self_pv") >= Constants.ACTIVATION_PV) {
            DistributorFinancials.reconcileFinancialState(distributor);
            RankUpdate.update(distributor, cutoffId);
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
