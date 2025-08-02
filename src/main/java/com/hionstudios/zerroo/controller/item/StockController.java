package com.hionstudios.zerroo.controller.item;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.DbTransaction;
import com.hionstudios.iam.IsAdmin;
import com.hionstudios.zerroo.flow.item.StockTransaction;

@RestController
@RequestMapping("api/stocks")
public class StockController {
        @GetMapping
        @IsAdmin
        public ResponseEntity<MapResponse> view(DataGridParams params) {
                return ((DbTransaction) () -> new StockTransaction().view(params)).read();
        }

        @PutMapping("{id}/location")
        @IsAdmin
        public ResponseEntity<MapResponse> location(@PathVariable long id, @RequestParam String location) {
                return ((DbTransaction) () -> new StockTransaction().location(id, location)).write();
        }

        @GetMapping("inward")
        @IsAdmin
        public ResponseEntity<MapResponse> inward(DataGridParams params) {
                return ((DbTransaction) () -> new StockTransaction().inward(params)).read();
        }

        @GetMapping("inward/{id}/items")
        @IsAdmin
        public ResponseEntity<MapResponse> inwardItems(
                        @PathVariable long id) {
                return ((DbTransaction) () -> new StockTransaction().inwardItems(id)).read();
        }

        @GetMapping("inward/{id}")
        @IsAdmin
        public ResponseEntity<MapResponse> inward(
                        @PathVariable long id) {
                return ((DbTransaction) () -> new StockTransaction().inwardDetails(id)).read();
        }

        @PostMapping("inward")
        @IsAdmin
        public ResponseEntity<MapResponse> inward(
                        @RequestParam String description,
                        @RequestParam(required = false) String ref,
                        @RequestParam long[] items,
                        @RequestParam int[] quantities,
                        @RequestParam long[] inventories) {
                return ((DbTransaction) () -> new StockTransaction().addInward(
                                description, ref, items, quantities, inventories)).write();
        }

        @GetMapping("items/{item}")
        @IsAdmin
        public ResponseEntity<MapResponse> availableStock(
                        @PathVariable long item,
                        @RequestParam long inventory) {
                return ((DbTransaction) () -> new StockTransaction().availableStock(item, inventory)).read();
        }

        @PostMapping("adjustment")
        @IsAdmin
        public ResponseEntity<MapResponse> addAdjustment(
                        @RequestParam String description,
                        @RequestParam String reason,
                        @RequestParam long inventory,
                        @RequestParam long[] items,
                        @RequestParam int[] adjustments) {
                return ((DbTransaction) () -> new StockTransaction().addAdjustment(
                                description, reason, inventory, items, adjustments)).write();
        }

        @GetMapping("adjustment")
        @IsAdmin
        public ResponseEntity<MapResponse> adjustment(DataGridParams params) {
                return ((DbTransaction) () -> new StockTransaction().adjustment(params)).read();
        }

        @GetMapping("adjustment/{id}")
        @IsAdmin
        public ResponseEntity<MapResponse> adjustment(
                        @PathVariable long id) {
                return ((DbTransaction) () -> new StockTransaction().adjustment(id)).read();
        }

        @GetMapping("adjustment/{id}/items")
        @IsAdmin
        public ResponseEntity<MapResponse> adjustmentItems(
                        @PathVariable long id) {
                return ((DbTransaction) () -> new StockTransaction().adjustmentItems(id)).read();
        }

        @PostMapping("transfer")
        @IsAdmin
        public ResponseEntity<MapResponse> transfer(
                        @RequestParam String description,
                        @RequestParam String reason,
                        @RequestParam long[] items,
                        @RequestParam int[] quantities,
                        @RequestParam long[] froms,
                        @RequestParam long[] tos) {
                return ((DbTransaction) () -> new StockTransaction().transfer(
                                description, reason, items, quantities, froms, tos)).write();
        }

        @GetMapping("transfer")
        @IsAdmin
        public ResponseEntity<MapResponse> transfer(DataGridParams params) {
                return ((DbTransaction) () -> new StockTransaction().transfer(params)).write();
        }

        @GetMapping("transfer/{id}")
        @IsAdmin
        public ResponseEntity<MapResponse> transfer(
                        @PathVariable long id) {
                return ((DbTransaction) () -> new StockTransaction().transfer(id)).read();
        }

        @GetMapping("transfer/{id}/items")
        @IsAdmin
        public ResponseEntity<MapResponse> transferItems(
                        @PathVariable long id) {
                return ((DbTransaction) () -> new StockTransaction().transferItems(id)).read();
        }
}
