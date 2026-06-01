package com.hionstudios.zerroo.flow.cutoff;

import java.util.List;

import com.hionstudios.MapResponse;
import com.hionstudios.db.Handler;
import com.hionstudios.time.TimeUtil;
import com.hionstudios.zerroo.Constants;
import com.hionstudios.zerroo.model.Distributor;
import com.hionstudios.zerroo.model.DistributorHistory;
import com.hionstudios.zerroo.model.IncomeWalletTransactionType;

public final class DistributorFinancials {
    private static final String FINANCIAL_SUMMARY_SQL =
            "Select " +
                    "Distributors.Total_Left_Pv, Distributors.Total_Right_Pv, Distributors.Cutoff_Left_Pv, Distributors.Cutoff_Right_Pv, Distributors.Carry_Left_Pv, Distributors.Carry_Right_Pv, " +
                    "Coalesce((Select (Distributor_Histories.New_Value)::numeric From Distributor_Histories Where Distributor_Histories.Distributor_Id = Distributors.Id And Distributor_Histories.Field = 'income_baseline_left_pv' Order By Distributor_Histories.Id Desc Limit 1), 0) Income_Baseline_Left_Pv, " +
                    "Coalesce((Select (Distributor_Histories.New_Value)::numeric From Distributor_Histories Where Distributor_Histories.Distributor_Id = Distributors.Id And Distributor_Histories.Field = 'income_baseline_right_pv' Order By Distributor_Histories.Id Desc Limit 1), 0) Income_Baseline_Right_Pv, " +
                    "Coalesce((Select (Distributor_Histories.New_Value)::numeric From Distributor_Histories Where Distributor_Histories.Distributor_Id = Distributors.Id And Distributor_Histories.Field = 'income_baseline_qualification_income' Order By Distributor_Histories.Id Desc Limit 1), 0) Income_Baseline_Qualification_Income, " +
                    "Coalesce((Select (Distributor_Histories.New_Value)::numeric From Distributor_Histories Where Distributor_Histories.Distributor_Id = Distributors.Id And Distributor_Histories.Field = 'income_baseline_pair_match_income' Order By Distributor_Histories.Id Desc Limit 1), 0) Income_Baseline_Pair_Match_Income, " +
                    "Coalesce((Select (Distributor_Histories.New_Value)::numeric From Distributor_Histories Where Distributor_Histories.Distributor_Id = Distributors.Id And Distributor_Histories.Field = 'income_baseline_sp_income' Order By Distributor_Histories.Id Desc Limit 1), 0) Income_Baseline_Sp_Income, " +
                    "Coalesce((Select (Distributor_Histories.New_Value)::numeric From Distributor_Histories Where Distributor_Histories.Distributor_Id = Distributors.Id And Distributor_Histories.Field = 'income_baseline_company_income' Order By Distributor_Histories.Id Desc Limit 1), 0) Income_Baseline_Company_Income, " +
                    "Coalesce((Select Distributor_Histories.New_Value From Distributor_Histories Where Distributor_Histories.Distributor_Id = Distributors.Id And Distributor_Histories.Field = 'first_pair_completed' Order By Distributor_Histories.Id Desc Limit 1), 'false') First_Pair_Completed, " +
                    "(Select Distributor_Histories.Time From Distributor_Histories Where Distributor_Histories.Distributor_Id = Distributors.Id And Distributor_Histories.Field = 'first_pair_completed' Order By Distributor_Histories.Id Desc Limit 1) First_Pair_Completed_At, " +
                    "Distributors.Active, " +
                    "Coalesce((Select Sum(Income_Wallet_Transactions.Full_Amount) From Income_Wallet_Transactions Join Income_Wallet_Transaction_Types On Income_Wallet_Transaction_Types.Id = Income_Wallet_Transactions.Type_Id And Income_Wallet_Transaction_Types.Type = ? Where Income_Wallet_Transactions.Distributor_Id = Distributors.Id), 0) Qualification_Income, " +
                    "Coalesce((Select Sum(Income_Wallet_Transactions.Full_Amount) From Income_Wallet_Transactions Join Income_Wallet_Transaction_Types On Income_Wallet_Transaction_Types.Id = Income_Wallet_Transactions.Type_Id And Income_Wallet_Transaction_Types.Type = ? Where Income_Wallet_Transactions.Distributor_Id = Distributors.Id), 0) Pair_Match_Income, " +
                    "Coalesce((Select Sum(Income_Wallet_Transactions.Full_Amount) From Income_Wallet_Transactions Join Income_Wallet_Transaction_Types On Income_Wallet_Transaction_Types.Id = Income_Wallet_Transactions.Type_Id And Income_Wallet_Transaction_Types.Type = ? Where Income_Wallet_Transactions.Distributor_Id = Distributors.Id), 0) Sp_Income, " +
                    "Coalesce((Select Sum(Income_Wallet_Transactions.Full_Amount) From Income_Wallet_Transactions Join Income_Wallet_Transaction_Types On Income_Wallet_Transaction_Types.Id = Income_Wallet_Transactions.Type_Id And Income_Wallet_Transaction_Types.Type = ? Where Income_Wallet_Transactions.Distributor_Id = Distributors.Id), 0) Company_Income, " +
                    "Coalesce((Select Sum(Income_Wallet_Transactions.Actual_Amount) From Income_Wallet_Transactions Join Income_Wallet_Transaction_Types On Income_Wallet_Transaction_Types.Id = Income_Wallet_Transactions.Type_Id And Income_Wallet_Transaction_Types.Type = ? Where Income_Wallet_Transactions.Distributor_Id = Distributors.Id), 0) Payout_Amount " +
                    "From Distributors Where Distributors.Id = ?";

    private DistributorFinancials() {
    }

    public static MapResponse summary(long distributorId) {
        return Handler.findFirst(FINANCIAL_SUMMARY_SQL,
                IncomeWalletTransactionType.QUALIFICATION_INCOME,
                IncomeWalletTransactionType.PAIR_MATCH_INCOME,
                IncomeWalletTransactionType.SELF_PURCHASE_INCOME,
                IncomeWalletTransactionType.COMPANY,
                IncomeWalletTransactionType.PAYOUT,
                distributorId);
    }

    private static double safeDouble(MapResponse summary, String key) {
        if (summary == null) {
            return 0;
        }
        Object value = summary.get(key);
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static boolean safeBoolean(MapResponse summary, String key) {
        if (summary == null) {
            return false;
        }
        Object value = summary.get(key);
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue() != 0;
        }
        String normalized = String.valueOf(value).trim().toLowerCase();
        return "true".equals(normalized) || "t".equals(normalized) || "1".equals(normalized) || "yes".equals(normalized);
    }

    private static double baselineDouble(MapResponse summary, String key) {
        return safeDouble(summary, key);
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
        double qualificationIncome = safeDouble(summary, "qualification_income");
        double pairMatchIncome = safeDouble(summary, "pair_match_income");
        double spIncome = safeDouble(summary, "sp_income");
        double companyIncome = safeDouble(summary, "company_income");
        double payoutAmount = safeDouble(summary, "payout_amount");
        double totalLeftPv = safeDouble(summary, "total_left_pv");
        double totalRightPv = safeDouble(summary, "total_right_pv");
        double baselineLeftPv = baselineDouble(summary, "income_baseline_left_pv");
        double baselineRightPv = baselineDouble(summary, "income_baseline_right_pv");
        boolean firstPairCompleted = isFirstPairCompleted(summary);
        double matchedPv = 0;
        double eligibleLeftPv = Math.max(0, totalLeftPv - baselineLeftPv);
        double eligibleRightPv = Math.max(0, totalRightPv - baselineRightPv);
        double carryLeftPv = eligibleLeftPv;
        double carryRightPv = eligibleRightPv;
        if (firstPairCompleted) {
            matchedPv = Math.floor(Math.max(0, Math.min(eligibleLeftPv, eligibleRightPv) - Constants.MIN_PAIR_MATCH) / Constants.MIN_PAIR_MATCH) * Constants.MIN_PAIR_MATCH;
            carryLeftPv = Math.max(0, eligibleLeftPv - Constants.MIN_PAIR_MATCH - matchedPv);
            carryRightPv = Math.max(0, eligibleRightPv - Constants.MIN_PAIR_MATCH - matchedPv);
        }
        double totalIncome = qualificationIncome + pairMatchIncome + spIncome + companyIncome;
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
        if (!safeBoolean(summary, "active")) {
            return;
        }
        double baselineQualificationIncome = baselineDouble(summary, "income_baseline_qualification_income");
        double baselinePairIncome = baselineDouble(summary, "income_baseline_pair_match_income");
        double baselineSpIncome = baselineDouble(summary, "income_baseline_sp_income");
        double baselineCompanyIncome = baselineDouble(summary, "income_baseline_company_income");
        boolean qualificationCompleted = isFirstPairCompleted(summary);
        boolean qualificationEligible = isQualificationEligible(summary);
        double recordedQualificationIncome = safeDouble(summary, "qualification_income") - baselineQualificationIncome;
        if (!qualificationCompleted && recordedQualificationIncome >= Constants.QUALIFICATION_INCOME - 0.0001) {
            markFirstPairCompleted(distributor, summary);
            summary = summary(distributor.getLongId());
            qualificationCompleted = true;
        }
        if (!qualificationCompleted && qualificationEligible) {
            markFirstPairCompleted(distributor, summary);
            IncomeTransaction.addIncome(distributor, Constants.QUALIFICATION_INCOME, IncomeWalletTransactionType.QUALIFICATION_INCOME);
            return;
        }
        double expectedPairIncome = expectedPairIncome(summary, qualificationCompleted);
        double recordedPairIncome = safeDouble(summary, "pair_match_income") - baselinePairIncome;
        double delta = expectedPairIncome - recordedPairIncome;
        if (Math.abs(delta) > 0.0001) {
            IncomeTransaction.addIncome(distributor, delta, IncomeWalletTransactionType.PAIR_MATCH_INCOME);
            return;
        }
        syncFromSummary(distributor, summary);
    }

    public static void recordIncomeBaseline(Distributor distributor) {
        if (distributor == null) {
            return;
        }
        MapResponse summary = summary(distributor.getLongId());
        long time = TimeUtil.currentTime();
        recordBaselineValue(distributor, "income_baseline_left_pv", safeDouble(summary, "total_left_pv"), time);
        recordBaselineValue(distributor, "income_baseline_right_pv", safeDouble(summary, "total_right_pv"), time);
        recordBaselineValue(distributor, "income_baseline_qualification_income", safeDouble(summary, "qualification_income"), time);
        recordBaselineValue(distributor, "income_baseline_pair_match_income", safeDouble(summary, "pair_match_income"), time);
        recordBaselineValue(distributor, "income_baseline_sp_income", safeDouble(summary, "sp_income"), time);
        recordBaselineValue(distributor, "income_baseline_company_income", safeDouble(summary, "company_income"), time);
    }

    private static boolean isFirstPairCompleted(MapResponse summary) {
        return safeBoolean(summary, "first_pair_completed");
    }

    private static void markFirstPairCompleted(Distributor distributor, MapResponse summary) {
        if (distributor == null || isFirstPairCompleted(summary)) {
            return;
        }
        long time = TimeUtil.currentTime();
        DistributorHistory history = new DistributorHistory();
        history.set("distributor_id", distributor.getLongId());
        history.set("field", "first_pair_completed");
        history.set("old_value", "false");
        history.set("new_value", "true");
        history.set("owner_id", distributor.getLongId());
        history.set("reason", "First qualification completed");
        history.set("time", time);
        history.insert();
    }

    private static void recordBaselineValue(Distributor distributor, String field, double value, long time) {
        DistributorHistory history = new DistributorHistory();
        history.set("distributor_id", distributor.getLongId());
        history.set("field", field);
        history.set("old_value", "0");
        history.set("new_value", String.valueOf(value));
        history.set("owner_id", distributor.getLongId());
        history.set("reason", "Income eligibility baseline reset");
        history.set("time", time);
        history.insert();
    }

    private static double expectedPairIncome(MapResponse summary, boolean qualificationCompleted) {
        if (!qualificationCompleted) {
            return 0;
        }
        double totalLeftPv = Math.max(0, safeDouble(summary, "total_left_pv") - baselineDouble(summary, "income_baseline_left_pv"));
        double totalRightPv = Math.max(0, safeDouble(summary, "total_right_pv") - baselineDouble(summary, "income_baseline_right_pv"));
        double matchedPairs = Math.floor(Math.max(0, Math.min(totalLeftPv, totalRightPv) - Constants.MIN_PAIR_MATCH) / Constants.MIN_PAIR_MATCH);
        return matchedPairs * Constants.PAIR_MATCH_INCOME;
    }

    private static boolean isQualificationEligible(MapResponse summary) {
        double totalLeftPv = Math.max(0, safeDouble(summary, "total_left_pv") - baselineDouble(summary, "income_baseline_left_pv"));
        double totalRightPv = Math.max(0, safeDouble(summary, "total_right_pv") - baselineDouble(summary, "income_baseline_right_pv"));
        double smaller = Math.min(totalLeftPv, totalRightPv);
        double larger = Math.max(totalLeftPv, totalRightPv);
        return smaller >= Constants.MIN_PAIR_MATCH
                && Math.abs(larger - (smaller * 2)) < 0.0001;
    }

    public static void reconcileAll() {
        List<Distributor> distributors = Distributor.findAll();
        for (Distributor distributor : distributors) {
            reconcileFinancialState(distributor);
        }
    }
}
