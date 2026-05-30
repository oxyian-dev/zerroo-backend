package com.hionstudios.zerroo.model;

import java.text.DecimalFormat;

import org.javalite.activejdbc.Model;

import com.hionstudios.MapResponse;
import com.hionstudios.zerroo.Constants;
import com.hionstudios.time.TimeUtil;

public class Invoice extends Model {
    public Invoice() {
    }

    public Invoice(MapResponse order, SaleOrder saleOrder) {
        long branchId = saleOrder.getLong("branch_id");
        set("branch_id", branchId);
        set("cutoff_id", saleOrder.get("cutoff_id"));
        set("user_id", saleOrder.get("user_id"));
        set("user_id", saleOrder.get("user_id"));
        set("invoice_id", getInvoiceId(branchId));
        set("billing_firstname", saleOrder.get("billing_firstname"));
        set("billing_lastname", saleOrder.get("billing_lastname"));
        set("billing_email", saleOrder.get("billing_email"));
        set("billing_phone", saleOrder.get("billing_phone"));
        set("billing_address_1", saleOrder.get("billing_address_1"));
        set("billing_address_2", saleOrder.get("billing_address_2"));
        set("billing_city", saleOrder.get("billing_city"));
        set("billing_postcode", saleOrder.get("billing_postcode"));
        set("billing_state", saleOrder.get("billing_state"));
        set("billing_country", saleOrder.get("billing_country"));

        set("shipping_firstname", saleOrder.get("shipping_firstname"));
        set("shipping_lastname", saleOrder.get("shipping_lastname"));
        set("shipping_email", saleOrder.get("shipping_email"));
        set("shipping_phone", saleOrder.get("shipping_phone"));
        set("shipping_alt_phone", saleOrder.get("shipping_alt_phone"));
        set("shipping_address_1", saleOrder.get("shipping_address_1"));
        set("shipping_address_2", saleOrder.get("shipping_address_2"));
        set("shipping_landmark", saleOrder.get("shipping_landmark"));
        set("shipping_postcode", saleOrder.get("shipping_postcode"));
        set("shipping_city", saleOrder.get("shipping_city"));
        set("shipping_state", saleOrder.get("shipping_state"));
        set("shipping_country", saleOrder.get("shipping_country"));
        set("shipping_latitude", saleOrder.get("shipping_latitude"));
        set("shipping_longitude", saleOrder.get("shipping_longitude"));

        set("shipping_fee", 0);
        set("shipping_basic", 0);
        set("shipping_gst_percent", 0);
        set("shipping_gst", 0);
        set("shipping_c_gst", 0);
        set("shipping_s_gst", 0);
        set("shipping_i_gst", 0);
        set("shipping_sac", order.get("shipping_sac"));
        set("fy", Constants.FY);
        set("time", TimeUtil.currentTime());
    }

    private String getInvoiceId(long branchId) {
        DecimalFormat invoiceFormater = new DecimalFormat("INV/" + Constants.FY + "/00000");
        long count = Invoice.count("fy = ? And Branch_Id = ?", Constants.FY, branchId);
        return invoiceFormater.format(count + 1);
    }
}
