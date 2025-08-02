package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Cached;

@Cached
public class Branch extends Model {
    public static final String GSTIN_TAMIL_NADU = "33AADFZ7502M1ZX";

    public static int getIdFromGstin(String gstin) {
        return Branch.findFirst("gstin = ?", gstin)
                .getInteger("id");
    }
}
