package com.hionstudios.zerroo.controller.sale;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.DbTransaction;
import com.hionstudios.iam.IsAdmin;
import com.hionstudios.zerroo.flow.sale.InvoiceTransaction;

@RequestMapping("api/invoices")
@RestController
public class InvoiceController {
    @GetMapping
    @IsAdmin
    public ResponseEntity<MapResponse> invoices(DataGridParams params) {
        return ((DbTransaction) () -> new InvoiceTransaction().invoices(params)).read();
    }

    @GetMapping("pdf")
    @IsAdmin
    public ResponseEntity<byte[]> invoice(
            @RequestParam long[] ids,
            HttpServletResponse response) {
        return new InvoiceTransaction().downloadInvoice(ids);
    }
}
