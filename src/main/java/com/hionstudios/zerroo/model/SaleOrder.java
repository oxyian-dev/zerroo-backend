package com.hionstudios.zerroo.model;

import java.text.DecimalFormat;

import org.javalite.activejdbc.Model;

import com.hionstudios.CommonUtil;
import com.hionstudios.zerroo.Constants;
import com.hionstudios.zerroo.params.OrderParams;

public class SaleOrder extends Model {
    public SaleOrder() {
    }

    public SaleOrder(
            OrderParams params,
            long time,
            long userid,
            long cutoff,
            double shipping,
            boolean isTn) {
        set("order_id", getOrderId());
        set("branch_id", Branch.getIdFromGstin(Branch.GSTIN_TAMIL_NADU));
        set("billing_firstname", params.billingFirstName);
        set("billing_lastname", params.billingLastName);
        set("billing_address_1", params.billingAddress1);
        set("billing_address_2", params.billingAddress2);
        set("billing_city", params.billingCity);
        set("billing_state", params.billingState);
        set("billing_country", params.billingCountry);
        set("billing_postcode", params.billingPostcode);
        set("billing_email", params.billingEmail);
        set("billing_phone", params.billingPhone);

        set("shipping_firstname", params.shippingFirstName);
        set("shipping_lastname", params.shippingLastName);
        set("shipping_address_1", params.shippingAddress1);
        set("shipping_address_2", params.shippingAddress2);
        set("shipping_landmark", params.shippingLandmark);
        set("shipping_city", params.shippingCity);
        set("shipping_state", params.shippingState);
        set("shipping_country", params.shippingCountry);
        set("shipping_postcode", params.shippingPostcode);
        set("shipping_email", params.shippingEmail);
        set("shipping_phone", params.shippingPhone);
        set("shipping_alt_phone", params.shippingAltPhone);
        set("time", time);
        set("user_id", userid);
        set("cutoff_id", cutoff);
        set("shipping_fee", shipping);
        double shippingGstPercent = 12;
        double shippingBasic = CommonUtil.removeGst(shipping, shippingGstPercent);
        double shippingGst = shipping - shippingBasic;

        set("shipping_basic", shippingBasic);
        set("shipping_gst_percent", shippingGstPercent);
        set("shipping_gst", shippingGst);

        if (isTn) {
            double halfGst = shippingGst / 2;
            set("shipping_c_gst", halfGst);
            set("shipping_s_gst", halfGst);
        } else {
            set("shipping_i_gst", shippingGst);
        }

        set("shipping_status_id", SaleOrderShippingStatus.getId(SaleOrderShippingStatus.UN_SHIPPED));

        set("shipping_sac", Constants.SHIPPING_SAC);
    }

    private String getOrderId() {
        DecimalFormat saleOrderFormater = new DecimalFormat("SO/" + Constants.FY + "/00000");
        long count = SaleOrder.count("Branch_Id = ?",
                Branch.getIdFromGstin(Branch.GSTIN_TAMIL_NADU));

        return saleOrderFormater.format(count + 1);
    }
}
