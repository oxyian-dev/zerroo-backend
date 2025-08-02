package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

import com.hionstudios.db.Handler;

public class Cutoff extends Model {
    public Cutoff() {
    }

    public Cutoff(int cutoffNumber, int statusId, long time) {
        set("cutoff_number", cutoffNumber);
        set("status_id", statusId);
        set("created_time", time);
    }

    public static long getCurrentId() {
        return Handler.getLong("Select Max(Id) From Cutoffs");
    }
}
