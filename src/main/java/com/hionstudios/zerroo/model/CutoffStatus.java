package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Cached;

@Cached
public class CutoffStatus extends Model {
    public static final String PENDING = "Pending";
    public static final String INITIATED = "Initiated";

    public static final int getId(String type) {
        return CutoffStatus.findFirst("status = ?", type)
                .getInteger("id");
    }
}
