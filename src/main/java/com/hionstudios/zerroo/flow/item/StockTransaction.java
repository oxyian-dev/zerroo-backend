package com.hionstudios.zerroo.flow.item;

import static com.hionstudios.MapResponse.failure;

import java.util.HashMap;

import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.Handler;
import com.hionstudios.db.SqlCriteria;
import com.hionstudios.db.SqlQuery;
import com.hionstudios.db.SqlUtil;
import com.hionstudios.zerroo.model.AdjustmentItem;
import com.hionstudios.zerroo.model.Inventory;
import com.hionstudios.zerroo.model.InwardItem;
import com.hionstudios.zerroo.model.Item;
import com.hionstudios.zerroo.model.Stock;
import com.hionstudios.zerroo.model.StockAdjustment;
import com.hionstudios.zerroo.model.StockInward;
import com.hionstudios.zerroo.model.StockLedger;
import com.hionstudios.zerroo.model.StockLedgerType;
import com.hionstudios.zerroo.model.StockTransfer;
import com.hionstudios.zerroo.model.TransferItem;

public class StockTransaction {
    public MapResponse view(DataGridParams params) {
        String sql = "Select Stocks.Id, (Select Image From Images Where Images.List_Id = Items.Image_Id Order By Index Asc Limit 1) Image, Items.SKU, Stocks.Location, Items.Title, Stocks.Quantity, Inventories.Inventory, Categories.Category, Sizes.Size From Stocks Join Items On Items.Id = Stocks.Item_Id Join Inventories On Inventories.Id = Stocks.Inventory_Id Join Item_Groups On Item_Groups.Id = Items.Group_Id Join Categories On Categories.Id = Item_Groups.Category_Id Left Join Sizes On Sizes.Id = Items.Size_Id";
        String count = "Select Count(*) From Stocks";
        String[] columns = { "Image", "SKU", "Location", "Title", "Quantity", "Inventory", "Category", "Size" };
        HashMap<String, String> mapping = new HashMap<>(1);
        mapping.put("Quantity", "Stocks.Quantity");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
        }
        String search = params.getSearch();
        SqlCriteria customCriteria = search == null ? null : new SqlCriteria("(Items.Sku iLike ?)", search);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }

    public MapResponse location(long id, String location) {
        if ("null".equals(location)) {
            location = null;
        }
        Stock.update("location = ?", "Id = ?", location, id);
        return MapResponse.success();
    }

    public MapResponse addInward(String description, String ref, long[] items, int[] quantities, long[] inventories) {
        StockInward inward = new StockInward(description, ref);
        if (inward.insert()) {
            long inwardId = inward.getLongId();
            for (int i = 0; i < items.length; i++) {
                if (quantities[i] < 1) {
                    return failure("Quantity should be positive");
                }
                if (!new InwardItem(items[i], inwardId, quantities[i], inventories[i]).insert()) {
                    return failure("Check the Values");
                }
                if (!addStock(items[i], inventories[i], quantities[i], StockLedgerType.INWARD)) {
                    return failure("Check the Values");
                }
            }
        }
        return MapResponse.success();
    }

    private boolean addStock(long item, long inventory, int quantity, String type) {
        Stock stock = Stock.findFirst("item_id = ? And Inventory_Id = ?", item, inventory);
        new StockLedger(item, inventory, quantity, type).insert();
        if (stock != null) {
            return stock.set("quantity", stock.getInteger("quantity") + quantity).saveIt();
        } else {
            return new Stock(item, inventory, quantity).insert();
        }
    }

    public static boolean minusStock(long itemId, int quantity, String type) {
        if (quantity <= 0) {
            return false;
        }
        long inventoty = Inventory.getDefaultInventory();
        new StockLedger(itemId, inventoty, -quantity, type).insert();
        Stock.update("quantity = quantity - ?",
                "item_id = ? And Inventory_Id = ?",
                quantity, itemId, inventoty);
        return true;
    }

    public MapResponse inward(DataGridParams params) {
        String sql = "Select Stock_Inwards.Id, Stock_Inwards.Id \"Action\", Stock_Inwards.Description, (Select Sum(Quantity) From Inward_Items Where Inward_Items.Inward_Id = Stock_Inwards.Id) Quantity, Users.Firstname \"Owner\", Stock_Inwards.Time From Stock_Inwards Join Users On Users.Id = Stock_Inwards.Owner_Id";
        String count = "Select Count(*) From Stock_Inwards";
        String[] columns = { "Action", "Description", "Quantity", "Owner", "Time" };
        HashMap<String, String> mapping = new HashMap<>(1);
        mapping.put("Owner", "Users.Firstname");
        HashMap<String, String> sortMapping = new HashMap<>(1);
        sortMapping.put("Owner", "\"Owner\"");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
        }
        params.sortColumn = sortMapping.getOrDefault(params.sortColumn, params.sortColumn);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, null, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }

    public MapResponse inwardItems(long id) {
        String sql = "Select Inward_Items.Id, (Select Image From Images Where Images.List_Id = Items.Image_Id Limit 1) Image, Items.SKU, Items.Title, Inward_Items.Quantity, Inventories.Inventory From Inward_Items Join Items On Items.Id = Inward_Items.Item_Id Join Inventories On Inventories.Id = Inward_Items.Inventory_Id";
        String count = "Select Count(*) From Inward_Items";
        String[] columns = { "Image", "SKU", "Title", "Quantity", "Inventory" };
        SqlCriteria criteria = new SqlCriteria("Where Inward_Items.Inward_Id = ?", id);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, criteria),
                columns);
    }

    public MapResponse inwardDetails(long id) {
        String sql = "Select Description, Ref_Id, Owner_Id, Firstname, time From Stock_Inwards Join Users On Users.Id = Stock_Inwards.Owner_Id Where Stock_Inwards.Id = ?";
        return Handler.findFirst(sql, id);
    }

    public MapResponse availableStock(long item, long inventory) {
        String sql = "Select Quantity From Stocks Where Item_Id = ? And Inventory_Id = ?";
        return new MapResponse().put("quantity", Handler.getInt(sql, item, inventory));
    }

    public MapResponse addAdjustment(
            String description,
            String reason,
            long inventory,
            long[] items,
            int[] adjustments) {
        StockAdjustment adjustment = new StockAdjustment(description, reason, inventory);
        if (adjustment.insert()) {
            long id = adjustment.getLongId();
            for (int i = 0; i < items.length; i++) {
                Integer available = availableStock(items[i], inventory).getInt("quantity");
                if (available == null) {
                    available = 0;
                }
                if (available == adjustments[i]) {
                    return failure("Available and Quantity shouldn't be the same");
                }
                if (!new AdjustmentItem(
                        id,
                        items[i],
                        available,
                        available + adjustments[i],
                        adjustments[i])
                        .insert()) {
                    return failure("Check the Values");
                }
                if (!addStock(items[i], inventory, adjustments[i], StockLedgerType.ADJUSTMENT)) {
                    return failure("Check the Values");
                }
                if (Item.update("quantity = quantity + ?", "id = ?", adjustments[i], items[i]) != 1) {
                    return failure();
                }
            }
        }
        return MapResponse.success();
    }

    public MapResponse adjustment(DataGridParams params) {
        String sql = "Select Stock_Adjustments.Id, Stock_Adjustments.Id \"Action\", Stock_Adjustments.Description, Stock_Adjustments.Reason, Users.Firstname \"Owner\", Stock_Adjustments.Time From Stock_Adjustments Join Users On Users.Id = Stock_Adjustments.Owner_Id";
        String count = "Select Count(*) From Stock_Adjustments";
        String[] columns = { "Action", "Description", "Reason", "Owner", "Time" };
        HashMap<String, String> mapping = new HashMap<>(1);
        mapping.put("Owner", "Users.Firstname");
        HashMap<String, String> sortMapping = new HashMap<>(1);
        sortMapping.put("Owner", "\"Owner\"");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
        }
        params.sortColumn = sortMapping.getOrDefault(params.sortColumn, params.sortColumn);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, null, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }

    public MapResponse adjustment(long id) {
        String sql = "Select Description, Reason, Inventories.Inventory, Time, Users.Firstname From Stock_Adjustments Join Users On Users.Id = Stock_Adjustments.Owner_Id Join Inventories On Inventories.Id = Stock_Adjustments.Inventory_Id Where Stock_Adjustments.Id = ?";
        return Handler.findFirst(sql, id);
    }

    public MapResponse adjustmentItems(long id) {
        String sql = "Select Adjustment_Items.Id, (Select Image From Images Where Images.List_Id = Items.Image_Id Limit 1) Image, Items.SKU, Items.Title, Adjustment_Items.Initial, Adjustment_Items.Final, Adjustment_Items.Adjustment From Adjustment_Items Join Items On Items.Id = Adjustment_Items.Item_Id";
        String count = "Select Count(*) From Adjustment_Items";
        String[] columns = { "Image", "SKU", "Title", "Initial", "Final", "Adjustment" };
        SqlCriteria criteria = new SqlCriteria("Where Adjustment_Items.Adjustment_Id = ?", id);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, criteria),
                columns);
    }

    public MapResponse transfer(
            String description, String reason, long[] items, int[] quantities, long[] froms, long[] tos) {
        StockTransfer transfer = new StockTransfer(description, reason);
        if (transfer.insert()) {
            long id = transfer.getLongId();
            for (int i = 0; i < items.length; i++) {
                if (froms[i] == tos[i]) {
                    return failure("Source and destination cannot be the same");
                }
                if (!new TransferItem(id, items[i], quantities[i], froms[i], tos[i]).insert()) {
                    return failure("Check the values");
                }
                Integer available = availableStock(items[i], froms[i]).getInt("quantity");
                if (available == null || available < quantities[i]) {
                    return failure("Source is less than available");
                }
                if (!addStock(items[i], froms[i], quantities[i] * -1, StockLedgerType.TRANSFER_FROM)) {
                    return failure("Check the Values");
                }
                if (!addStock(items[i], tos[i], quantities[i], StockLedgerType.TRANSFER_TO)) {
                    return failure("Check the Values");
                }
            }
            return MapResponse.success();
        }
        return failure();
    }

    public MapResponse transfer(DataGridParams params) {
        String sql = "Select Stock_Transfers.Id, Stock_Transfers.Id \"Action\", Stock_Transfers.Description, Stock_Transfers.Reason, Users.Firstname \"Owner\", Stock_Transfers.Time, (Select Sum(Quantity) From Transfer_Items Where Transfer_Items.Transfer_Id = Transfer_Items.Id) Quantity From Stock_Transfers Join Users On Users.Id = Stock_Transfers.Owner_Id";
        String count = "Select Count(*) From Stock_Transfers";
        String[] columns = { "Action", "Description", "Reason", "Owner", "Time", "Quantity" };
        HashMap<String, String> mapping = new HashMap<>(1);
        mapping.put("Owner", "Users.Firstname");
        HashMap<String, String> sortMapping = new HashMap<>(1);
        sortMapping.put("Owner", "\"Owner\"");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
        }
        params.sortColumn = sortMapping.getOrDefault(params.sortColumn, params.sortColumn);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, null, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }

    public MapResponse transfer(long id) {
        String sql = "Select Description, Reason, Time, Users.Firstname, (Select Sum(Quantity) From Transfer_Items Where Transfer_Items.Transfer_id = Stock_Transfers.Id) Quantity From Stock_Transfers Join Users On Users.Id = Stock_Transfers.Owner_Id Where Stock_Transfers.Id = ?";
        return Handler.findFirst(sql, id);
    }

    public MapResponse transferItems(long id) {
        String sql = "Select Transfer_Items.Id, (Select Image From Images Where Images.List_Id = Items.Image_Id Limit 1) Image, Items.SKU, Items.Title, Transfer_Items.Quantity, From_Inventory.Inventory \"From\", To_Inventory.Inventory \"To\" From Transfer_Items Join Items On Items.Id = Transfer_Items.Item_Id Join Inventories From_Inventory On From_Inventory.Id = Transfer_Items.From_inventory Join Inventories To_Inventory On To_Inventory.Id = Transfer_Items.To_inventory";
        String count = "Select Count(*) From Transfer_Items";
        String[] columns = { "Image", "SKU", "Title", "Quantity", "From", "To" };
        SqlCriteria criteria = new SqlCriteria("Where Transfer_Items.Transfer_Id = ?", id);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, criteria),
                columns);
    }

    public static boolean isAvailable(long item, int quantity) {
        String sql = "Select Sum(Quantity)::Int From Stocks Where Item_Id = ? And Inventory_Id = ?";
        return Handler.getInt(sql, item, Inventory.getDefaultInventory()) >= quantity;
    }
}
