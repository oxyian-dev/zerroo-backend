package com.hionstudios.zerroo.oauth;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.hionstudios.db.Handler;

public class ZohoBooks {
    public static void saleOrders(long from, long to, HttpServletResponse response) {
        String sql = "Select Invoices.Invoice_Id \"Invoice Number\", To_Char(To_Timestamp(Invoices.Time/1000) at Time Zone 'Asia/Kolkata', 'YYYY-MM-DD') \"Invoice Date\", Invoices.Billing_Firstname \"Billing Firstname\", Invoices.Billing_Lastname \"Billing Lastname\", Invoices.Billing_Email \"Billing Email\", Invoices.Billing_Phone \"Billing Phone\", Invoices.Billing_Address_1 \"Billing Address 1\", Invoices.Billing_Address_2 \"Billing Address 2\", Invoices.Billing_City \"Billing City\", Invoices.Billing_Postcode \"Billing Postcode\", Invoices.Billing_State \"Billing State\", Invoices.Billing_Country \"Billing Country\", Invoices.Shipping_Firstname \"Shipping Firstname\", Invoices.Shipping_Lastname \"Shipping Lastname\", Invoices.Shipping_Email \"Shipping Email\", Invoices.Shipping_Phone \"Shipping Phone\", Invoices.Shipping_Address_1 \"Shipping Address 1\", Invoices.Shipping_Address_2 \"Shipping Address 2\", Invoices.Shipping_City \"Shipping City\", Invoices.Shipping_Postcode \"Shipping Postcode\", Invoices.Shipping_State \"Shipping State\", Invoices.Shipping_Country \"Shipping Country\", Case When Invoices.Shipping_State = 'Tamil Nadu' Then 'TN' When Invoices.Shipping_State = 'Telangana' Then 'TS' When Invoices.Shipping_State = 'Jharkhand' Then 'JH' When Invoices.Shipping_State = 'Rajasthan' Then 'RJ' When Invoices.Shipping_State = 'Uttarakhand' Then 'UK' When Invoices.Shipping_State = 'Maharashtra' Then 'MH' When Invoices.Shipping_State = 'Lakshadweep' Then 'LD' When Invoices.Shipping_State = 'Andaman & Nicobar' Then 'AN' When Invoices.Shipping_State = 'Bihar' Then 'BR' When Invoices.Shipping_State = 'Gujarat' Then 'GJ' When Invoices.Shipping_State = 'Chandigarh' Then 'CH' When Invoices.Shipping_State = 'Goa' Then 'GA' When Invoices.Shipping_State = 'Odisha' Then 'OR' When Invoices.Shipping_State = 'Karnataka' Then 'KA' When Invoices.Shipping_State = 'Kerala' Then 'KL' When Invoices.Shipping_State = 'Delhi' Then 'DL' When Invoices.Shipping_State = 'Chattisgarh' Then 'CH' When Invoices.Shipping_State = 'Uttar Pradesh' Then 'UP' When Invoices.Shipping_State = 'West Bengal' Then 'WB' When Invoices.Shipping_State = 'Manipur' Then 'MN' When Invoices.Shipping_State = 'Pondicherry' Then 'PY' When Invoices.Shipping_State = 'Andhra Pradesh' Then 'AP' END \"Place of Supply\", (Items.Title) \"Item Name\", (Items.Description) \"Item Description\", (Items.Sku) SKU, (Items.Hsn) HSN, (Invoice_Items.Price) Price, (Invoice_Items.Gst_Percent) \"GST Percentage\", (Invoice_Items.Basic) \"Basic Price\", (Invoice_Items.Gst) \"GST Amount\", (Invoice_Items.C_GST) \"C GST\", (Invoice_Items.S_GST) \"S GST\", (Invoice_Items.I_Gst) \"I GST\", '996812' as \"Shipping Charge SAC Code\", Invoices.Shipping_Fee \"Shipping Charge\", Case When Invoices.Shipping_State != Branches.State Then 'I' Else '' End || 'GST18' \"Shipping Charge Tax Name\", Case When Invoices.Shipping_State = Branches.State Then 'Tax Group' Else 'ItemAmount' End as \"Shipping Charge Tax Type\", 18 \"Shipping Charge Tax %\" From Invoices Join Invoice_Items On Invoice_Items.Invoice_Id = Invoices.Id Join Items On Items.Id = Invoice_Items.Item_Id Join Users On Users.Id = Invoices.User_Id Join Branches On Branches.Id = Invoices.Branch_Id Where Invoices.Time Between ? And ?";

        String[] columns = {
                "Invoice Number",
                "Invoice Date",
                "Billing Firstname",
                "Billing Lastname",
                "Billing Email",
                "Billing Phone",
                "Billing Address 1",
                "Billing Address 2",
                "Billing City",
                "Billing Postcode",
                "Billing State",
                "Billing Country",
                "Shipping Firstname",
                "Shipping Lastname",
                "Shipping Email",
                "Shipping Phone",
                "Shipping Address 1",
                "Shipping Address 2",
                "Shipping City",
                "Shipping Postcode",
                "Shipping State",
                "Shipping Country",
                "Place of Supply",
                "Item Name",
                "Item Description",
                "SKU",
                "HSN",
                "price",
                "GST Percentage",
                "Basic Price",
                "GST Amount",
                "C GST",
                "S GST",
                "I GST",
                "Shipping Charge SAC Code",
                "Shipping Charge",
                "Shipping Charge Tax Type",
                "Shipping Charge Tax %"
        };
        response.setContentType("text/csv");
        response.addHeader("Content-Disposition", "attachment; filename=\"gst.csv\"");
        try (PrintWriter writer = response.getWriter()) {
            Handler.toCsv(writer, sql, columns, from, to);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
