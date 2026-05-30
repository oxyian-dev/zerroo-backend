package com.hionstudios.zerroo.flow;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.hionstudios.MapResponse;
import com.hionstudios.db.Handler;
import com.hionstudios.zerroo.model.BankVerificationStatus;
import com.hionstudios.zerroo.model.CutoffStatus;
import com.hionstudios.zerroo.model.ForwardShipmentStatus;
import com.hionstudios.zerroo.model.IncomeWalletTransactionType;
import com.hionstudios.zerroo.model.KycVerificationStatus;
import com.hionstudios.zerroo.model.PurchaseWalletRequestStatus;
import com.hionstudios.zerroo.model.SaleOrderShippingStatus;

public class AdminTransaction {
    public MapResponse dashboard() {
        String sql = "With LatestCutoff As (Select Created_Time, Initiated_Time From Cutoffs Join Cutoff_Statuses On Cutoff_Statuses.Id = Cutoffs.Status_Id And Cutoff_Statuses.Status = ? Order By Cutoffs.Id Desc Limit 1) Select (Select Count(*) From Users Join Distributors On Distributors.Id = Users.Id Where To_Char(To_Timestamp(Created_Time/1000) at Time Zone 'Asia/Kolkata', 'YYYY-MM-DD') = To_Char(Now() at Time Zone 'Asia/Kolkata', 'YYYY-MM-DD')) Today, (Select Count(*) From Purchase_Wallet_Requests Join Purchase_Wallet_Request_Statuses On Purchase_Wallet_Request_Statuses.Id = Purchase_Wallet_Requests.Status_Id And Purchase_Wallet_Request_Statuses.Status = ?) pending_wallets, (Select count(*) From Kyc_Verifications Join Kyc_Verification_Statuses On Kyc_Verification_Statuses.Id = Kyc_Verifications.Status And Kyc_Verification_Statuses.Status = ?) pending_kyc, (Select Count(*) From Bank_Verifications Join Bank_Verification_Statuses On Bank_Verification_Statuses.Id = Bank_Verifications.Status And Bank_Verification_Statuses.Status = ?) pending_bank, (Select Count(*) From Sale_Orders Join Sale_Order_Shipping_Statuses On Sale_Order_Shipping_Statuses.Id = Sale_Orders.Shipping_Status_Id And Sale_Order_Shipping_Statuses.Status = ?) unshipped_sale_orders, (Select Count(*) From Forward_Shipments Join Forward_Shipment_Statuses On Forward_Shipment_Statuses.Id = Forward_Shipments.Status_Id And Forward_Shipment_Statuses.Status = ?) pending_shipments, (Select Coalesce(Sum(Price), 0) From (Select Sum(Sale_Order_Items.Price) Price From Sale_Orders Join Sale_Order_Items On Sale_Order_Items.Order_Id = Sale_Orders.Id Where To_Char(To_Timestamp(Time/1000) at Time Zone 'Asia/Kolkata', 'YYYY-MM-DD') = To_Char(Now() at Time Zone 'Asia/Kolkata', 'YYYY-MM-DD') Group By Sale_Orders.Id) Revenue) Revenue_Today, (Select Coalesce(Sum(Price), 0) From (Select Sum(Sale_Order_Items.Price) Price From Sale_Orders Join Sale_Order_Items On Sale_Order_Items.Order_Id = Sale_Orders.Id Where To_Char(To_Timestamp(Time/1000) at Time Zone 'Asia/Kolkata', 'YYYY-MM') = To_Char(Now() at Time Zone 'Asia/Kolkata', 'YYYY-MM') Group By Sale_Orders.Id) Revenue) Revenue_Month, (Select Coalesce(Sum(Income_Wallet_Transactions.Actual_Amount), 0) From Income_Wallet_Transactions) Income_Wallet, (Select Coalesce(Sum(Purchase_Wallet), 0) From Distributors) Purchase_Wallet, (Select Coalesce(Sum(Amount), 0) From Payout_Entries) Payouts, (Select Coalesce(Sum(Income_Wallet_Transactions.Full_Amount), 0) From Income_Wallet_Transactions Join Income_Wallet_Transaction_Types On Income_Wallet_Transaction_Types.Id = Income_Wallet_Transactions.Type_Id And Income_Wallet_Transaction_Types.Type = ?) pair_match_income_lifetime, (Select Coalesce(Sum(Income_Wallet_Transactions.Full_Amount), 0) From Income_Wallet_Transactions Join Income_Wallet_Transaction_Types On Income_Wallet_Transaction_Types.Id = Income_Wallet_Transactions.Type_Id And Income_Wallet_Transaction_Types.Type = ? And LatestCutoff.Created_Time Is Not Null And Income_Wallet_Transactions.Time Between LatestCutoff.Created_Time And LatestCutoff.Initiated_Time) current_cutoff_pair_income, (Select Coalesce(Sum(Price), 0) From Sale_Order_Items) Revenue_Lifetime From (Select 1) Base Left Join LatestCutoff On True";

        return Handler.findFirst(sql,
                CutoffStatus.INITIATED,
                PurchaseWalletRequestStatus.PENDING,
                KycVerificationStatus.PENDING,
                BankVerificationStatus.PENDING,
                SaleOrderShippingStatus.UN_SHIPPED,
                ForwardShipmentStatus.PENDING,
                IncomeWalletTransactionType.PAIR_MATCH_INCOME,
                IncomeWalletTransactionType.PAIR_MATCH_INCOME);
    }

    // Payouts
    public void tds(long from, long to, HttpServletResponse response) {
        String sql = "Select Users.Username ZID, Kyc_Verifications.Pan, Users.Firstname, Users.Lastname, Addresses.Address_1 \"Address 1\", Addresses.Address_2 \"Address 2\", Addresses.State, Addresses.Postcode, Sum(Income_Wallet_Transactions.Full_Amount) \"Amount\", Sum(Income_Wallet_Transactions.Tds_Amount) \"TDS\", Sum(Income_Wallet_Transactions.Admin_Amount) \"Admin\" From Income_Wallet_Transactions Join Income_Wallet_Transaction_Types On Income_Wallet_Transaction_Types.Id = Income_Wallet_Transactions.Type_Id Join Users On Users.Id = Income_Wallet_Transactions.Distributor_Id Left Join (Select * From Addresses Order By Is_Default Limit 1) Addresses On Addresses.Distributor_Id = Users.Id Join Kyc_Verifications On Kyc_Verifications.Distributor_Id = Users.Id Join Kyc_Verification_Statuses On Kyc_Verification_Statuses.Id = Kyc_Verifications.Status And Kyc_Verification_Statuses.Status = ? Where Income_Wallet_Transactions.Time Between ? And ? And (Income_Wallet_Transaction_Types.Type In (?)) Group By Users.Username, Kyc_Verifications.Pan, Users.Firstname, Users.Lastname, Addresses.Address_1, Addresses.Address_2, Addresses.State, Addresses.Postcode";

        String[] columns = {
                "ZID",
                "PAN",
                "Firstname",
                "Lastname",
                "Address 1",
                "Address 2",
                "State",
                "Postcode",
                "Amount",
                "TDS",
                "Admin"
        };

        response.setContentType("text/csv");
        response.addHeader("Content-Disposition", "attachment; filename=\"tds.csv\"");
        try (PrintWriter writer = response.getWriter()) {
            Handler.toCsv(writer, sql, columns, KycVerificationStatus.VERIFIED, from, to,
                    IncomeWalletTransactionType.PAIR_MATCH_INCOME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
