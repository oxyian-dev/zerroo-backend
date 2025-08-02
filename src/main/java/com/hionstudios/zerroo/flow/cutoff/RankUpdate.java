package com.hionstudios.zerroo.flow.cutoff;

import com.hionstudios.zerroo.model.Distributor;
import com.hionstudios.zerroo.model.DistributorRank;
import com.hionstudios.zerroo.model.Rank;

public class RankUpdate {
    private static final int MAX_INDEX = 15;

    public static boolean update(Distributor distributor, long cutoff) {
        Integer rankId = distributor.getInteger("rank_id");

        int currentIndex = 0;
        if (rankId != null) {
            Rank rank = Rank.findById(rankId);
            currentIndex = rank.getInteger("index");
        }
        if (currentIndex != MAX_INDEX) {
            Rank nextRank = Rank.findFirst("index = ?", currentIndex + 1);
            int newRankId = nextRank.getInteger("id");
            double income = distributor.getDouble("total_income");
            if (income >= nextRank.getDouble("min_income")) {
                distributor.set("rank_id", newRankId);
                distributor.saveIt();
                DistributorRank distributorRank = new DistributorRank(distributor.getLongId(), newRankId, cutoff);
                distributorRank.insert();
                return true;
            }
        }
        return false;
    }
}
