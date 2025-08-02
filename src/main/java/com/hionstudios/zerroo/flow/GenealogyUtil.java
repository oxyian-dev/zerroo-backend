package com.hionstudios.zerroo.flow;

import java.util.Map;

import org.javalite.activejdbc.RowListenerAdapter;

import com.hionstudios.ListResponse;
import com.hionstudios.db.Handler;
import com.hionstudios.zerroo.Constants;
import com.hionstudios.zerroo.flow.cutoff.CutoffTransaction;
import com.hionstudios.zerroo.flow.cutoff.IncomeCalculator;
import com.hionstudios.zerroo.model.Distributor;

public class GenealogyUtil {
    private static final String GENEALOGY_QUERY = "With Recursive UpLine as (Select Id, Parent_Id, Placement = 1 Placement From Distributors Where Distributors.Id = ? Union All Select Distributors.Id, Distributors.Parent_Id, Distributors.Placement = 1 Placement From Distributors Join UpLine On UpLine.Parent_Id = Distributors.Id) Select * From UpLine Where Parent_Id Is Not Null";

    public boolean isDownLine(long upLine, long downLine) {
        return !isUpLine(downLine, upLine);
    }

    public boolean isDownLine(String upLine, String downLine) {
        return !isUpLine(downLine, upLine);
    }

    public ListResponse getUplines(long userid) {
        String sql = "With Recursive UpLine as (Select Distributors.Id, Distributors.Parent_Id From Distributors Where Distributors.Id = ? Union All Select Distributors.Id, Distributors.Parent_Id From Distributors Join UpLine On UpLine.Parent_Id = Distributors.Id) Select Id From UpLine";
        return Handler.firstColumn(sql, userid);
    }

    public ListResponse getUplines(String username) {
        String sql = "With Recursive UpLine as (Select Users.Username, Distributors.Parent_Id From Distributors Join Users On Users.Id = Distributors.Id Where Users.Username = ? Union All Select Users.Username, Distributors.Parent_Id From Distributors Join UpLine On UpLine.Parent_Id = Distributors.Id Join Users On Users.Id = Distributors.Id) Select Username From UpLine";
        return Handler.firstColumn(sql, username);
    }

    public boolean isUpLine(long downLine, long upLine) {
        return getUplines(downLine).contains(upLine);
    }

    public boolean isUpLine(String downLine, String upLine) {
        return getUplines(downLine).contains(upLine);
    }

    public static void addPv(long userid, double pv) {
        Distributor distributor = Distributor.findById(userid);

        final double self_pv = distributor.getDouble("self_pv");
        final double cutoff_self_pv = distributor.getDouble("cutoff_self_pv");

        double total = self_pv + pv;

        // Declaring it final to use it inside the Anonymous class
        // Dirty Code
        final double[] addUplinePv = { 0 };
        double addSpPv = 0;

        if (self_pv >= Constants.MAX_SELF_PV) {
            // Update SP PV only
            final double cutoff_sp_pv = distributor.getDouble("cutoff_sp_pv");
            final double sp_pv = distributor.getDouble("sp_pv");

            distributor.set("cutoff_sp_pv", cutoff_sp_pv + pv);
            distributor.set("sp_pv", sp_pv + pv);

            addSpPv = pv;
        } else if (total <= Constants.MAX_SELF_PV) {
            // No need to update SP PV
            distributor.set("cutoff_self_pv", cutoff_self_pv + pv);
            distributor.set("self_pv", total);

            addUplinePv[0] = pv;
        } else {
            // While adding PV, the total PV will be exceeding the Max PV
            // Update both SP PV & Self PV.
            final double cutoff_sp_pv = distributor.getDouble("cutoff_sp_pv");
            final double sp_pv = distributor.getDouble("sp_pv");

            final double findCutoffSpPv = total - Constants.MAX_SELF_PV;
            final double new_cutoff_self_pv = cutoff_self_pv + Constants.MAX_SELF_PV - self_pv;

            distributor.set("self_pv", Constants.MAX_SELF_PV);
            distributor.set("cutoff_self_pv", new_cutoff_self_pv);

            distributor.set("sp_pv", sp_pv + findCutoffSpPv);
            distributor.set("cutoff_sp_pv", cutoff_sp_pv + findCutoffSpPv);

            addUplinePv[0] = new_cutoff_self_pv;
            addSpPv = findCutoffSpPv;
        }

        // Add SP Income when SP PV > 0
        if (addSpPv > 0) {
            long cutoffId = CutoffTransaction.getCurrentCutoffId();
            IncomeCalculator.spIncome(distributor, cutoffId, addSpPv);
        }

        if (distributor.isModified()) {
            distributor.saveIt();
        }

        // Add Left / Right PV to uplines
        if (addUplinePv[0] > 0) {
            Handler.findWith(GENEALOGY_QUERY, new Long[] { userid }, new RowListenerAdapter() {
                @Override
                public void onNext(Map<String, Object> downline) {
                    long parent = (long) downline.get("parent_id");
                    boolean placement = (boolean) downline.get("placement");
                    if (placement) {
                        Distributor.update(
                                "Cutoff_Left_Pv = Cutoff_Left_Pv + ?, Total_Left_Pv = Total_Left_Pv + ?",
                                "Id = ? ", addUplinePv[0], addUplinePv[0], parent);
                    } else {
                        Distributor.update(
                                "Cutoff_Right_Pv = Cutoff_Right_Pv + ?, Total_Right_Pv = Total_Right_Pv + ?",
                                "Id = ?", addUplinePv[0], addUplinePv[0], parent);
                    }
                }
            });
        }
    }
}
