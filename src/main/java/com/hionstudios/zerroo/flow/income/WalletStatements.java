package com.hionstudios.zerroo.flow.income;

import java.util.HashMap;

import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.Handler;
import com.hionstudios.db.SqlCriteria;
import com.hionstudios.db.SqlQuery;
import com.hionstudios.db.SqlUtil;

public class WalletStatements {
        public MapResponse incomes(DataGridParams params) {
                String sql = "Select Income_Wallet_Transactions.Id, Income_Wallet_Transactions.Time, Users.Username ZID, Users.Firstname, Income_Wallet_Transaction_Types.Type, Income_Wallet_Transactions.Actual_Amount \"Effective Amount\", Income_Wallet_Transactions.Tds_Amount \"TDS\", Income_Wallet_Transactions.Admin_Amount \"Admin Charges\", Income_Wallet_Transactions.Full_Amount \"Full Amount\", Income_Wallet_Transactions.Closing_Amount \"Closing Balance\" From Income_Wallet_Transactions Join Users On Users.Id = Income_Wallet_Transactions.Distributor_Id Join Income_Wallet_Transaction_Types On Income_Wallet_Transaction_Types.Id = Income_Wallet_Transactions.Type_Id";
                String count = "Select Count(*) From Income_Wallet_Transactions";
                String[] columns = {
                                "Time",
                                "ZID",
                                "Firstname",
                                "Type",
                                "Effective Amount",
                                "TDS",
                                "Admin Charges",
                                "Full Amount",
                                "Closing Balance"
                };
                HashMap<String, String> mapping = new HashMap<>(6);
                mapping.put("ZID", "Users.Username");
                mapping.put("Effective Amount", "Income_Wallet_Transactions.Actual_Amount");
                mapping.put("TDS", "Income_Wallet_Transactions.Tds_Amount");
                mapping.put("Admin Charges", "Income_Wallet_Transactions.Admin_Amount");
                mapping.put("Full Amount", "Income_Wallet_Transactions.Full_Amount");
                mapping.put("Closing Balance", "Income_Wallet_Transactions.Closing_Amount");
                for (int i = 0; i < params.filterColumn.length; i++) {
                        params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
                }
                params.sortColumn = mapping.getOrDefault(params.sortColumn, params.sortColumn);
                String search = params.getSearch();
                SqlCriteria customCriteria = search == null ? null
                                : new SqlCriteria("(Users.Username = ?", search);
                SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
                SqlCriteria filter = SqlUtil.constructCriteria(params);
                return Handler.toDataGrid(
                                new SqlQuery(sql, criteria),
                                new SqlQuery(count, filter),
                                columns);
        }

        public MapResponse purchase(DataGridParams params) {
                String sql = "Select Purchase_Wallet_Transactions.Id, Purchase_Wallet_Transactions.Time, Users.Username ZID, Users.Firstname \"Name\", Purchase_Wallet_Transactions.Amount, Purchase_Wallet_Transactions.Opening_Amount \"Opening Amount\", Purchase_Wallet_Transactions.Closing_Amount \"Closing Amount\", Purchase_Wallet_Transaction_Types.Type, Purchase_Wallet_Transactions.Remark From Purchase_Wallet_Transactions Join Purchase_Wallet_Transaction_Types On Purchase_Wallet_Transaction_Types.Id = Purchase_Wallet_Transactions.Type_Id Join Distributors On Distributors.Id = Purchase_Wallet_Transactions.Distributor_id Join Users On Users.Id = Distributors.Id";
                String count = "Select Count(*) From Purchase_Wallet_Transactions Join Purchase_Wallet_Transaction_Types On Purchase_Wallet_Transaction_Types.Id = Purchase_Wallet_Transactions.Type_Id Join Distributors On Distributors.Id = Purchase_Wallet_Transactions.Distributor_id Join Users On Users.Id = Distributors.Id";

                String[] columns = {
                                "Time",
                                "ZID",
                                "Name",
                                "Amount",
                                "Opening Amount",
                                "Closing Amount",
                                "Type",
                                "Remark"
                };
                HashMap<String, String> mapping = new HashMap<>(3);
                mapping.put("ZID", "Users.Username");
                mapping.put("Name", "Users.Firstname");
                mapping.put("Opening Amount", "Purchase_Wallet_Transactions.Opening_Amount");
                mapping.put("Closing Amount", "Purchase_Wallet_Transactions.Closing_Amount");
                for (int i = 0; i < params.filterColumn.length; i++) {
                        params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
                }
                params.sortColumn = mapping.getOrDefault(params.sortColumn, params.sortColumn);
                String search = params.getSearch();
                SqlCriteria customCriteria = search == null ? null
                                : new SqlCriteria("(Users.Username = ?", search);
                SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
                SqlCriteria filter = SqlUtil.constructCriteria(params);
                return Handler.toDataGrid(
                                new SqlQuery(sql, criteria),
                                new SqlQuery(count, filter),
                                columns);
        }

        public MapResponse incomes(long id, DataGridParams params) {
                String sql = "Select Income_Wallet_Transactions.Id, Income_Wallet_Transactions.Time, Income_Wallet_Transaction_Types.Type, Income_Wallet_Transactions.Actual_Amount \"Effective Amount\", Income_Wallet_Transactions.Tds_Amount \"TDS\", Income_Wallet_Transactions.Admin_Amount \"Admin Charges\", Income_Wallet_Transactions.Full_Amount \"Full Amount\", Income_Wallet_Transactions.Closing_Amount \"Closing Balance\" From Income_Wallet_Transactions Join Income_Wallet_Transaction_Types On Income_Wallet_Transaction_Types.Id = Income_Wallet_Transactions.Type_Id";
                String count = "Select Count(*) From Income_Wallet_Transactions";
                String[] columns = {
                                "Time",
                                "Type",
                                "Effective Amount",
                                "TDS",
                                "Admin Charges",
                                "Full Amount",
                                "Closing Balance"
                };
                HashMap<String, String> mapping = new HashMap<>(6);
                mapping.put("Effective Amount", "Income_Wallet_Transactions.Actual_Amount");
                mapping.put("TDS", "Income_Wallet_Transactions.Tds_Amount");
                mapping.put("Admin Charges", "Income_Wallet_Transactions.Admin_Amount");
                mapping.put("Full Amount", "Income_Wallet_Transactions.Full_Amount");
                mapping.put("Closing Balance", "Income_Wallet_Transactions.Closing_Amount");
                for (int i = 0; i < params.filterColumn.length; i++) {
                        params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
                }
                params.sortColumn = mapping.getOrDefault(params.sortColumn, params.sortColumn);
                SqlCriteria customCriteria = new SqlCriteria("(Income_Wallet_Transactions.Distributor_Id = ?)", id);
                SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
                SqlCriteria filter = SqlUtil.constructCriteria(params);
                return Handler.toDataGrid(
                                new SqlQuery(sql, criteria),
                                new SqlQuery(count, filter),
                                columns);
        }

        public MapResponse purchase(long id, DataGridParams params) {
                String sql = "Select Purchase_Wallet_Transactions.Id, Purchase_Wallet_Transactions.Time, Purchase_Wallet_Transactions.Amount, Purchase_Wallet_Transactions.Opening_Amount \"Opening Amount\", Purchase_Wallet_Transactions.Closing_Amount \"Closing Amount\", Purchase_Wallet_Transaction_Types.Type, Purchase_Wallet_Transactions.Remark From Purchase_Wallet_Transactions Join Purchase_Wallet_Transaction_Types On Purchase_Wallet_Transaction_Types.Id = Purchase_Wallet_Transactions.Type_Id";
                String count = "Select Count(*) From Purchase_Wallet_Transactions Join Purchase_Wallet_Transaction_Types On Purchase_Wallet_Transaction_Types.Id = Purchase_Wallet_Transactions.Type_Id";

                String[] columns = {
                                "Time",
                                "Amount",
                                "Opening Amount",
                                "Closing Amount",
                                "Type",
                                "Remark"
                };
                HashMap<String, String> mapping = new HashMap<>(4);
                mapping.put("Opening Amount", "Purchase_Wallet_Transactions.Opening_Amount");
                mapping.put("Closing Amount", "Purchase_Wallet_Transactions.Closing_Amount");
                for (int i = 0; i < params.filterColumn.length; i++) {
                        params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
                }
                params.sortColumn = mapping.getOrDefault(params.sortColumn, params.sortColumn);
                SqlCriteria customCriteria = new SqlCriteria("(Purchase_Wallet_Transactions.Distributor_id = ?)", id);
                SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
                SqlCriteria filter = SqlUtil.constructCriteria(params);
                return Handler.toDataGrid(
                                new SqlQuery(sql, criteria),
                                new SqlQuery(count, filter),
                                columns);
        }
}
