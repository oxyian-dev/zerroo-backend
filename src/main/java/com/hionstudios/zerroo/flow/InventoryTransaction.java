package com.hionstudios.zerroo.flow;

import com.hionstudios.CachedSelect;
import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.Handler;
import com.hionstudios.db.SqlCriteria;
import com.hionstudios.db.SqlQuery;
import com.hionstudios.db.SqlUtil;
import com.hionstudios.zerroo.model.Inventory;

public class InventoryTransaction {
    public MapResponse view(DataGridParams params) {
        String sql = "Select Inventories.Id, Inventories.Inventory, Inventories.City, Coalesce((Select Sum(Quantity) From Stocks Where Inventory_Id = Inventories.Id), 0) Quantity From Inventories Join Branches On Branches.Id = Inventories.Branch_Id";
        String count = "Select Count(*) From Inventories Join Branches On Branches.Id = Inventories.Branch_Id";
        String[] columns = { "Inventory", "City", "Quantity" };
        SqlCriteria criteria = SqlUtil.constructCriteria(params, null, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }

    public MapResponse add(
            String inventory,
            String contact,
            String phone,
            String address1,
            String address2,
            String postcode,
            String landmark,
            String city,
            String state,
            long branch) {
        Inventory i = new Inventory(
                inventory,
                contact,
                phone,
                address1,
                address2,
                postcode,
                landmark,
                city,
                state,
                branch);
        CachedSelect.dropCache("inventory");
        return i.saveIt() ? MapResponse.success() : MapResponse.failure();
    }
}
