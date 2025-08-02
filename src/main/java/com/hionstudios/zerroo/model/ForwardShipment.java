package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

import com.hionstudios.MapResponse;
import com.hionstudios.time.TimeUtil;

public class ForwardShipment extends Model {
    public ForwardShipment() {
    }

    public ForwardShipment(MapResponse order) {
        set("user_id", order.get("user_id"));
        set("firstname", order.get("shipping_firstname"));
        set("lastname", order.get("shipping_lastname"));
        set("email", order.get("shipping_email"));
        set("phone", order.get("shipping_phone"));
        set("alt_phone", order.get("shipping_alt_phone"));
        set("address_1", order.get("shipping_address_1"));
        set("address_2", order.get("shipping_address_2"));
        set("landmark", order.get("shipping_landmark"));
        set("postcode", order.get("shipping_postcode"));
        set("city", order.get("shipping_city"));
        set("state", order.get("shipping_state"));
        set("country", order.get("shipping_country"));
        set("latitude", order.get("shipping_latitude"));
        set("longitude", order.get("shipping_longitude"));
        set("status_id", ForwardShipmentStatus.getId(ForwardShipmentStatus.PENDING));
        set("transporter_id", Transporter.getId(Transporter.ZERROO));

        set("time", TimeUtil.currentTime());
    }
}
