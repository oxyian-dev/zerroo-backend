package com.hionstudios.zerroo.controller.sale;

import javax.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;

import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
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

    @GetMapping(value = "pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> invoice(
            @RequestParam long[] ids,
            HttpServletResponse response) {
        return new InvoiceTransaction().downloadInvoice(ids);
    }

    // Lightweight test route to validate security/path without DB or templates
    @GetMapping(value = "pdf/test", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> testPdf() {
        try {
            // Tiny one-page PDF using basic PDF header. Using iTextRenderer here would require HTML; keep minimal.
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // Minimal valid PDF bytes (simple generated doc)
            String pdf = "%PDF-1.4\n" +
                         "1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj\n" +
                         "2 0 obj<</Type/Pages/Count 1/Kids[3 0 R]>>endobj\n" +
                         "3 0 obj<</Type/Page/Parent 2 0 R/MediaBox[0 0 300 144]/Contents 4 0 R/Resources<</Font<</F1 5 0 R>>>>>>endobj\n" +
                         "4 0 obj<</Length 55>>stream\nBT /F1 24 Tf 72 100 Td (Invoice PDF endpoint OK) Tj ET\nendstream endobj\n" +
                         "5 0 obj<</Type/Font/Subtype/Type1/BaseFont/Helvetica>>endobj\n" +
                         "xref\n0 6\n0000000000 65535 f \n0000000010 00000 n \n0000000060 00000 n \n0000000112 00000 n \n0000000286 00000 n \n0000000404 00000 n \ntrailer<</Size 6/Root 1 0 R>>\nstartxref\n490\n%%EOF";
            out.write(pdf.getBytes());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "test.pdf");
            return ResponseEntity.ok().headers(headers).body(out.toByteArray());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
