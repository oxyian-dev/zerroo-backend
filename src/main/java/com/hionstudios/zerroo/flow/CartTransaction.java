package com.hionstudios.zerroo.flow;

import java.util.UUID;

import com.hionstudios.MapResponse;
import com.hionstudios.db.Handler;
import com.hionstudios.iam.UserUtil;
import com.hionstudios.zerroo.model.Cart;
import com.hionstudios.time.TimeUtil;

public class CartTransaction {

    public MapResponse add(long item) {
        return addToCart(UserUtil.getUserid(), item, 1) ? MapResponse.success() : MapResponse.failure();
    }

    public MapResponse sync(long[] items, int[] quantities, long[] times) {
        long user = UserUtil.getUserid();
        for (int i = 0; i < items.length; i++) {
            long item = items[i];
            int quantity = quantities[i];
            long time = times[i];
            if (!addToCart(user, item, quantity, time)) {
                return MapResponse.failure();
            }
        }
        return MapResponse.success();
    }

    public boolean addToCart(long user, long item, int quantity) {
        return addToCart(user, item, quantity, TimeUtil.currentTime());
    }

    public boolean addToCart(long user, long item, int quantity, long time) {
        Cart cart = Cart.findFirst("user_id = ? And item_id = ?", user, item);
        if (cart == null) {
            cart = new Cart(user, item, quantity, time);
            return cart.insert();
        } else {
            int currentQuantity = cart.getInteger("quantity");
            cart.set("quantity", currentQuantity + quantity);
            return cart.saveIt();
        }
    }

    public MapResponse get() {
        String sql = "Select Items.Id Item, Items.Title, Items.Description, Categories.Category, Brands.Brand, Price_Lists.Price, Price_Lists.Mrp, Round((Price_Lists.Mrp - Price_Lists.Price) / Price_Lists.Mrp * 100) Discount, Price_Lists.Pv, Array(Select Images.Image From Images Where List_Id = Items.Image_Id) Images, Carts.Quantity, Sizes.Size, Sizes.Id Size_Id, Colors.Color, Colors.Id Color_Id, Colors.Hex, Carts.Time, Coalesce(Stocks.Quantity, 0) Stock, Carts.Combo_Id, Carts.Unique_Id From Carts Join Items On Items.Id = Carts.Item_Id Join Item_Groups On Item_Groups.Id = Items.Group_Id Join Categories On Categories.Id = Item_Groups.Category_Id Left Join Sizes On Sizes.Id = Items.Size_Id Left Join Colors On Colors.Id = Items.Color_Id Join Brands On Brands.Id = Item_Groups.Brand_Id Left Join Combo_Groups On Combo_Groups.Id = Carts.Combo_Group_Id Join Price_Lists On Price_Lists.Id = (Case When Combo_Groups.Price_Id Is Not Null Then Combo_Groups.Price_Id Else Items.Price_Id End) Left Join Stocks On Stocks.Item_Id = Items.Id Where Carts.User_Id = ? Order By Carts.Time";
        long userid = UserUtil.getUserid();
        MapResponse response = new MapResponse(2);
        response.put("carts", Handler.findAll(sql, userid));
        response.put("purchase_wallet", Handler.get("Select Purchase_Wallet From Distributors Where Id = ?", userid));
        return response;
    }

    public MapResponse delete(long item, String uid) {
        long userid = UserUtil.getUserid();
        if (uid != null && !"".equals(uid)) {
            Cart.delete("Unique_id = ? And User_Id = ?", uid, userid);
            MapResponse response = get();
            response.put("status", "success");
            return response;
        } else if (Cart.delete("user_id = ? And item_id = ?", userid, item) == 1) {
            MapResponse response = get();
            response.put("status", "success");
            return response;
        }
        return MapResponse.failure();
    }

    public MapResponse count() {
        long userid = UserUtil.getUserid();
        String sql = "Select Sum(Quantity) \"count\" From Carts Where User_Id = ?";
        return Handler.findFirst(sql, userid);
    }

    public MapResponse setCount(long item, int quantity) {
        long userid = UserUtil.getUserid();
        if (Cart.update("quantity = ?", "item_id = ? And user_id = ?", quantity, item, userid) > 0) {
            MapResponse response = get();
            response.put("status", "success");
            return response;
        } else {
            return MapResponse.failure();
        }
    }

    public static void clearCart() {
        long userid = UserUtil.getUserid();
        Cart.delete("user_id = ?", userid);
    }

    public MapResponse addCombo(long[] items, long[] combos, long[] groups) {
        String unique = UUID.randomUUID().toString();
        long userid = UserUtil.getUserid();
        long time = TimeUtil.currentTime();
        for (int i = 0; i < items.length; i++) {
            long combo = combos[i];
            long group = groups[i];
            long item = items[i];
            Cart cart = new Cart(unique, combo, group, userid, item, time);
            if (!cart.insert()) {
                return MapResponse.failure("Try again");
            }
        }
        return MapResponse.success();
    }
}
