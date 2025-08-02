package com.hionstudios.zerroo.flow;

import com.hionstudios.db.DbUtil;
import com.hionstudios.zerroo.model.Distributor;
import org.javalite.activejdbc.LazyList;

public class MigrationPvShifting {
    public static void main(String[] args) {
        try {
            DbUtil.openTransaction();
            LazyList<Distributor> allDistributors = Distributor.findAll();

            for (Distributor distributor : allDistributors) {

                double carry_Left_Pv = distributor.getDouble("carry_left_pv");
                double carry_Right_Pv = distributor.getDouble("carry_right_pv");
                double total_Left_Pv = distributor.getDouble("total_left_pv");
                double total_Right_Pv = distributor.getDouble("total_right_pv");

                if (carry_Left_Pv > carry_Right_Pv) {
                    carry_Left_Pv = carry_Left_Pv + carry_Right_Pv;

                    total_Right_Pv = total_Right_Pv - carry_Right_Pv;
                    total_Left_Pv = total_Left_Pv + carry_Right_Pv;

                    carry_Right_Pv = 0;
                } else {
                    carry_Right_Pv = carry_Right_Pv + carry_Left_Pv;

                    total_Left_Pv = total_Left_Pv - carry_Left_Pv;
                    total_Right_Pv = total_Right_Pv + carry_Left_Pv;

                    carry_Left_Pv = 0;
                }

                distributor.set("carry_left_pv", carry_Left_Pv);
                distributor.set("carry_right_pv", carry_Right_Pv);
                distributor.set("total_left_pv", total_Left_Pv);
                distributor.set("total_right_pv", total_Right_Pv);
                distributor.saveIt();

            }

            System.out.println("Income Pv shifted to Power pv updated for all distributors.");
            DbUtil.commitTransaction();
        } catch (Exception e) {
            DbUtil.rollback();
            e.printStackTrace();
        } finally {
            DbUtil.close();
        }

    }

}
