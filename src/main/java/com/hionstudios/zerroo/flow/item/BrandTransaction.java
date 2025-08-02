package com.hionstudios.zerroo.flow.item;

import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.Handler;
import com.hionstudios.db.SqlCriteria;
import com.hionstudios.db.SqlQuery;
import com.hionstudios.db.SqlUtil;
import com.hionstudios.zerroo.model.Brand;

public class BrandTransaction {
    public MapResponse get(DataGridParams params) {
        String sql = "Select Brands.Id, Brands.Id \"Action\", Brands.Brand, (Select Count(*) From Item_Groups Where Item_Groups.Brand_Id = Brands.Id) Items From Brands";
        String count = "Select Count(*) From Brands";

        String[] columns = {
                "Action",
                "Brand",
                "Items"
        };
        String search = params.getSearch();
        SqlCriteria customCriteria = search == null ? null
                : new SqlCriteria("(Brands.Brand iLike ?)", "%" + search + "%");
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }

    public MapResponse get(long id) {
        String sql = "Select * From Brands Where Id = ?";
        return Handler.findFirst(sql, id);
    }

    public MapResponse add(String brand) {
        return new Brand(brand).insert() ? MapResponse.success() : MapResponse.failure();
    }

    public MapResponse edit(long id, String brand) {
        Brand.update("brand = ?", "id = ?", brand, id);
        return MapResponse.success();
    }
}
