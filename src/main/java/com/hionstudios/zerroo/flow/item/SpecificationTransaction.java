package com.hionstudios.zerroo.flow.item;

import java.util.HashMap;

import com.hionstudios.CachedSelect;
import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.Handler;
import com.hionstudios.db.SqlCriteria;
import com.hionstudios.db.SqlQuery;
import com.hionstudios.db.SqlUtil;
import com.hionstudios.zerroo.model.Specification;
import com.hionstudios.zerroo.model.SpecificationGroup;
import com.hionstudios.zerroo.model.SpecificationList;
import com.hionstudios.zerroo.model.SpecificationType;

public class SpecificationTransaction {
    public MapResponse view(DataGridParams params) {
        String sql = "Select Specifications.Id, Specification_Types.Specification \"Type\", Specifications.Value From Specifications Join Specification_Types On Specification_Types.Id = Specifications.Type_Id";
        String count = "Select Count(*) From Specifications";
        String[] columns = { "Type", "Value" };
        HashMap<String, String> sortMapping = new HashMap<>(1);
        sortMapping.put("Type", "\"Type\"");
        params.sortColumn = sortMapping.getOrDefault(params.sortColumn, params.sortColumn);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, null, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }

    public MapResponse addSpecification(long type, String value) {
        CachedSelect.dropCache("specifications");
        CachedSelect.dropCache("specification-type");
        CachedSelect.dropCache("specification-list");
        return new Specification().set("type_id", type).set("value", value).insert() ? MapResponse.success()
                : MapResponse.failure();
    }

    public MapResponse addType(String type) {
        CachedSelect.dropCache("specifications");
        CachedSelect.dropCache("specification-type");
        CachedSelect.dropCache("specification-list");
        return new SpecificationType(type).insert() ? MapResponse.success() : MapResponse.failure();
    }

    public MapResponse types(DataGridParams params) {
        String sql = "Select Id, Specification \"Type\" From Specification_Types";
        String count = "Select Count(*) From Specification_Types";
        String[] columns = { "Type" };
        SqlCriteria criteria = SqlUtil.constructCriteria(params, null, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params);
        return Handler.toDataGrid(new SqlQuery(sql, criteria), new SqlQuery(count, filter), columns);
    }

    public MapResponse viewList(DataGridParams params) {
        String sql = "Select Specification_Lists.Id, Specification_Lists.Id \"Action\", Specification_Lists.Name, (Select Count(*) From Specification_Groups Where List_Id = Specification_Lists.Id) Specifications, (Select Count(*) From Item_Groups Join Items On Items.Group_Id = Item_Groups.Id Where Specification_Id = Specification_Lists.Id) Items From Specification_Lists";
        String count = "Select Count(*) From Specification_Lists";
        String[] columns = { "Action", "Name", "Specifications", "Items" };
        SqlCriteria criteria = SqlUtil.constructCriteria(params, null, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params);
        return Handler.toDataGrid(new SqlQuery(sql, criteria), new SqlQuery(count, filter), columns);
    }

    public MapResponse addList(
            String name,
            long[] specifications) {
        CachedSelect.dropCache("specifications");
        CachedSelect.dropCache("specification-type");
        CachedSelect.dropCache("specification-list");
        SpecificationList list = new SpecificationList();
        list.set("name", name);
        if (list.insert()) {
            long id = list.getLongId();
            for (long s : specifications) {
                if (!new SpecificationGroup().set("list_id", id).set("specification_id", s).insert()) {
                    return MapResponse.failure();
                }
            }
            return MapResponse.success();
        }
        return MapResponse.failure();
    }

    public MapResponse getList(long id) {
        String sql = "Select Specification_Lists.Name, Array_Agg(Specification_Id) Specifications From Specification_Lists Left Join Specification_Groups On Specification_Groups.List_Id = Specification_Lists.Id Where Id = ? Group By Specification_Lists.Id, Specification_Lists.Name";
        return Handler.findFirst(sql, id);
    }

    public MapResponse editSpecificationList(long id, String name, long[] specifications) {
        CachedSelect.dropCache("specifications");
        CachedSelect.dropCache("specification-type");
        CachedSelect.dropCache("specification-list");
        SpecificationList list = SpecificationList.findById(id);
        if (!list.getString("name").equals(name)) {
            if (!list.set("name", name).saveIt()) {
                return MapResponse.failure("Name Error");
            }
        }
        SpecificationGroup.delete("list_id = ?", id);
        for (long s : specifications) {
            if (!new SpecificationGroup().set("list_id", id).set("specification_id", s).insert()) {
                return MapResponse.failure();
            }
        }
        return MapResponse.success();
    }
}
