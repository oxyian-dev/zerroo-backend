package com.hionstudios;

import java.util.List;

import com.hionstudios.db.Handler;

public class CachedSelect {
    private static MapResponse cachedSelect = new MapResponse();

    private static String getSql(String select) {
        String sql = null;
        switch (select) {
            case "category":
                sql = "Select Categories.Id, Coalesce(Parent.Category || ' - ', '') || Categories.Category \"label\" From Categories Left Join Categories Parent On Parent.Id = Categories.Parent";
                break;
            case "roles":
                sql = "Select Id, Role \"label\" From Roles";
                break;
            case "price":
                sql = "Select Id, Name \"label\" From Price_Lists";
                break;
            case "image":
                sql = "Select Id, Name \"label\" From Image_Lists";
                break;
            case "size":
                sql = "Select Id, Size \"label\" From Sizes Order By Index Asc";
                break;
            case "color":
                sql = "Select Id, Color \"label\" From Colors";
                break;
            case "group":
                sql = "Select Id, Name \"label\" From Item_Groups";
                break;
            case "specifications":
                sql = "Select Specifications.Id, Specification || ': ' || Value \"label\" From Specifications Join Specification_Types On Specification_Types.Id = Specifications.Type_Id";
                break;
            case "specification-type":
                sql = "Select Id, Specification \"label\" From Specification_Types";
                break;
            case "item":
                sql = "Select Items.Id, Title || Coalesce(' - ' || Sizes.Size, '') \"label\" From Items Left Join Sizes On Sizes.Id = Items.Size_Id";
                break;
            case "inventory":
                sql = "Select Id, Inventory \"label\" From Inventories";
                break;
            case "courier":
                sql = "Select Id, Courier \"label\" From Couriers";
                break;
            case "branch":
                sql = "Select Id, Branch \"label\" From Branches";
                break;
            case "transporter":
                sql = "Select Id, Transporter \"label\" From Transporters";
                break;
            case "brand":
                sql = "Select Id, Brand \"label\" From Brands";
                break;
            case "specification-list":
                sql = "Select Id, Name \"label\" From Specification_Lists";
                break;
            case "combo-group":
                sql = "Select Id, Name \"label\" From Combo_Groups";
                break;
        }
        return sql;
    }

    public MapResponse select(String select) {
        String sql = getSql(select);
        MapResponse response = cachedSelect.getMap(sql);
        if (response == null) {
            List<MapResponse> list = Handler.findAll(sql);
            response = new MapResponse().put("options", list);
            cachedSelect.put(sql, response);
        }
        return response;
    }

    public MapResponse dynamicSelect(String select) {
        String sql = getSql(select);
        MapResponse response = cachedSelect.getMap(sql);
        if (response == null) {
            response = new MapResponse().put("options", Handler.firstColumn(sql));
            cachedSelect.put(sql, response);
        }
        return response;
    }

    public static void dropCache(String select) {
        cachedSelect.remove(getSql(select));
    }
}
