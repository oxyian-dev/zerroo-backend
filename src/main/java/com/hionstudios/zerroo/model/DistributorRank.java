package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

import com.hionstudios.time.TimeUtil;

public class DistributorRank extends Model {
    public DistributorRank() {
    }

    public DistributorRank(long distributor, int rank, long cutoff) {
        set("distributor_id", distributor);
        set("rank_id", rank);
        set("cutoff_id", cutoff);
        set("time", TimeUtil.currentTime());
    }
}
