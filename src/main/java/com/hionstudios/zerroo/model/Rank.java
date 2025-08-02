package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Cached;

@Cached
public class Rank extends Model {
    public static final String MARKETING_ASSOCIATE = "Marketing Associate";
    public static final String CONSULTANT = "Consultant";
    public static final String PEARL = "Pearl";
    public static final String BRONZE = "Saffire";
    public static final String GOLD = "Gold";

    public static String getString(int id) {
        return Rank.findById(id).getString("rank");
    }

    public static int getId(String rank) {
        return Rank.findFirst("rank = ?", rank).getInteger("id");
    }
}
