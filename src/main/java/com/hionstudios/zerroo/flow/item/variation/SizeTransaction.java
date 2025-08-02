package com.hionstudios.zerroo.flow.item.variation;

import com.hionstudios.CachedSelect;
import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.Handler;
import com.hionstudios.db.SqlCriteria;
import com.hionstudios.db.SqlQuery;
import com.hionstudios.db.SqlUtil;
import com.hionstudios.iam.UserUtil;
import com.hionstudios.zerroo.model.Size;
import com.hionstudios.time.TimeUtil;

public class SizeTransaction {
    public MapResponse get(long id) {
        String sql = "Select Size, Index From Sizes Where Id = ?";
        return Handler.findFirst(sql, id);
    }

    public MapResponse addSize(String size, int index) {
        CachedSelect.dropCache("size");
        return new Size()
                .set("size", size)
                .set("index", index)
                .set("created_time", TimeUtil.currentTime())
                .set("created_by", UserUtil.getUserid())
                .insert() ? MapResponse.success() : MapResponse.failure();
    }

    public MapResponse editSize(long id, String size, int index) {
        CachedSelect.dropCache("size");
        return Size.findById(id)
                .set("size", size)
                .set("index", index)
                .set("modified_by", UserUtil.getUserid())
                .set("modified_time", TimeUtil.currentTime())
                .saveIt() ? MapResponse.success() : MapResponse.failure();
    }

    public MapResponse view(DataGridParams params) {
        String sql = "Select Id, Id \"Action\", Size, Index, (Select Count(*) From Items Where Size_Id = Sizes.Id) Items From Sizes";
        String count = "Select Count(*) From Sizes";
        String[] columns = { "Action", "Size", "Index", "Items" };
        String search = params.getSearch();
        SqlCriteria customCriteria = search == null ? null : new SqlCriteria("(Sizes.Size iLike ?)", search);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }
}
