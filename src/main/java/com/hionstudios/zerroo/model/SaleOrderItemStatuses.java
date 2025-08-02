package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Cached;

@Cached
public class SaleOrderItemStatuses extends Model {
    public static final String UN_SHIPPED = "Un Shipped";
    public static final String SHIPPED = "Shipped";
    public static final String SHIPMENT_PROCESSING = "Shipment Processing";
    public static final String DISPATCHED = "Dispatched";
    public static final String DELIVERED = "Delivered";
    public static final String FULFILLED = "Fulfilled";
    public static final String REFUND_REQUESTES = "Refund Requested";
    public static final String RETURN_INITIATED = "Return Initiated";
    public static final String RETURN_RECEIVED = "Return Received";
    public static final String QC_PROCESSING = "QC Processing";
    public static final String REFUNDED = "Refunded";
    public static final String REFUND_CANCELLED = "Refund Cancelled";
    public static final String EXCHANGE_REQUESTED = "Exchange Requested";

    public static int getId(String status) {
        return SaleOrderItemStatuses.findFirst("status = ?", status)
                .getInteger("id");
    }
}
