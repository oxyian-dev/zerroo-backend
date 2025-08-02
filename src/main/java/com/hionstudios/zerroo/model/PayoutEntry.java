package com.hionstudios.zerroo.model;

import java.math.BigDecimal;
import java.util.Map;

import org.javalite.activejdbc.Model;

public class PayoutEntry extends Model {
    public PayoutEntry() {
    }

    public PayoutEntry(long payoutId, Map<String, Object> entry) {
        set("payout_id", payoutId);
        set("distributor_id", entry.get("distributor_id"));
        set("firstname", entry.get("firstname"));
        set("lastname", entry.get("lastname"));
        set("city", "Dindigul");
        set("account_number", entry.get("account_no"));
        set("ifsc", entry.get("ifsc"));
        set("bank", entry.get("bank"));
        set("branch", entry.get("branch"));
        double amount = ((BigDecimal) entry.get("income_wallet")).doubleValue();
        set("amount", amount);
    }
}
