package com.hionstudios.zerroo.flow.item;

import java.util.HashMap;
import java.util.Objects;

import com.hionstudios.CachedSelect;
import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.Handler;
import com.hionstudios.db.SqlCriteria;
import com.hionstudios.db.SqlQuery;
import com.hionstudios.db.SqlUtil;
import com.hionstudios.iam.UserUtil;
import com.hionstudios.zerroo.model.Item;
import com.hionstudios.zerroo.model.ItemHistory;
import com.hionstudios.time.TimeUtil;

public class ItemTransaction {
    public MapResponse view(DataGridParams params) {
        String sql = "Select Items.Id, Items.Id \"Action\", (Select Array[Image, Image_Lists.Id::Text] From Images Join Image_Lists On Image_Lists.Id = Images.List_Id Where Image_Lists.Id = Items.Image_Id Order By Index Limit 1) Image, Item_Groups.Name \"Group\", SKU, Title, Online_Status \"Online Status\",Featured_Status \"Featured Status\", Price_Lists.Mrp, Price_Lists.Price, Sizes.Size, Colors.Hex Color From Items Join Item_Groups On Item_Groups.Id = Items.Group_Id Left Join Price_Lists On Price_Lists.Id = Items.Price_Id Left Join Sizes On Sizes.Id = Items.Size_Id Left Join Colors On Colors.Id = Items.Color_Id";
        String count = "Select Count(*) From Items Join Item_Groups On Item_Groups.Id = Items.Group_Id Join Price_Lists On Price_Lists.Id = Items.Price_Id Left Join Sizes On Sizes.Id = Items.Size_Id Left Join Colors On Colors.Id = Items.Color_Id";
        String[] columns = {
                "Action",
                "Image",
                "Group",
                "SKU",
                "Title",
                "Online Status",
                "Mrp",
                "Price",
                "Size",
                "Color",
                "Featured Status"
        };
        HashMap<String, String> mapping = new HashMap<>(2);
        mapping.put("Group", "Item_Groups.Name");
        mapping.put("Price", "Price_Lists.Name");
        mapping.put("Online Status", "Online_Status");
        mapping.put("Featured Status", "Featured_Status");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
        }
        HashMap<String, String> sortMapping = new HashMap<>(1);
        sortMapping.put("Group", "\"Group\"");
        String search = params.getSearch();
        SqlCriteria customCriteria = search == null ? null
                : new SqlCriteria("(Items.Title iLike ?) Or Items.Sku = ?", "%" + search + "%", search);
        params.sortColumn = sortMapping.getOrDefault(params.sortColumn, params.sortColumn);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }

    public MapResponse addItem(
            long group,
            String sku,
            String hsn,
            String title,
            String description,
            long price,
            Long image,
            Long size,
            Long color,
            Double weight,
            Double length,
            Double breadth,
            Double height) {
        Item item = new Item();
        Item.constructItem(group,
                sku,
                hsn,
                title,
                description,
                price,
                image,
                size,
                color,
                weight,
                length,
                breadth,
                height,
                item);
        item.set("created_time", TimeUtil.currentTime());
        item.set("created_by", UserUtil.getUserid());
        CachedSelect.dropCache("item");
        return item.insert() ? MapResponse.success() : MapResponse.failure();
    }

    public MapResponse get(long id) {
        String sql = "Select Group_Id \"group\", Sku, Title, Description, Hsn, Price_id Price, Size_Id Size, Color_Id Color, Weight, Length, Breadth, Height, Image_id Image From Items Where Id = ?";
        return Handler.findFirst(sql, id);
    }

    public MapResponse get(String sku) {
        String sql = "Select Group_Id \"group\", Sku, Title, Description, Hsn, Price_id Price, Size_Id Size, Color_Id Color, Weight, Length, Breadth, Height, Image_id Image From Items Where Items.Sku = ?";
        return Handler.findFirst(sql, sku);
    }

    public MapResponse edit(
            long id,
            long group,
            String sku,
            String hsn,
            String title,
            String description,
            long price,
            long image,
            Long size,
            Long color,
            Double weight,
            Double length,
            Double breadth,
            Double height,
            String reason) {
        Item item = Item.findById(id);
        long user = UserUtil.getUserid();
        long time = TimeUtil.currentTime();
        addHistory(id, "Group", item.getLong("group_id"), group, reason, user, time);
        addHistory(id, "SKU", item.getString("sku"), sku, reason, user, time);
        addHistory(id, "HSN", item.getString("hsn"), hsn, reason, user, time);
        addHistory(id, "Title", item.getString("title"), title, reason, user, time);
        addHistory(id, "Description", item.getString("description"), description, reason, user, time);
        addHistory(id, "Price", item.getLong("price_id"), price, reason, user, time);
        addHistory(id, "Image", item.getLong("image_id"), image, reason, user, time);
        addHistory(id, "Size", item.getLong("size_id"), size, reason, user, time);
        addHistory(id, "Color", item.getLong("color_id"), color, reason, user, time);
        addHistory(id, "Weight", item.getDouble("weight"), weight, reason, user, time);
        addHistory(id, "Length", item.getDouble("length"), length, reason, user, time);
        addHistory(id, "Breadth", item.getDouble("breadth"), breadth, reason, user, time);
        addHistory(id, "Height", item.getDouble("height"), height, reason, user, time);
        Item.constructItem(group,
                sku,
                hsn,
                title,
                description,
                price,
                image,
                size,
                color,
                weight,
                length,
                breadth,
                height,
                item);
        item.set("last_modified_time", time);
        item.set("last_modified_by", user);
        CachedSelect.dropCache("item");
        return item.saveIt() ? MapResponse.success() : MapResponse.failure();
    }

    private static void addHistory(
            long item,
            String field,
            Object oldValue,
            Object newValue,
            String reason,
            long user,
            long time) {
        if (!Objects.equals(oldValue, newValue)) {
            ItemHistory itemHistory = new ItemHistory();
            itemHistory.set("item_id", item);
            itemHistory.set("field", field);
            itemHistory.set("old_value", String.valueOf(oldValue));
            itemHistory.set("new_value", String.valueOf(newValue));
            itemHistory.set("owner_id", user);
            itemHistory.set("time", time);
            itemHistory.set("reason", reason);
            itemHistory.insert();
        }
    }

    public MapResponse status(long id, boolean status, String reason) {
        if (Item.update("online_status = ?", "id = ?", status, id) == 1) {
            addHistory(id, "Online Status", !status, status, reason, UserUtil.getUserid(), TimeUtil.currentTime());
            return MapResponse.success();
        }
        return MapResponse.failure();
    }

    public MapResponse featured_status(long id, boolean featured_status) {
        if (Item.update("featured_status = ?", "id = ?", featured_status, id) == 1) {
            return MapResponse.success();
        }
        return MapResponse.failure();
    }

    private boolean isUnique(long group, Long size, Long color) {
        String sizeCriteria = size == null ? "Size_Id Is ?" : "Size_Id = ?";
        String colorCriteria = size == null ? "Color_Id Is ?" : "Color_Id = ?";
        String sql = "Select Id From Items Where Group_Id = ? And (" + sizeCriteria
                + ") And (" + colorCriteria + ")";
        return !Handler.exists(sql, group, size, color);
    }
}
