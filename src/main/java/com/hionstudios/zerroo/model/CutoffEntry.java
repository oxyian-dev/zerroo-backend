package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

public class CutoffEntry extends Model {
    public CutoffEntry() {
    }

    public CutoffEntry(Distributor distributor, long cutoffId) {
        set("cutoff_id", cutoffId);
        set("distributor_id", distributor.getLongId());

        set("cutoff_left_pv", distributor.get("cutoff_left_pv"));
        set("cutoff_right_pv", distributor.get("cutoff_right_pv"));

        set("carry_left_pv", distributor.get("carry_left_pv"));
        set("carry_right_pv", distributor.get("carry_right_pv"));

        set("cutoff_pv", distributor.get("cutoff_self_pv"));
        set("cutoff_sp_pv", distributor.get("cutoff_sp_pv"));

    }
}
