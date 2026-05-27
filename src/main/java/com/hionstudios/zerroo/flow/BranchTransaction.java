package com.hionstudios.zerroo.flow;

import com.hionstudios.CachedSelect;
import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.Handler;
import com.hionstudios.db.SqlCriteria;
import com.hionstudios.db.SqlQuery;
import com.hionstudios.db.SqlUtil;
import com.hionstudios.zerroo.model.Branch;

public class BranchTransaction {
    public MapResponse view(DataGridParams params) {
        String sql = "Select Id, Id \"Action\", Branch, Source_Of_Supply \"Source\", City, State, GSTIN From Branches";
        String count = "Select Count(*) From Branches";
        String[] columns = { "Action", "Branch", "Source", "City", "State", "GSTIN" };
        SqlCriteria criteria = SqlUtil.constructCriteria(params, null, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }

    public MapResponse add(
            String branch,
            String sourceOfSupply,
            String phone,
            String email,
            String address1,
            String address2,
            String postcode,
            String landmark,
            String city,
            String state,
            String country,
            String gstin) {
        Branch b = new Branch(
                branch,
                sourceOfSupply,
                phone,
                email,
                address1,
                address2,
                postcode,
                landmark,
                city,
                state,
                country,
                gstin);
        CachedSelect.dropCache("branch");
        return b.saveIt() ? MapResponse.success() : MapResponse.failure();
    }
}
