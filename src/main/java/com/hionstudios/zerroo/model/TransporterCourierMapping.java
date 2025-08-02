package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.CompositePK;

@CompositePK({ "transporter_id", "courier_id" })
public class TransporterCourierMapping extends Model {
    public TransporterCourierMapping() {
    }

    public TransporterCourierMapping(long transporter, long courier) {
        set("transporter_id", transporter);
        set("courier_id", courier);
    }
}
