package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Cached;

@Cached
public class StockLedgerType extends Model {
    public static final String INWARD = "Inward";
    public static final String ADJUSTMENT = "Adjustment";
    public static final String TRANSFER_TO = "Transfer To";
    public static final String TRANSFER_FROM = "Transfer From";
    public static final String SALES = "Sales";
    
    public static int getId(String level) {
        return StockLedgerType.findFirst("type = ?", level).getInteger("id");
    }
}
