package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Cached;

@Cached
public class ForwardShipmentStatus extends Model {
    public static final String PENDING = "Pending";
    public static final String PROCESSING = "Processing";
    public static final String PICKED_UP = "Picked Up";
    public static final String DISPATCHED = "Dispatched";
    public static final String DELIVERED = "Delivered";
    public static final String RTO_PENDING = "RTO Pending";
    public static final String RTO_RETURNED = "RTO Returned";
    public static final String LOST = "Lost";
    public static final String EXCEPTION = "Exception";
    public static final String ERROR = "Error";

    public static final int getId(String status) {
        ForwardShipmentStatus forwardShipmentStatus = ForwardShipmentStatus.findFirst("status =?", status);
        return forwardShipmentStatus.getInteger("id");
    }
}
