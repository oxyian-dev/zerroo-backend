package com.hionstudios.zerroo.flow.sale;

import java.util.List;

import com.hionstudios.MapResponse;
import com.hionstudios.db.Handler;
import com.hionstudios.iam.UserUtil;
import com.hionstudios.time.TimeUtil;
import com.hionstudios.zerroo.flow.CartTransaction;
import com.hionstudios.zerroo.flow.GenealogyUtil;
import com.hionstudios.zerroo.flow.PurchaseWalletFlow;
import com.hionstudios.zerroo.flow.item.StockTransaction;
import com.hionstudios.zerroo.mail.MailSenderFrom;
import com.hionstudios.zerroo.mail.MailUtil;
import com.hionstudios.zerroo.model.Address;
import com.hionstudios.zerroo.model.Cutoff;
import com.hionstudios.zerroo.model.PurchaseWalletTransactionType;
import com.hionstudios.zerroo.model.SaleOrder;
import com.hionstudios.zerroo.model.SaleOrderItem;
import com.hionstudios.zerroo.model.StockLedgerType;
import com.hionstudios.zerroo.params.OrderParams;

public class PurchaseTransaction {
    public MapResponse purchase(long addressId, boolean shipping) {
        long time = TimeUtil.currentTime();
        long userid = UserUtil.getUserid();
        Address[] addresses = getAddresses(userid, addressId);
        Address shippingAddress = addresses[0];
        Address billingAddress = addresses[0];
        OrderParams params = new OrderParams();
        constructParam(params, shippingAddress, billingAddress);
        String sql = "Select Carts.Quantity, Carts.Item_Id, Price_Lists.Mrp, Price_Lists.Price, Price_Lists.Cost, Price_Lists.Gst_Percent, Price_Lists.Pv, Carts.Combo_Id From Carts Join Items On Items.Id = Carts.Item_Id Left Join Combo_Groups On Combo_Groups.Id = Carts.Combo_Group_Id Join Price_Lists On Price_Lists.Id = (Case When Combo_Groups.Price_Id Is Not Null Then Combo_Groups.Price_Id Else Items.Price_Id End) Where Carts.User_Id = ?";
        List<MapResponse> cartItems = Handler.findAll(sql, userid);
        if (cartItems.size() == 0) {
            return MapResponse.failure("Not Cart Items Found");
        }
        double totalPrice = 0, shippingCharge = shipping(shipping);
        for (MapResponse cartItem : cartItems) {
            long itemId = cartItem.getLong("item_id");
            int quantity = cartItem.getInt("quantity");
            double price = cartItem.getDouble("price");
            if (!StockTransaction.isAvailable(itemId, quantity)) {
                return MapResponse.failure("Insufficient Stock");
            }
            totalPrice += price * quantity;
        }
        double purchaseWallet = Handler.getDouble("Select Purchase_Wallet From Distributors Where Id = ?", userid);
        if (purchaseWallet < (totalPrice + shippingCharge)) {
            return MapResponse.failure("Insufficient funds.");
        } else {
            PurchaseWalletFlow.minus(userid, totalPrice + shippingCharge, PurchaseWalletTransactionType.PURCHASE);
        }
        boolean isTn = params.getShippingState().equals("Tamil Nadu");
        long currentCutoffId = Cutoff.getCurrentId();
        SaleOrder saleOrder = new SaleOrder(
                params,
                time,
                userid,
                currentCutoffId,
                shippingCharge,
                isTn);

        if (!saleOrder.insert()) {
            return MapResponse.failure("Try again");
        }

        long saleOrderId = saleOrder.getLongId();
        double totalPv = 0;

        for (MapResponse cartItem : cartItems) {
            long itemId = cartItem.getLong("item_id");
            Long comboId = cartItem.getLong("combo_id");
            double mrp = cartItem.getDouble("mrp");
            double price = cartItem.getDouble("price");
            double cost = cartItem.getDouble("cost");
            int gstPercent = cartItem.getInt("gst_percent");
            double pv = cartItem.getDouble("pv");
            int quantity = cartItem.getInt("quantity");
            totalPv += pv * quantity;
            for (int i = 0; i < quantity; i++) {
                SaleOrderItem saleOrderItem = new SaleOrderItem(
                        saleOrderId,
                        itemId,
                        comboId,
                        mrp,
                        price,
                        cost,
                        gstPercent,
                        pv,
                        isTn);
                if (!saleOrderItem.insert()) {
                    return MapResponse.failure("Try again");
                }
            }
            StockTransaction.minusStock(itemId, quantity, StockLedgerType.SALES);
        }
        if (totalPv > 0) {
            GenealogyUtil.addPv(userid, totalPv);
        }
        CartTransaction.clearCart();

        String name = UserUtil.getFirstname();
        String email = UserUtil.getEmail();

        String htmlContent = String.format(
                "<html> <head> <title>Order Placed Successfully</title></head> <body>"
                        + "<p>Dear %s,</p>"
                        + "<p>Thank you for placing your order with us. We have received your request and are currently processing it.</p>"
                        + "<p>Your order ID is <strong>%d</strong>.</p>"
                        + "<p>Once your order is shipped, we will notify you with the tracking details.</p>"
                        + "<p>If you have any further questions, please do not hesitate to contact our support team.</p>"
                        + "<p>Thank you for choosing us. We look forward to serving you again!</p>"
                        + "<p>Best regards,<br>Zerroo Team</p>",
                name, saleOrderId);

        MailUtil.sendMailAsync(
                MailSenderFrom.noReply(),
                email,
                "Order Placed Successfully",
                htmlContent,
                true);
        return MapResponse.success();
    }

    private static void constructParam(OrderParams params, Address shippingAddress, Address billingAddress) {
        params.setShippingFirstName(shippingAddress.getString("firstname"));
        params.setBillingFirstName(billingAddress.getString("firstname"));

        params.setShippingLastName(shippingAddress.getString("lastname"));
        params.setBillingLastName(billingAddress.getString("lastname"));

        params.setShippingPhone(shippingAddress.getString("phone"));
        params.setBillingPhone(billingAddress.getString("phone"));

        params.setShippingAltPhone(shippingAddress.getString("alt_phone"));

        params.setShippingEmail(shippingAddress.getString("email"));
        params.setBillingEmail(billingAddress.getString("email"));

        params.setShippingAddress1(shippingAddress.getString("address_1"));
        params.setBillingAddress1(billingAddress.getString("address_1"));

        params.setShippingAddress2(shippingAddress.getString("address_2"));
        params.setBillingAddress2(billingAddress.getString("address_2"));

        params.setShippingPostcode(shippingAddress.getString("postcode"));
        params.setBillingPostcode(billingAddress.getString("postcode"));

        params.setShippingLandmark(shippingAddress.getString("landmark"));

        params.setShippingCity(shippingAddress.getString("city"));
        params.setBillingCity(billingAddress.getString("city"));

        params.setShippingState(shippingAddress.getString("state"));
        params.setBillingState(billingAddress.getString("state"));

        params.setShippingCountry(shippingAddress.getString("country"));
        params.setBillingCountry(billingAddress.getString("country"));
    }

    /**
     * Return the Shipping and Billing Address
     * The default Address is the Billing Address and the selected address is the
     * Shipping Address
     * If there is no default address present for the user, the selected address is
     * the Billing Address
     * 
     * @param userId    Current user
     * @param addressId Selected address
     * @return Array of Shipping and Billing Address
     */
    private static Address[] getAddresses(long userId, long addressId) {
        List<Address> addresses = Address.find("Distributor_Id = ? And (Id = ? Or Is_Default)",
                userId, addressId).limit(2).orderBy("is_default");
        if (addresses.size() == 1) {
            Address address = addresses.get(0);
            return new Address[] { address, address };
        } else {
            Address billingAddress = addresses.get(0);
            Address shippingAddress = addresses.get(1);
            return new Address[] { shippingAddress, billingAddress };
        }
    }

    public static double shipping(boolean shipping) {
        return shipping ? 100 : 0;
    }

    public MapResponse purchases() {
        long userid = UserUtil.getUserid();

        String sql = "Select Forward_Shipments.Invoice_Id, Sale_Orders.Order_Id, Sale_Orders.Time, Array_Agg(Json_Build_Object('item_id', Items.Id, 'title', Items.Title, 'description', Items.Description, 'category', Categories.Category, 'size', Sizes.size, 'image', (Select Images.Image From Images Where Images.List_Id = Items.Image_Id Limit 1), 'price', Sale_Order_Items.Price, 'mrp', Sale_Order_Items.Mrp, 'discount', Round(((Sale_Order_Items.Mrp - Sale_Order_Items.Price) / Sale_Order_Items.Mrp * 100)), 'status', Sale_Order_Item_Statuses.Status, 'track', Replace(Couriers.Tracking_Url, '${awb}', Forward_Shipments.awb))) Items, Sale_Orders.Shipping_Fee, Sale_Orders.Shipping_Fee + Sum(Sale_Order_Items.Price) Total From Sale_Orders Join Sale_Order_Items On Sale_Order_Items.Order_Id = Sale_Orders.Id Join Items On Items.Id = Sale_Order_Items.Item_Id Join Item_Groups On Item_Groups.Id = Items.Group_Id Join Categories On Categories.Id = Item_Groups.Category_Id Left Join Sizes On Sizes.Id = Items.Size_Id Join Sale_Order_Item_Statuses On Sale_Order_Item_Statuses.Id = Sale_Order_Items.Status_Id Left Join Forward_Shipments On Forward_Shipments.Id = Sale_Orders.Shipment_Id Left Join Couriers On Couriers.Id = Forward_Shipments.Courier_Id Where Sale_Orders.User_Id = ? Group By Sale_Orders.Order_Id, Forward_Shipments.Invoice_Id, Sale_Orders.Time, Sale_Orders.Shipping_Fee Order By Time Desc";
        MapResponse response = new MapResponse();
        response.put("orders", Handler.findAll(sql, userid));
        return response;
    }
}
