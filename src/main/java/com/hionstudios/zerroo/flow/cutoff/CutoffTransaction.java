package com.hionstudios.zerroo.flow.cutoff;

import java.util.HashMap;

import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.Handler;
import com.hionstudios.db.SqlCriteria;
import com.hionstudios.db.SqlQuery;
import com.hionstudios.db.SqlUtil;
import com.hionstudios.iam.UserUtil;
import com.hionstudios.time.TimeUtil;
import com.hionstudios.zerroo.model.Cutoff;
import com.hionstudios.zerroo.model.CutoffEntry;
import com.hionstudios.zerroo.model.CutoffStatus;
import com.hionstudios.zerroo.model.Distributor;

public class CutoffTransaction {
    public MapResponse cutoffs(DataGridParams params) {
        String sql = "Select Cutoffs.Id, Cutoffs.Id \"Action\", Cutoffs.Cutoff_Number \"Cutoff Number\", Cutoffs.Created_Time \"Created Time\", Cutoff_Statuses.Status, Cutoffs.Initiated_Time \"Initiated Time\", Initiators.Firstname \"Initiated By\" From Cutoffs Join Cutoff_Statuses On Cutoff_Statuses.Id = Cutoffs.Status_Id Left Join Users Initiators On Initiators.Id = Cutoffs.Initiated_By";
        String count = "Select Count(*) From Cutoffs Join Cutoff_Statuses On Cutoff_Statuses.Id = Cutoffs.Status_Id";
        String[] columns = {
                "Action",
                "Cutoff Number",
                "Created Time",
                "Status",
                "Initiated Time",
                "Initiated By"
        };
        String search = params.getSearch();
        HashMap<String, String> mapping = new HashMap<>(4);
        mapping.put("Cutoff Number", "Cutoffs.Cutoff_Number");
        mapping.put("Created Time", "Cutoffs.Created_Time");
        mapping.put("Initiated Time", "Initiators.Initiated_Time");
        mapping.put("Initiated By", "Initiators.Firstname");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
        }
        SqlCriteria customCriteria = search == null ? null
                : new SqlCriteria("(Cutoffs.Cutoff_Number::Text = ?)", search);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }

    public MapResponse cutoff(long id, DataGridParams params) {
        String sql = "Select Cutoff_Entries.Id, Users.Username ZID, Users.Firstname \"Name\", Cutoff_Entries.Cutoff_Left_Pv \"Cutoff Left Pv\", Cutoff_Entries.Cutoff_Right_Pv \"Cutoff Right Pv\", Cutoff_Entries.Cutoff_Pv \"Cutoff Self Pv\", Cutoff_Entries.Cutoff_Sp_Pv \"Cutoff Sp Pv\" From Cutoff_Entries Join Users On Users.Id = Cutoff_Entries.Distributor_Id";
        String count = "Select Count(*) From Cutoff_Entries Join Users On Users.Id = Cutoff_Entries.Distributor_Id";
        String[] columns = {
                "ZID",
                "Name",
                "Cutoff Left Pv",
                "Cutoff Right Pv",
                "Cutoff Self Pv",
                "Cutoff Sp Pv"
        };
        String search = params.getSearch();
        HashMap<String, String> mapping = new HashMap<>(4);
        mapping.put("ZID", "Users.Username");
        mapping.put("Name", "Users.Firstname");
        mapping.put("Cutoff Left Pv", "Cutoff_Entries.Cutoff_Left_Pv");
        mapping.put("Cutoff Right Pv", "Cutoff_Entries.Cutoff_Right_Pv");
        mapping.put("Cutoff Self Pv", "Cutoff_Entries.Cutoff_Pv");
        mapping.put("Cutoff Sp Pv", "Cutoff_Entries.Cutoff_Sp_Pv");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
        }
        params.sortColumn = mapping.getOrDefault(params.sortColumn, params.sortColumn);
        SqlCriteria customCriteria = search == null
                ? new SqlCriteria("(Cutoff_Entries.Cutoff_Id = ?)", id)
                : new SqlCriteria("(Cutoff_Entries.Cutoff_Id = ? And Distributors.Username = ?)", id, search);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params,
                new SqlCriteria("(Cutoff_Entries.Cutoff_Id = ?)", id));
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }

    public static Cutoff getCurrentCutoff() {
        return Cutoff.findFirst("status_id = ?", CutoffStatus.getId(CutoffStatus.PENDING));
    }

    public static long getCurrentCutoffId() {
        return getCurrentCutoff().getLongId();
    }

    /**
     * 
     * 1. For deactivated Distributors
     * 2. For Distributors having 0 or less Self Plus PV
     * 3. Repurchase (Self PV)
     * 
     * @return
     */
    public MapResponse initiate() {
        String criteria = "(Distributors.Cutoff_Self_Pv != ? Or Distributors.Cutoff_Left_Pv != ? Or Distributors.Cutoff_Right_Pv != ?) And Distributors.Active";
        long time = TimeUtil.currentTime();
        long userid = UserUtil.getUserid();

        Cutoff cutoff = getCurrentCutoff();
        long cutoffId = cutoff.getLongId();
        Distributor.findWith((model) -> {
            Distributor distributor = (Distributor) model;
            IncomeCalculator.pairMatchIncome(distributor, cutoffId);
            new CutoffEntry(distributor, cutoffId).insert();

            distributor.set("cutoff_self_pv", 0);
            distributor.set("cutoff_left_pv", 0);
            distributor.set("cutoff_right_pv", 0);

            distributor.saveIt();
        }, criteria, 0, 0, 0);

        Distributor.update("Cutoff_Sp_Pv = ?", "Active = ?", 0, true);

        cutoff.set("status_id", CutoffStatus.getId(CutoffStatus.INITIATED));
        cutoff.set("initiated_time", time);
        cutoff.set("initiated_by", userid);
        if (!cutoff.saveIt()) {
            return MapResponse.failure("Try again");
        }
        washout();
        int nextCutoffNumber = cutoff.getInteger("cutoff_number") + 1;
        int statusId = CutoffStatus.getId(CutoffStatus.PENDING);
        if (!new Cutoff(nextCutoffNumber, statusId, time).insert()) {
            return MapResponse.failure();
        }
        MapResponse response = MapResponse.success();
        response.put("id", cutoffId);
        return response;
    }

    private static void washout() {
        Distributor.update("Cutoff_Self_Pv = ?, Cutoff_Left_Pv = ?, Cutoff_Right_Pv = ?",
                "Active = ? Or Cutoff_Self_Pv = ?", 0, 0, 0, false, 0);
    }

    public MapResponse entries(long id, DataGridParams params) {
        String sql = "Select Cutoff_Entries.Id, Cutoffs.Created_Time \"From\", Cutoffs.Initiated_Time \"To\", Cutoff_Entries.Cutoff_Left_Pv, Cutoff_Entries.Cutoff_Right_Pv, Cutoff_Entries.Cutoff_Pv, Cutoff_Statuses.Status From Cutoff_Entries Join Cutoffs On Cutoffs.Id = Cutoff_Entries.Cutoff_Id Join Cutoff_Statuses On Cutoff_Statuses.Id = Cutoffs.Status_Id";

        String count = "Select count(*) From Cutoff_Entries Join Cutoffs On Cutoffs.Id = Cutoff_Entries.Cutoff_Id Join Cutoff_Statuses On Cutoff_Statuses.Id = Cutoffs.Status_Id";

        String[] columns = {
                "From",
                "To",
                "Cutoff_Left_Pv",
                "Cutoff_Right_Pv",
                "Cutoff_Pv"
        };
        HashMap<String, String> mapping = new HashMap<>(2);
        mapping.put("From", "Cutoffs.Created_Time");
        mapping.put("To", "Cutoffs.Initiated_Time");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
        }
        params.sortColumn = mapping.getOrDefault(params.sortColumn, params.sortColumn);
        SqlCriteria customCriteria = new SqlCriteria("Cutoff_Entries.Distributor_Id = ?", id);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params, customCriteria);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }
}
