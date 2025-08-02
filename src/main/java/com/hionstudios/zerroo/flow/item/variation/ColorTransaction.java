package com.hionstudios.zerroo.flow.item.variation;

import com.hionstudios.CachedSelect;
import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.Handler;
import com.hionstudios.db.SqlCriteria;
import com.hionstudios.db.SqlQuery;
import com.hionstudios.db.SqlUtil;
import com.hionstudios.zerroo.model.Color;

public class ColorTransaction {
    public MapResponse get(long id) {
        String sql = "Select '#' || Hex Hex, Color From Colors Where Id = ?";
        return Handler.findFirst(sql, id);
    }

    public MapResponse viewColors(DataGridParams params) {
        String sql = "Select Id, Id \"Action\", Color, Hex, (Select Count(*) From Items Where Color_Id = Colors.Id) Items From Colors";
        String count = "Select Count(*) From Colors";
        String[] columns = { "Action", "Color", "Hex", "Items" };
        String search = params.getSearch();
        SqlCriteria customCriteria = search == null ? null
                : new SqlCriteria("(Colors.color iLike ?)", search + "%");
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }

    public MapResponse addColour(String color, String hex) {
        CachedSelect.dropCache("color");
        Color c = new Color();
        return Color.construct(color, hex, c).insert() ? MapResponse.success() : MapResponse.failure();
    }

    public MapResponse editColour(long id, String color, String hex) {
        CachedSelect.dropCache("color");
        Color c = Color.findById(id);
        return Color.construct(color, hex, c).saveIt() ? MapResponse.success() : MapResponse.failure();
    }
}
