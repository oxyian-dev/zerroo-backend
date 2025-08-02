package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Cached;

@Cached
public class SaleOrderShippingStatus extends Model {
    public static final String UN_SHIPPED = "Un Shipped";
    public static final String SHIPPED = "Shipped";

    public static int getId(String status) {
        return SaleOrderShippingStatus.findFirst("status = ?", status)
                .getInteger("id");
    }
}
