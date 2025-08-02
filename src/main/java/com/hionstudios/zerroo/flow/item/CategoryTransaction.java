package com.hionstudios.zerroo.flow.item;

import static com.hionstudios.CommonUtil.categories;

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
import com.hionstudios.zerroo.model.Category;

public class CategoryTransaction {
    public MapResponse view(DataGridParams params) {
        String sql = "Select Categories.Id, Categories.Id \"Action\", Categories.Category, Categories.Image, Parent.Category Parent, Categories.Display, (Select Sum(Stocks.Quantity) From Stocks Join Items On Items.Id = Stocks.Item_Id Join Item_Groups On Item_Groups.Id = Items.Group_Id And Item_Groups.Category_Id = Categories.Id) quantity From Categories Left Join Categories Parent On Parent.Id = Categories.Parent";
        String count = "Select Count(*) From Categories";
        HashMap<String, String> mapping = new HashMap<>(4);
        mapping.put("Category", "Categories.Category");
        mapping.put("Image", "Categories.Image");
        mapping.put("Parent", "Parent.Category");
        mapping.put("Display", "Categories.Display");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.get(params.filterColumn[i]);
        }

        String search = params.getSearch();
        SqlCriteria customCriteria = search == null ? null
                : new SqlCriteria("(Categories.Category iLike ?)", search + "%");
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                "Action", "Category", "Image", "Parent", "Display", "Quantity");
    }

    public MapResponse add(String category, Long parent, MultipartFile image) {
        Category c = new Category();
        c.set("category", category);
        c.set("parent", parent);
        if (c.insert()) {
            CachedSelect.dropCache("category");
            if (image != null) {
                if (c.set("image", ImageUtil.uploadProducts(image, "cat-" + c.getId())).saveIt()) {
                    categories = null;
                    return MapResponse.success();
                }
            } else {
                categories = null;
                return MapResponse.success();
            }
        }
        return MapResponse.failure();
    }

    public MapResponse edit(long id, String category, Long parent, MultipartFile image, boolean removed) {
        Category c = Category.findById(id);
        c.set("category", category);
        c.set("parent", parent);
        String i = c.getString("image");
        if (image != null) {
            c.set("image", ImageUtil.uploadProducts(image, "cat-" + id));
        } else if (i != null && removed) {
            ImageUtil.delete(i);
            c.set("image", null);
        }
        CachedSelect.dropCache("category");
        categories = null;
        return c.saveIt() ? MapResponse.success() : MapResponse.failure();
    }

    public MapResponse updateImage(long id, MultipartFile image) {
        Category category = Category.findById(id);
        String i = category.getString("image");
        if (i != null) {
            ImageUtil.delete(i);
        }
        categories = null;
        return category.set("image", ImageUtil.uploadProducts(image, "cat-" + id)).saveIt() ? MapResponse.success()
                : MapResponse.failure();
    }

    public MapResponse display(long id, boolean display) {
        categories = null;
        return Category.update("display = ?", "id = ?", display, id) == 1 ? MapResponse.success()
                : MapResponse.failure();
    }

    public MapResponse view(long id) {
        String sql = "Select * From Categories Where Id = ?";
        return Handler.findFirst(sql, id);
    }
}
