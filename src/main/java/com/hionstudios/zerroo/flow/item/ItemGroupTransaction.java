package com.hionstudios.zerroo.flow.item;

import java.util.HashMap;

import org.springframework.web.multipart.MultipartFile;

import com.hionstudios.CachedSelect;
import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.Handler;
import com.hionstudios.db.SqlCriteria;
import com.hionstudios.db.SqlQuery;
import com.hionstudios.db.SqlUtil;
import com.hionstudios.zerroo.flow.ImageUtil;
import com.hionstudios.zerroo.model.ItemGroup;

public class ItemGroupTransaction {
    public MapResponse addGroup(
            String name,
            long category,
            long brand,
            Long specification,
            MultipartFile size) {
        ItemGroup group = new ItemGroup(name, category, brand, specification);
        group.insert();
        CachedSelect.dropCache("group");
        if (size != null) {
            String chart = ImageUtil.uploadProducts(size, group.getId() + "-size-chart");
            group.set("size_chart", chart).saveIt();
        }
        return MapResponse.success();
    }

    public MapResponse editGroup(
            long id,
            String name,
            long category,
            long brand,
            Long specification,
            MultipartFile size,
            boolean removed) {
        ItemGroup group = ItemGroup.findById(id);
        group.set("name", name);
        group.set("category_id", category);
        group.set("brand_id", brand);
        group.set("specification_id", specification);
        String i = group.getString("size_chart");
        CachedSelect.dropCache("group");
        if (size != null) {
            String chart = ImageUtil.uploadProducts(size, group.getId() + "-size-chart");
            group.set("size_chart", chart);
        } else if (i != null && removed) {
            ImageUtil.delete(i);
            group.set("size_chart", null);
        }
        group.saveIt();
        return MapResponse.success();
    }

    public MapResponse view(long id) {
        String sql = "Select Id, Name, Category_Id \"category\", specification_id specification, brand_id brand, size_chart size From Item_Groups Where Id = ?";
        return Handler.findFirst(sql, id);
    }

    public MapResponse view(DataGridParams params) {
        String sql = "Select Item_Groups.Id, Item_Groups.Id \"Action\", Item_Groups.Name, Categories.Category, Specification_Lists.Name Specification, Brands.Brand, Size_Chart \"Size Chart\" From Item_Groups Join Categories On Categories.Id = Item_Groups.Category_Id Join Brands On Brands.Id = Item_Groups.Brand_Id Left Join Specification_Lists On Specification_Lists.Id = Item_Groups.Specification_Id";
        String count = "Select Count(*) From Item_Groups Join Categories On Categories.Id = Item_Groups.Category_Id Join Brands On Brands.Id = Item_Groups.Brand_Id Left Join Specification_Lists On Specification_Lists.Id = Item_Groups.Specification_Id";
        HashMap<String, String> mapping = new HashMap<>(2);
        mapping.put("Specification", "Specification_Lists.Name");
        mapping.put("Size Chart", "Size_Chart");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.get(params.filterColumn[i]);
        }
        String[] columns = {
                "Action",
                "Name",
                "Category",
                "Brand",
                "Specification",
                "Size Chart"
        };
        SqlCriteria criteria = SqlUtil.constructCriteria(params, null, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }
}
