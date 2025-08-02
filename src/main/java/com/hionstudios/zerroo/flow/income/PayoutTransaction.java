package com.hionstudios.zerroo.flow.income;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.javalite.activejdbc.RowListenerAdapter;

import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.Handler;
import com.hionstudios.db.SqlCriteria;
import com.hionstudios.db.SqlQuery;
import com.hionstudios.db.SqlUtil;
import com.hionstudios.zerroo.Constants;
import com.hionstudios.zerroo.flow.cutoff.IncomeTransaction;
import com.hionstudios.zerroo.model.BankVerificationStatus;
import com.hionstudios.zerroo.model.Distributor;
import com.hionstudios.zerroo.model.IncomeWalletTransactionType;
import com.hionstudios.zerroo.model.KycVerificationStatus;
import com.hionstudios.zerroo.model.Payout;
import com.hionstudios.zerroo.model.PayoutEntry;

public class PayoutTransaction {
    private static String BANK = "Axis Bank";

    public MapResponse payouts(DataGridParams params) {
        String sql = "Select Payouts.Id, Payouts.Id \"Payout Id\", Payout_Statuses.Status, Coalesce((Select Sum(Payout_Entries.Amount) From Payout_Entries Where Payout_Entries.Payout_Id = Payouts.Id), 0) Amount, Payouts.Created_Time \"Created Time\", Creator.Firstname \"Creator\", Payouts.Approved_Time \"Approved Time\", Approver.Firstname \"Approver\" From Payouts Join Payout_Statuses On Payout_Statuses.Id = Payouts.Status_Id Left Join Users Creator On Creator.Id = Payouts.Created_By Left Join Users Approver On Approver.Id = Payouts.Approved_By";

        String count = "Select Count(*) From Payouts Join Payout_Statuses On Payout_Statuses.Id = Payouts.Status_Id Left Join Users Creator On Creator.Id = Payouts.Created_By Left Join Users Approver On Approver.Id = Payouts.Approved_By";
        String[] columns = {
                "Payout Id",
                "Status",
                "Amount",
                "Created Time",
                "Creator",
                "Approved Time",
                "Approver"
        };
        HashMap<String, String> mapping = new HashMap<>(4);
        mapping.put("Created Time", "Payouts.Created_Time");
        mapping.put("Creator", "Creator.Firstname");
        mapping.put("Approved Time", "Payouts.Approved_Time");
        mapping.put("Approver", "Approver.Firstname");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
        }
        params.sortColumn = mapping.getOrDefault(params.sortColumn, params.sortColumn);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, null, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }

    public MapResponse axisBankEntries(long id, DataGridParams params) {
        String sql = "Select Payout_Entries.Id, Users.Username ZID, Payout_Entries.Firstname || ' ' || Payout_Entries.Lastname \"Name\", Payout_Entries.City, Payout_Entries.Account_Number \"Account Number\", Payout_Entries.Amount, 'Zerroo' Description, 'zerrooofficial2024@gmail.com' Email From Payout_Entries Join Users On Users.Id = Payout_Entries.Distributor_Id";

        HashMap<String, String> mapping = new HashMap<>(1);
        mapping.put("ZID", "Users.Username");
        mapping.put("Account Number", "Payout_Entries.Account_Number");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
        }
        params.sortColumn = mapping.getOrDefault(params.sortColumn, params.sortColumn);

        String[] columns = {
                "ZID",
                "Name",
                "City",
                "Account Number",
                "Amount",
                "Description",
                "Email"
        };
        SqlCriteria custom = new SqlCriteria(
                "(Payout_Entries.Payout_Id = ? And Payout_Entries.Bank = ?)", id, BANK);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, custom);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                columns);
    }

    public MapResponse nonAxisBankEntries(long id, DataGridParams params) {
        String sql = "Select Payout_Entries.Id, Users.Username ZID, Payout_Entries.Firstname || ' ' || Payout_Entries.Lastname \"Name\", Payout_Entries.City, Payout_Entries.Account_Number \"Account Number\", Payout_Entries.Amount, 'Zerroo' Description, Payout_Entries.IFSC, Payout_Entries.Bank, 'zerrooofficial2024@gmail.com' Email From Payout_Entries Join Users On Users.Id = Payout_Entries.Distributor_Id";

        HashMap<String, String> mapping = new HashMap<>(1);
        mapping.put("ZID", "Users.Username");
        mapping.put("Account Number", "Payout_Entries.Account_Number");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
        }
        params.sortColumn = mapping.getOrDefault(params.sortColumn, params.sortColumn);

        String[] columns = {
                "ZID",
                "Name",
                "City",
                "Account Number",
                "Amount",
                "Description",
                "IFSC",
                "Bank",
                "Email"
        };
        SqlCriteria custom = new SqlCriteria(
                "(Payout_Entries.Payout_Id = ? And (Payout_Entries.Bank != ? Or Payout_Entries.Bank Is Null))", id,
                BANK);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, custom);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                columns);
    }

    public MapResponse distributors(long id, DataGridParams params) {
        String sql = "Select Payout_Entries.Id, Payout_Entries.Firstname, Payout_Entries.Lastname, Payout_Entries.City, Payout_Entries.Account_Number \"Account Number\", Payout_Entries.IFSC, Payout_Entries.Bank, Payout_Entries.Branch, Payout_Entries.Amount From Payout_Entries";
        String count = "Select count(*) From Payout_Entries";
        HashMap<String, String> mapping = new HashMap<>(1);
        mapping.put("Account Number", "Payout_Entries.Account_Number");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
        }
        params.sortColumn = mapping.getOrDefault(params.sortColumn, params.sortColumn);
        String[] columns = {
                "Firstname",
                "Lastname",
                "City",
                "Account Number",
                "IFSC",
                "Bank",
                "Branch",
                "Amount"
        };
        SqlCriteria custom = new SqlCriteria("(Payout_Entries.Distributor_Id = ?)", id);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, custom, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }

    public MapResponse initiate() {
        String sql = "Select Distributors.Id Distributor_Id, Distributors.Income_Wallet, Kyc_Verifications.Pan_Firstname Firstname, Kyc_Verifications.Pan_Lastname Lastname, Bank_Verifications.Bank, Bank_Verifications.Branch, Bank_Verifications.Ifsc, Bank_Verifications.Account_No From Distributors Join Kyc_Verification_Statuses On Kyc_Verification_Statuses.Id = Distributors.Kyc_Status_Id And Kyc_Verification_Statuses.Status = ? Join Bank_Verification_Statuses On Bank_Verification_Statuses.Id = Distributors.Bank_Status_Id And Bank_Verification_Statuses.Status = ? Join Kyc_Verifications On Kyc_Verifications.Id = Distributors.Kyc_Verification_Id Join Bank_Verifications On Bank_Verifications.Id = Distributors.Bank_Verification_Id Where Distributors.Income_Wallet >= ?";

        Payout payout = new Payout();
        payout.insert();
        long payoutId = payout.getLongId();

        Handler.findWith(sql,
                new Object[] { KycVerificationStatus.VERIFIED, BankVerificationStatus.VERIFIED, Constants.MIN_PAYOUT },
                new RowListenerAdapter() {
                    @Override
                    public void onNext(Map<String, Object> row) {
                        PayoutEntry payoutEntry = new PayoutEntry(payoutId, row);
                        if (payoutEntry.insert()) {
                            Distributor distributor = Distributor.findById(row.get("distributor_id"));
                            double amount = ((BigDecimal) row.get("income_wallet")).doubleValue();
                            IncomeTransaction.addIncome(distributor, -amount,
                                    IncomeWalletTransactionType.PAYOUT);
                            distributor.set("income_wallet", 0);
                            distributor.saveIt();
                        }
                    }
                });
        return MapResponse.success();
    }
}
