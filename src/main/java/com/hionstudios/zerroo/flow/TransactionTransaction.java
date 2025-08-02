package com.hionstudios.zerroo.flow;

import java.util.HashMap;
import java.util.List;

import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.Handler;
import com.hionstudios.db.SqlCriteria;
import com.hionstudios.db.SqlQuery;
import com.hionstudios.db.SqlUtil;
import com.hionstudios.iam.UserUtil;
import com.hionstudios.zerroo.model.IncomeWalletTransactionType;

public class TransactionTransaction {
    public MapResponse incomeTransaction(DataGridParams params) {
        String sql = "Select Income_Wallet_Transactions.Id, Income_Wallet_Transactions.Time, Income_Wallet_Transaction_Types.Type, Income_Wallet_Transactions.Actual_Amount \"Effective Amount\", Income_Wallet_Transactions.Tds_Amount \"TDS\", Income_Wallet_Transactions.Admin_Amount \"Admin Charges\", Income_Wallet_Transactions.Full_Amount \"Full Amount\", Income_Wallet_Transactions.Closing_Amount \"Closing Balance\" From Income_Wallet_Transactions Join Income_Wallet_Transaction_Types On Income_Wallet_Transaction_Types.Id = Income_Wallet_Transactions.Type_Id";
        String count = "Select Count(*) From Income_Wallet_Transactions Join Income_Wallet_Transaction_Types On Income_Wallet_Transaction_Types.Id = Income_Wallet_Transactions.Type_Id";
        String[] columns = {
                "Time",
                "Type",
                "Effective Amount",
                "TDS",
                "Admin Charges",
                "Full Amount",
                "Closing Balance"
        };
        long userid = UserUtil.getUserid();
        HashMap<String, String> mapping = new HashMap<>(5);
        mapping.put("Effective Amount", "Income_Wallet_Transactions.Actual_Amount");
        mapping.put("TDS", "Income_Wallet_Transactions.Tds_Amount");
        mapping.put("Admin Charges", "Income_Wallet_Transactions.Admin_Amount");
        mapping.put("Full Amount", "Income_Wallet_Transactions.Full_Amount");
        mapping.put("Closing Balance", "Income_Wallet_Transactions.Closing_Amount");
        mapping.put("Type", "Income_Wallet_Transaction_Types.Type");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
        }
        HashMap<String, String> sortMapping = new HashMap<>(1);
        SqlCriteria customCriteria = new SqlCriteria("Income_Wallet_Transactions.Distributor_Id = ?", userid);
        params.sortColumn = sortMapping.getOrDefault(params.sortColumn, params.sortColumn);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params, customCriteria, false);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }

    public MapResponse purchaseTransactions(DataGridParams params) {
        String sql = "Select Purchase_Wallet_Transactions.Id, Purchase_Wallet_Transactions.Time, Purchase_Wallet_Transaction_Types.Type, Purchase_Wallet_Transactions.Amount, Purchase_Wallet_Transactions.Closing_Amount \"Closing Balance\" From Purchase_Wallet_Transactions Join Purchase_Wallet_Transaction_Types On Purchase_Wallet_Transaction_Types.Id = Purchase_Wallet_Transactions.Type_Id";
        String count = "Select Count(*) From Purchase_Wallet_Transactions";
        String[] columns = {
                "Time",
                "Type",
                "Amount",
                "Closing Balance"
        };
        long userid = UserUtil.getUserid();
        HashMap<String, String> mapping = new HashMap<>(1);
        mapping.put("Closing Balance", "Purchase_Wallet_Transactions.Closing_Amount");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
        }
        HashMap<String, String> sortMapping = new HashMap<>(1);
        SqlCriteria customCriteria = new SqlCriteria("Purchase_Wallet_Transactions.Distributor_Id = ?", userid);
        params.sortColumn = sortMapping.getOrDefault(params.sortColumn, params.sortColumn);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params, customCriteria, false);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }

    public MapResponse payoutTransactions(DataGridParams params) {
        String sql = "Select Payout_Entries.Id Id, Payouts.Created_Time Date, Payouts.Approved_Time \"Approved Date\", Payout_Entries.Amount, Payout_Entries.Account_Number \"Account Number\", Payout_Entries.IFSC, Payout_Entries.Bank, Payout_Entries.Branch From Payout_Entries Join Payouts On Payouts.Id = Payout_Entries.Payout_Id";
        String count = "Select Count(*) From Payout_Entries Join Payouts On Payouts.Id = Payout_Entries.Payout_Id";
        String[] columns = {
                "Date",
                "Approved Date",
                "Amount",
                "Account Number",
                "IFSC",
                "Bank",
                "Branch"
        };
        long userid = UserUtil.getUserid();
        HashMap<String, String> mapping = new HashMap<>(2);
        mapping.put("Account Number", "Payout_Entries.Account_Number");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
        }
        HashMap<String, String> sortMapping = new HashMap<>(1);
        SqlCriteria customCriteria = new SqlCriteria("Payout_Entries.Distributor_Id = ?", userid);
        params.sortColumn = sortMapping.getOrDefault(params.sortColumn, params.sortColumn);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params, customCriteria, false);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }

    public MapResponse month_incomeTransactions() {
        String sql = "Select To_Char(To_Timestamp(Income_Wallet_Transactions.Time/1000) at Time Zone 'Asia/Kolkata', 'Mon') as month, Sum(Income_Wallet_Transactions.Full_Amount) as full_amount From Generate_Series(1, 12) As Months(Month) Left Join Income_Wallet_Transactions On To_Char(To_Timestamp(Income_Wallet_Transactions.Time/1000) at Time Zone 'Asia/Kolkata', 'MM')::Int = Months.Month Join Income_Wallet_Transaction_Types On Income_Wallet_Transaction_Types.Id = Income_Wallet_Transactions.Type_Id And Income_Wallet_Transaction_Types.Type = ? Where Income_Wallet_Transactions.Distributor_Id = ? Group By month Order By Min(Income_Wallet_Transactions.Time) Limit 12";

        sql = "Select * From Generate_Series(1, 12) As Months(Month) Left Join (Select  To_Char(To_Timestamp(Income_Wallet_Transactions.Time/1000) at Time Zone 'Asia/Kolkata', 'Mon') as month, Sum(Income_Wallet_Transactions.Full_Amount) as full_amount From Income_Wallet_Transactions Join Income_Wallet_Transaction_Types On Income_Wallet_Transaction_Types.Id = Income_Wallet_Transactions.Type_Id And Income_Wallet_Transaction_Types.Type = ? Where Income_Wallet_Transactions.Distributor_Id = ? Group By month Order By Min(Income_Wallet_Transactions.Time) Limit 12) Incomes On TO_CHAR(DATE '2000-01-01' + (Months.Month - 1) * INTERVAL '1 month', 'Mon') = Incomes.Month";

        long userid = UserUtil.getUserid();

        List<MapResponse> month = Handler.findAll(sql, IncomeWalletTransactionType.PAIR_MATCH_INCOME, userid);
        MapResponse response = new MapResponse().put("month", month);
        return response;

    }
}
