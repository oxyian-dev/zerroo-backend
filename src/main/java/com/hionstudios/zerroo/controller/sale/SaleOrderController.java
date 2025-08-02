package com.hionstudios.zerroo.controller.sale;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.DbConnection;
import com.hionstudios.db.DbTransaction;
import com.hionstudios.iam.IsAdmin;
import com.hionstudios.zerroo.flow.sale.SaleOrderTransaction;
import com.hionstudios.zerroo.oauth.ZohoBooks;

@RestController
@RequestMapping("api/sale-orders")
public class SaleOrderController {
    @GetMapping
    @IsAdmin
    public ResponseEntity<MapResponse> saleOrders(DataGridParams params) {
        return ((DbTransaction) () -> new SaleOrderTransaction().saleOrders(params)).read();
    }

    @GetMapping("un-shipped")
    @IsAdmin
    public ResponseEntity<MapResponse> unShippedSaleOrders(DataGridParams params) {
        return ((DbTransaction) () -> new SaleOrderTransaction().unShippedSaleOrders(params)).read();
    }

    @GetMapping("shipped")
    @IsAdmin
    public ResponseEntity<MapResponse> shippedSaleOrders(DataGridParams params) {
        return ((DbTransaction) () -> new SaleOrderTransaction().shippedSaleOrders(params)).read();
    }

    @GetMapping("{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> get(@PathVariable long id) {
        return ((DbTransaction) () -> new SaleOrderTransaction().get(id)).read();
    }

    @PostMapping("{id}/ship")
    @IsAdmin
    public ResponseEntity<MapResponse> ship(@PathVariable long id) {
        return ((DbTransaction) () -> new SaleOrderTransaction().ship(id)).read();
    }

    @GetMapping("distributors/{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> saleOrders(
            @PathVariable long id,
            DataGridParams params) {
        return ((DbTransaction) () -> new SaleOrderTransaction().saleOrders(id, params)).read();
    }

    @PostMapping("export")
    @IsAdmin
    public void export(
            @RequestParam long from,
            @RequestParam long to,
            HttpServletResponse response) {
        ((DbConnection) () -> ZohoBooks.saleOrders(from, to, response)).read();
    }

    @GetMapping("count")
    @IsAdmin
    public ResponseEntity<MapResponse> count() {
        return ((DbTransaction) () -> new SaleOrderTransaction().count()).read();
    }
}
