package com.hionstudios.zerroo.flow.item;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.hionstudios.CachedSelect;
import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.Handler;
import com.hionstudios.db.SqlCriteria;
import com.hionstudios.db.SqlQuery;
import com.hionstudios.db.SqlUtil;
import com.hionstudios.zerroo.flow.ImageUtil;
import com.hionstudios.zerroo.model.Combo;
import com.hionstudios.zerroo.model.ComboGroup;
import com.hionstudios.zerroo.model.ComboGroupItem;
import com.hionstudios.zerroo.model.ComboGroupMapping;
import com.hionstudios.zerroo.oauth.WorkDrive;

public class ComboTransaction {
    public MapResponse combos(DataGridParams params) {
        String sql = "Select Combos.Id, Combos.Id \"Action\", Combos.Image, Combos.Name, Combos.Description, Categories.Category From Combos Join Categories On Categories.Id = Combos.Category_Id";
        String count = "Select Count(*) From Combos Join Categories On Categories.Id = Combos.Category_Id";
        String[] columns = {
                "Action",
                "Image",
                "Name",
                "Description",
                "Category"
        };
        String search = params.getSearch();
        SqlCriteria customCriteria = search == null ? null
                : new SqlCriteria("(Combos.Name iLike ?)",
                        "%" + search + "%");
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }

    public MapResponse combo(String name, long category, String description, MultipartFile image) {
        CachedSelect.dropCache("combo-group");
        String imageId = ImageUtil.upload(image, UUID.randomUUID().toString(), WorkDrive.Folder.COMBO);
        Combo combo = new Combo(name, category, description, imageId);
        return combo.insert() ? MapResponse.success() : MapResponse.failure();
    }

    public MapResponse combo(long id) {
        String sql = "Select Combos.Id, Combos.Name, Combos.Category_Id Category, Combos.Image, Combos.Description From Combos Where Combos.Id = ?";
        return Handler.findFirst(sql, id);
    }

    public MapResponse editCombo(long id,
            String name,
            long category,
            String description,
            MultipartFile image,
            boolean imageChanged) {
        CachedSelect.dropCache("combo-group");
        Combo combo = Combo.findById(id);
        combo.set("name", name);
        combo.set("description", description);
        combo.set("category_id", category);
        if (imageChanged) {
            ImageUtil.delete(combo.getString("image"));
            String imageId = ImageUtil.upload(image, UUID.randomUUID().toString(), WorkDrive.Folder.COMBO);
            combo.set("image", imageId);
        }
        return combo.saveIt() ? MapResponse.success() : MapResponse.failure();
    }

    public MapResponse groups(DataGridParams params) {
        String sql = "Select Combo_Groups.Id, Combo_Groups.Id \"Action\", Combo_Groups.Name, Price_Lists.Price, Price_Lists.Pv, Array(Select Name From Item_Groups Join Combo_Group_Items On Combo_Group_Items.Item_Group_Id = Item_Groups.Id And Combo_Group_Items.Group_Id = Combo_Groups.Id) Items From Combo_Groups Join Price_Lists On Price_Lists.Id = Combo_Groups.Price_Id";
        String count = "Select Count(*) From Combo_Groups Join Price_Lists On Price_Lists.Id = Combo_Groups.Price_Id";
        String[] columns = {
                "Action",
                "Name",
                "Price",
                "PV",
                "Items"
        };
        String search = params.getSearch();
        SqlCriteria customCriteria = search == null ? null
                : new SqlCriteria("(Combo_Groups.Name iLike ?)", "%" + search + "%");
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }

    public MapResponse group(long id) {
        String sql = "Select Name, Description, Price_Id price, Array(Select Id From Item_Groups Join Combo_Group_Items On Combo_Group_Items.Item_Group_Id = Item_Groups.Id And Combo_Group_Items.Group_Id = Combo_Groups.Id) items From Combo_Groups Where Id = ?";
        return Handler.findFirst(sql, id);
    }

    public MapResponse group(String name, String description, long price, long[] items) {
        CachedSelect.dropCache("combo-group");
        ComboGroup comboGroup = new ComboGroup(name, description, price);
        if (comboGroup.insert()) {
            long comboGroupId = comboGroup.getLongId();
            for (int i = 0; i < items.length; i++) {
                if (!new ComboGroupItem(comboGroupId, items[i]).insert()) {
                    return MapResponse.failure();
                }
            }
            return MapResponse.success();
        }
        return MapResponse.failure();
    }

    public MapResponse group(long id, String name, String description, long price, long[] items) {
        CachedSelect.dropCache("combo-group");
        ComboGroup.update("name = ?, description = ?, price_id = ?", "Id = ?", name, description, price, id);
        ComboGroupItem.delete("group_id = ?", id);
        for (int i = 0; i < items.length; i++) {
            if (!new ComboGroupItem(id, items[i]).insert()) {
                return MapResponse.failure();
            }
        }
        return MapResponse.success();
    }

    public MapResponse mapping(long id, DataGridParams params) {
        String sql = "Select Combo_Groups.Id, Combo_Groups.Id \"Action\", Combo_Groups.Name \"Group\", Price_Lists.Price, Price_Lists.Mrp, Combo_Group_Mappings.Quantity From Combo_Group_Mappings Join Combos On Combos.Id = Combo_Group_Mappings.Combo_Id Join Combo_Groups On Combo_Groups.Id = Combo_Group_Mappings.Combo_Group_Id Join Price_Lists On Price_Lists.Id = Combo_Groups.Price_Id";
        String count = "Select Count(*) From Combo_Group_Mappings Join Combos On Combos.Id = Combo_Group_Mappings.Combo_Id Join Combo_Groups On Combo_Groups.Id = Combo_Group_Mappings.Combo_Group_Id Join Price_Lists On Price_Lists.Id = Combo_Groups.Price_Id";
        String[] columns = {
                "Action",
                "Group",
                "Price",
                "Mrp",
                "Quantity"
        };
        SqlCriteria customCriteria = new SqlCriteria("(Combos.Id = ?)", id);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }

    public MapResponse mapping(long id, long group, int quantity) {
        CachedSelect.dropCache("combo-group");
        ComboGroupMapping comboGroupMapping = new ComboGroupMapping(id, group, quantity);
        return comboGroupMapping.insert() ? MapResponse.success() : MapResponse.failure();
    }
}
