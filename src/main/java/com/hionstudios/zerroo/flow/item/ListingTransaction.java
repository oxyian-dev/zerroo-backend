package com.hionstudios.zerroo.flow.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hionstudios.MapResponse;
import com.hionstudios.db.Handler;
import com.hionstudios.db.SqlCriteria;
import com.hionstudios.db.SqlQuery;
import com.hionstudios.db.SqlUtil;

public class ListingTransaction {

    public MapResponse listing(
            long category,
            String sort,
            List<String> f_category,
            List<String> f_size,
            List<String> f_color,
            List<String> f_brand,
            List<Double> f_price,
            List<Integer> f_discount) {

        // Sorting
        String direction;
        if (sort == null) {
            sort = "Price_Lists.Price";
            direction = "Asc";
        } else if (sort.equals("latest")) {
            sort = "Items.Created_Time";
            direction = "Desc";
        } else if (sort.equals("price-desc")) {
            sort = "Price_Lists.Price";
            direction = "Desc";
        } else if (sort.equals("price-asc")) {
            sort = "Price_Lists.Price";
            direction = "Asc";
        } else if (sort.equals("discount")) {
            sort = "Discount";
            direction = "Desc";
        } else {
            sort = "Price_Lists.Price";
            direction = "Asc";
        }

        // Filtering
        ArrayList<String> filter = new ArrayList<>(6);
        if (f_category != null && f_category.size() > 0) {
            filter.add("(Categories.Id In (" + String.join(",", f_category) + "))");
        }

        if (f_brand != null && f_brand.size() > 0) {
            filter.add("(Brands.Id In (" + String.join(",", f_brand) + "))");
        }

        if (f_size != null && f_size.size() > 0) {
            filter.add("(Items.Size_Id In (" + String.join(",", f_size) + ") Or Items.Size_Id Is Null)");
        }

        if (f_color != null && f_color.size() > 0) {
            filter.add("(Colors.Id In (" + String.join(",", f_color) + "))");
        }

        if (f_price != null && f_price.size() == 2) {
            filter.add("(Price_Lists.Price Between " + f_price.get(0) + " And " + f_price.get(1) + " )");
        }

        if (f_discount != null) {
            filter.add("(Round((Price_Lists.Mrp - Price_Lists.Price) / Price_Lists.Mrp * 100) >= "
                    + Collections.min(f_discount) + ")");
        }
        String filterStr = filter.size() > 0 ? "And " + String.join("And", filter) : "";

        String customCriteriaStr = "Items.Online_Status And (Categories.Id In (With Recursive Child as (Select Id, Parent From Categories Where Id = ? Union All Select Categories.Id, Categories.Parent From Categories Join Child On Child.Id = Categories.Parent) Select Id from Child))";

        SqlCriteria customCriteria = new SqlCriteria(customCriteriaStr + filterStr, category);
        SqlCriteria criteria = SqlUtil.constructCriteria(customCriteria, sort, direction);
        String sql = "Select Items.Id, Items.Group_Id, Items.Size_Id, Items.Sku, Items.Title, Items.Description, Brands.Brand, Categories.Category, Price_Lists.Mrp, Price_Lists.Price, Round(((Price_Lists.Mrp - Price_Lists.Price) / Price_Lists.Mrp * 100)) Discount, Price_Lists.Pv, (Select Image From Images Where List_Id = Items.Image_Id Order By Index Limit 1) Image, Colors.Color, Colors.Hex, Stocks.Quantity From Items Join Item_Groups On Item_Groups.Id = Items.Group_Id Join Categories On Categories.Id = Item_Groups.Category_Id Join Price_Lists On Price_Lists.Id = Items.Price_Id Left Join Image_Lists On Image_Lists.Id = Items.Image_Id Join Brands On Brands.Id = Item_Groups.Brand_Id Left Join Colors On Colors.Id = Items.Color_Id Join Stocks On Stocks.Item_Id = Items.Id And (Stocks.Quantity > 0)";

        SqlQuery query = new SqlQuery(sql, criteria);
        List<MapResponse> listings = Handler.findAll(query);
        MapResponse response = new MapResponse(2);
        response.put("listing", listings);
        response.put("category", new CategoryTransaction().view(category));
        return response;
    }

    /**
     * Construct all the Filter params for the Shop Listing page
     *
     * @param category Category ID
     * @return Filter options
     */
    public MapResponse filters(long category) {
        // Category Criteria
        String customCriteria = "Where Items.Online_Status And Price_Lists.Pv > 0 And (Category_Id In (With Recursive Child as (Select Id, Parent From Categories Where Id = ? Union All Select Categories.Id, Categories.Parent From Categories Join Child On Child.Id = Categories.Parent) Select Id from Child))";

        // Brand Filters and Count
        String brandSql = "Select Brands.Id, Brands.Brand, Count(*) From Items Join Item_Groups On Item_Groups.Id = Items.Group_Id Join Stocks On Stocks.Item_Id = Items.Id And (Stocks.Quantity > 0) Join Price_Lists On Price_Lists.Id = Items.Price_Id Left Join Brands On Brands.Id = Item_Groups.Brand_Id "
                + customCriteria + " Group By 1, 2 Order By 3 Desc";
        List<MapResponse> brands = Handler.findAll(brandSql, category);

        // Color Filters and Count
        String colorSql = "Select Colors.Id, Colors.Color, Colors.Hex, Count(*) From Items Join Item_Groups On Item_Groups.Id = Items.Group_Id Join Stocks On Stocks.Item_Id = Items.Id And (Stocks.Quantity > 0) Join Price_Lists On Price_Lists.Id = Items.Price_Id Join Colors On Colors.Id = Items.Color_Id "
                + customCriteria + " Group By 1, 2, 3 Order By 4 Desc";
        List<MapResponse> colors = Handler.findAll(colorSql, category);

        // Category Filters and Count
        String categorySql = "Select Categories.Id, Categories.Category, Count(*) From Items Join Item_Groups On Item_Groups.Id = Items.Group_Id Join Stocks On Stocks.Item_Id = Items.Id And (Stocks.Quantity > 0) Join Price_Lists On Price_Lists.Id = Items.Price_Id Join Categories On Categories.Id = Item_Groups.Category_Id "
                + customCriteria + " Group By 1, 2 Order By 3 Desc";
        List<MapResponse> categories = Handler.findAll(categorySql, category);

        // Size Filters and Count
        String sizeSql = "Select Sizes.Id, Sizes.Size, Count(*) From Items Join Item_Groups On Item_Groups.Id = Items.Group_Id Join Price_Lists On Price_Lists.Id = Items.Price_Id Join Stocks On Stocks.Item_Id = Items.Id And (Stocks.Quantity > 0) Join Sizes On Sizes.Id = Items.Size_Id "
                + customCriteria + " Group By 1, 2 Order By Sizes.Index";
        List<MapResponse> sizes = Handler.findAll(sizeSql, category);

        // Discount Filters and Count
        String discountSql = "Select Case When Discount >= 0 Then 0 When Discount >= 10 Then 10 When Discount >= 20 Then 20 When Discount >= 30 Then 30 When Discount >= 40 Then 40 When Discount >= 50 Then 50 When Discount >= 60 Then 60 When Discount >= 70 Then 70 When Discount >= 80 Then 80 When Discount >= 90 Then 90 End As Discount, Count (*) From (Select Round((Price_Lists.Mrp - Price_Lists.Price) / Price_Lists.Mrp * 100) Discount From Items Join Stocks On Stocks.Item_Id = Items.Id And (Stocks.Quantity > 0) Join Price_Lists On Price_Lists.Id = Items.Price_Id Join Item_Groups On Item_Groups.Id = Items.Group_Id "
                + customCriteria + ") as discount Group By 1";
        List<MapResponse> discount = Handler.findAll(discountSql, category);

        // Price Range Filter
        String priceSql = "Select Price_Lists.Price, Count(*) From Items Join Item_Groups On Item_Groups.Id = Items.Group_Id Join Stocks On Stocks.Item_Id = Items.Id And (Stocks.Quantity > 0) Left Join Price_Lists On Price_Lists.Id = Items.Price_Id "
                + customCriteria + " Group By 1";
        List<MapResponse> price = Handler.findAll(priceSql, category);

        MapResponse filters = new MapResponse(6);
        filters.put("brands", brands);
        filters.put("colors", colors);
        filters.put("categories", categories);
        filters.put("sizes", sizes);
        filters.put("discount", discount);
        filters.put("price", price);
        return filters;
    }

    public MapResponse item(long id) {
        String sql = "Select Items.Id, Items.Title, Items.Description, Items.Group_Id, Item_Groups.Category_Id, Categories.Category, Items.Size_Id, Sizes.Size, Items.Color_Id, Colors.Color, Colors.Hex, Brands.Brand, Price_Lists.Mrp, Price_Lists.Price, Round(((Price_Lists.Mrp - Price_Lists.Price) / Price_Lists.Mrp * 100)) Discount, Price_Lists.Pv, Array(Select Images.Image From Images Where Images.List_Id = Items.Image_Id Order By Index) Images, Coalesce(Stocks.Quantity, 0) Quantity From Items Join Item_Groups On Item_Groups.Id = Items.Group_Id Join Categories On Categories.Id = Item_Groups.Category_Id Join Brands On Brands.Id = Item_Groups.Brand_Id Join Price_Lists On Price_Lists.Id = Items.Price_Id Left Join Sizes On Sizes.Id = Items.Size_Id Left Join Colors On Colors.Id = Items.Color_Id Join Stocks On Stocks.Item_Id = Items.Id Where Items.Id = ?";
        MapResponse item = Handler.findFirst(sql, id);
        assert item != null;
        Long color = item.getLong("color_id");
        long group = item.getLong("group_id");

        // Get all that Item Group
        String colorSql = "Select Min(Items.Id) Id, Min(Items.Title) Title, Colors.Id Color_Id, Colors.Color, Colors.Hex From Items Join Colors On Colors.Id = Items.Color_Id Where Items.Group_Id = (Select Group_Id From Items Where Items.Id = ?) Group By Colors.Id, Colors.Color, Colors.Hex";
        List<MapResponse> colors = Handler.findAll(colorSql, id);

        // Get all the Sizes of the same Coloured Item
        String sizeSql = "Select Items.Id, Items.Title, Size_Id, Sizes.Size From Items Join Sizes On Sizes.Id = Items.Size_Id Left Join Colors On Colors.Id = Items.Color_Id Where Items.Group_Id = (Select Group_Id From Items Where Items.Id = ?) And (Items.Color_Id "
                + (color == null ? "Is Null" : "= " + color) + ") Order By Sizes.Index";
        List<MapResponse> sizes = Handler.findAll(sizeSql, id);

        // Get all the Specifications of that Item Group
        String specificationSql = "Select Specifications.Id, Specification_Types.Specification, Specifications.Value From Specifications Join Specification_Types On Specification_Types.Id = Specifications.Type_Id Join Specification_Groups On Specification_Groups.Specification_Id = Specifications.Id And Specification_Groups.List_Id = ?";
        List<MapResponse> specifications = Handler.findAll(specificationSql, group);

        MapResponse response = new MapResponse();
        response.put("item", item);
        response.put("sizes", sizes);
        response.put("colors", colors);
        response.put("specifications", specifications);
        return response;
    }

    public MapResponse children(long id) {
        MapResponse response = new MapResponse(1);
        response.put("categories", childCategories(id));
        return response;
    }

    public List<MapResponse> childCategories(long id) {
        String sql = "Select Id, Category, Image, Display From Categories Where Parent = ?";
        List<MapResponse> children = new ArrayList<>();
        List<MapResponse> categories = Handler.findAll(sql, id);
        for (MapResponse category : categories) {
            if (category.getBoolean("display")) {
                children.add(category);
            }
            children.addAll(childCategories(category.getLong("id")));
        }
        return children;
    }

    public MapResponse combos(long category) {
        String sql = "Select Combos.Id, Combos.Name, Combos.Description, Combos.Image, Categories.Category, (Select Json_Build_Object('mrp', Sum(Quantity * Mrp), 'price', Sum(Quantity * Price), 'discount', (Sum(Quantity * Mrp) - Sum(Quantity * Price)) / Sum(Quantity * Mrp) * 100) From Price_Lists Join Combo_Groups On Combo_Groups.Price_Id = Price_Lists.Id Join Combo_Group_Mappings On Combo_Group_Mappings.Combo_Group_Id = Combo_Groups.Id) Price From Combos Join Categories On Categories.Id = Combos.Category_Id Join Combo_Group_Mappings On Combo_Group_Mappings.Combo_Id = Combos.Id Join Combo_Groups On Combo_Groups.Id = Combo_Group_Mappings.Combo_Group_Id Join Combo_Group_Items On Combo_Group_Items.Group_Id = Combo_Group_Mappings.Combo_Group_Id Join Item_Groups On Item_Groups.Id = Combo_Group_Items.Item_Group_Id Join Items On Items.Group_Id = Combo_Group_Items.Item_Group_Id Join Stocks On Stocks.Item_Id = Items.Id Where Combos.Category_Id = ? Group By Combos.Id, Combos.Name, Combos.Description, Combos.Image, Categories.Category Having Sum(Stocks.Quantity) >= Sum(Combo_Group_Mappings.Quantity)";
        List<MapResponse> combos = Handler.findAll(sql, category);
        MapResponse response = new MapResponse();
        response.put("combos", combos);
        return response;
    }

    public MapResponse combo(long id) {
        String sql = "Select Combos.Name, Combos.Description, Array_Agg(Json_Build_Object('name', Combo_Groups.Name, 'description', Combo_Groups.Description, 'quantity', Combo_Group_Mappings.Quantity, 'combo_group_id', Combo_Group_Mappings.Combo_Group_Id, 'items', Array(Select Json_Build_Object('item_id', Items.Id, 'title', title, 'description', description, 'group_id', Combo_Group_Items.Item_Group_Id, 'image', (Select Image From Images Where List_Id = Items.Image_Id Order By Index Limit 1), 'size', Sizes.Size, 'size_id', Sizes.Id, 'color', Colors.Color, 'color_id', Colors.Id) From Items Join Stocks On Stocks.Item_Id = Items.Id And (Stocks.Quantity > 0) Left Join Sizes On Sizes.Id = Items.Size_Id Left Join Colors On Colors.Id = Items.Color_Id Join Combo_Group_Items On Combo_Group_Items.Item_Group_Id = Items.Group_Id And Combo_Group_Items.Group_Id = Combo_Groups.Id Order By Items.Group_Id, Items.Id))) \"groups\" From Combos Join Combo_Group_Mappings On Combo_Group_Mappings.Combo_Id = Combos.Id Join Combo_Groups On Combo_Groups.Id = Combo_Group_Mappings.Combo_Group_Id Join Price_Lists On Price_Lists.Id = Combo_Groups.Price_Id Where Combos.Id = ? Group By Combos.Name, Combos.Description";
        return Handler.findFirst(sql, id);
    }
}