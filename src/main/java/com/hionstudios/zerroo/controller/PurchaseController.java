package com.hionstudios.zerroo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hionstudios.MapResponse;
import com.hionstudios.db.DbTransaction;
import com.hionstudios.iam.IsAuthenticatedUser;
import com.hionstudios.iam.IsDistributor;
import com.hionstudios.zerroo.flow.sale.InvoiceTransaction;
import com.hionstudios.zerroo.flow.sale.PurchaseTransaction;

@RestController
@RequestMapping("api/purchases")
public class PurchaseController {
    @GetMapping("shipping")
    @IsAuthenticatedUser
    public ResponseEntity<MapResponse> shipping() {
        return ResponseEntity.ok(MapResponse.success().put("shipping_charge", PurchaseTransaction.shipping(true)));
    }

    @GetMapping
    @IsAuthenticatedUser
    public synchronized ResponseEntity<MapResponse> purchases() {
        return ((DbTransaction) () -> new PurchaseTransaction().purchases()).read();
    }

    @PostMapping
    @IsAuthenticatedUser
    public synchronized ResponseEntity<MapResponse> purchase(
            @RequestParam long address,
            @RequestParam boolean shipping) {
        return ((DbTransaction) () -> new PurchaseTransaction().purchase(address, shipping))
                .write();
    }

    @GetMapping("download")
    @IsAuthenticatedUser
    public ResponseEntity<byte[]> downloadInvoice(@RequestParam long[] invoice_id) {
        return new InvoiceTransaction().downloadInvoice(invoice_id);
    }
}
