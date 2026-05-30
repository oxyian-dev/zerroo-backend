package com.hionstudios.zerroo.flow.cutoff;

import java.util.List;

import com.hionstudios.MapResponse;
import com.hionstudios.db.Handler;
import com.hionstudios.zerroo.model.Distributor;
import com.hionstudios.zerroo.model.IncomeWalletTransactionType;

public final class DistributorFinancials {
    private static final String FINANCIAL_SUMMARY_SQL =
            "Select " +
                    "Distributors.Total_Left_Pv, Distributors.Total_Right_Pv, Distributors.Cutoff_Left_Pv, Distributors.Cutoff_Right_Pv, Distributors.Carry_Left_Pv, Distributors.Carry_Right_Pv, " +
                    "(Floor(Least(Distributors.Total_Left_Pv, Distributors.Total_Right_Pv) / ?) * ?) Pair_Match_Income, " +
                    "Coalesce((Select Sum(Income_Wallet_Transactions.Full_Amount) From Income_Wallet_Transactions Join Income_Wallet_Transaction_Types On Income_Wallet_Transaction_Types.Id = Income_Wallet_Transactions.Type_Id And Income_Wallet_Transaction_Types.Type = ? Where Income_Wallet_Transactions.Distributor_Id = Distributors.Id), 0) Recorded_Pair_Match_Income, " +
                    "Coalesce((Select Sum(Income_Wallet_Transactions.Full_Amount) From Income_Wallet_Transactions Join Income_Wallet_Transaction_Types On Income_Wallet_Transaction_Types.Id = Income_Wallet_Transactions.Type_Id And Income_Wallet_Transaction_Types.Type = ? Where Income_Wallet_Transactions.Distributor_Id = Distributors.Id), 0) Sp_Income, " +
                    "Coalesce((Select Sum(Income_Wallet_Transactions.Full_Amount) From Income_Wallet_Transactions Join Income_Wallet_Transaction_Types On Income_Wallet_Transaction_Types.Id = Income_Wallet_Transactions.Type_Id And Income_Wallet_Transaction_Types.Type = ? Where Income_Wallet_Transactions.Distributor_Id = Distributors.Id), 0) Company_Income, " +
                    "Coalesce((Select Sum(Income_Wallet_Transactions.Actual_Amount) From Income_Wallet_Transactions Join Income_Wallet_Transaction_Types On Income_Wallet_Transaction_Types.Id = Income_Wallet_Transactions.Type_Id And Income_Wallet_Transaction_Types.Type = ? Where Income_Wallet_Transactions.Distributor_Id = Distributors.Id), 0) Payout_Amount " +
                    "From Distributors Where Distributors.Id = ?";

    private DistributorFinancials() {
    }

    public static MapResponse summary(long distributorId) {
        return Handler.findFirst(FINANCIAL_SUMMARY_SQL,
                80,
                800,
                IncomeWalletTransactionType.PAIR_MATCH_INCOME,
                IncomeWalletTransactionType.SELF_PURCHASE_INCOME,
                IncomeWalletTransactionType.COMPANY,
                IncomeWalletTransactionType.PAYOUT,
                distributorId);
    }

    public static void reconcile(Distributor distributor) {
        MapResponse summary = summary(distributor.getLongId());
        syncFromSummary(distributor, summary);
        long cutoffId = CutoffTransaction.getCurrentCutoffId();
        if (cutoffId > 0) {
            RankUpdate.update(distributor, cutoffId);
        }
    }

    private static void syncFromSummary(Distributor distributor, MapResponse summary) {
        if (distributor == null) {
            return;
        }
        double pairMatchIncome = summary.getDouble("pair_match_income");
        double spIncome = summary.getDouble("sp_income");
        double companyIncome = summary.getDouble("company_income");
        double payoutAmount = summary.getDouble("payout_amount");
        double totalLeftPv = summary.getDouble("total_left_pv");
        double totalRightPv = summary.getDouble("total_right_pv");
        double matchedPv = Math.floor(Math.min(totalLeftPv, totalRightPv) / 80) * 80;
        double carryLeftPv = Math.max(0, totalLeftPv - matchedPv);
        double carryRightPv = Math.max(0, totalRightPv - matchedPv);
        double totalIncome = pairMatchIncome + spIncome + companyIncome;
        double incomeWallet = (totalIncome * 0.9) + payoutAmount;

        distributor.set("carry_left_pv", carryLeftPv);
        distributor.set("carry_right_pv", carryRightPv);
        distributor.set("total_income", totalIncome);
        distributor.set("pair_match_income", pairMatchIncome);
        distributor.set("sp_income", spIncome);
        distributor.set("income_wallet", incomeWallet);
        distributor.saveIt();
    }

    public static void reconcileFinancialState(Distributor distributor) {
        if (distributor == null) {
            return;
        }
        MapResponse summary = summary(distributor.getLongId());
        double expectedPairIncome = summary.getDouble("pair_match_income");
        double recordedPairIncome = summary.getDouble("recorded_pair_match_income");
        double delta = expectedPairIncome - recordedPairIncome;
        if (Math.abs(delta) > 0.0001) {
            IncomeTransaction.addIncome(distributor, delta, IncomeWalletTransactionType.PAIR_MATCH_INCOME);
            return;
        }
        syncFromSummary(distributor, summary);
    }

    public static void reconcileAll() {
        List<Distributor> distributors = Distributor.findAll();
        for (Distributor distributor : distributors) {
            reconcileFinancialState(distributor);
        }
    }
}
