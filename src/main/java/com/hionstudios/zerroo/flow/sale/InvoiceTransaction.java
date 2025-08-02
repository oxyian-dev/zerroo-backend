package com.hionstudios.zerroo.flow.sale;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.DbUtil;
import com.hionstudios.db.Handler;
import com.hionstudios.db.SqlCriteria;
import com.hionstudios.db.SqlQuery;
import com.hionstudios.db.SqlUtil;
import com.hionstudios.iam.UserUtil;
import com.hionstudios.zerroo.mail.HionTemplateConfig;

public class InvoiceTransaction {
    public MapResponse invoices(DataGridParams params) {
        String sql = "Select Invoices.Id, Invoices.Id \"Action\", Invoices.Time, Invoices.Invoice_Id \"Invoice Id\", Users.Username ZID, Users.Firstname Customer, Sum(Invoice_Items.Price) Price, Sum(Invoice_Items.Pv) PV From Invoices Join Invoice_Items On Invoice_Items.Invoice_Id = Invoices.Id Join Users On Users.Id = Invoices.User_Id";
        String count = "Select Count(Distinct Invoices.Id) From Invoices Join Invoice_Items On Invoice_Items.Invoice_Id = Invoices.Id Join Users On Users.Id = Invoices.User_Id";
        String[] columns = {
                "Action",
                "Time",
                "Invoice Id",
                "ZID",
                "Customer",
                "Price",
                "PV"
        };
        HashMap<String, String> mapping = new HashMap<>(3);
        mapping.put("ZID", "Users.Username");
        mapping.put("Invoice Id", "Invoices.Invoice_Id");
        mapping.put("Customer", "Users.Firstname");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
        }
        String search = params.getSearch();
        SqlCriteria customCriteria = search == null
                ? null
                : new SqlCriteria("(Invoices.Invoice_Id iLike ?)", search);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true,
                "Group By 1, 2, 3, 4, 5, 6");
        SqlCriteria filter = SqlUtil.constructCriteria(params, null, false, "Group By Invoices.Id");
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }

    public MapResponse invoice(long id) {
        if (UserUtil.isDistributor()) {
            String sql = "Select Invoices.Id From Invoices Where Invoices.Id = ? And Invoices.User_Id = ?";
            if (!Handler.exists(sql, id, UserUtil.getUserid())) {
                return null;
            }
        }
        String sql = "Select Invoices.Id, Invoices.Invoice_Id, Invoices.Invoice_Id, Invoices.Billing_Firstname, Invoices.Billing_Lastname, Invoices.Billing_Email, Invoices.Billing_Phone, Invoices.Billing_Address_1, Invoices.Billing_Address_2, Invoices.Billing_City, Invoices.Billing_Postcode, Invoices.Billing_State, Invoices.Billing_Country, Invoices.Shipping_FirstName, Invoices.Shipping_Lastname, Invoices.Shipping_Email, Invoices.Shipping_Phone, Invoices.Shipping_Address_1, Invoices.Shipping_Address_2, Invoices.Shipping_City, Invoices.Shipping_Postcode, Invoices.Shipping_State, Invoices.Shipping_Country, Invoices.Shipping_Fee, Invoices.Time, Sum(Invoice_Items.Basic) Basic, Sum(Invoice_Items.C_Gst) C_Gst, Sum(Invoice_Items.S_Gst) S_Gst, Sum(Invoice_Items.Gst) Gst, Sum(Invoice_Items.I_Gst) I_Gst, Sum(Invoice_Items.Price) Product_Total, Invoices.Shipping_Basic, Invoices.Shipping_C_Gst, Invoices.Shipping_S_Gst, Invoices.Shipping_I_Gst, Sum(Invoice_Items.Price) + Invoices.Shipping_Fee Total, To_Char(To_Timestamp(Invoices.Time/1000) at Time Zone 'Asia/Kolkata', 'DD-MM-YYYY') Date, Users.Username From Invoices Join Invoice_Items On Invoice_Items.Invoice_Id = Invoices.Id Join Users On Users.Id = Invoices.User_Id Where Invoices.Id = ? Group By Invoices.*, Invoices.Id, Username";

        String items = "Select (Select Images.Image From Images Where Images.List_Id = Items.Image_Id Order by Index Asc Limit 1) Image, Items.Title, Items.Sku, Invoice_Items.Mrp, Invoice_Items.Price, Items.Hsn, Invoice_Items.Gst_Percent, Round(((Invoice_Items.Mrp - Invoice_Items.Price) / Invoice_Items.Mrp * 100)) Discount, Count(*) * Price Total, Count(*) quantity From Invoice_Items Join Items On Items.Id = Invoice_Items.Item_Id Join Invoices On Invoices.Id = Invoice_Items.Invoice_Id And Invoices.Id = ? Group By 1, 2, 3, 4, 5, 6, 7, 8";

        MapResponse response = Handler.findFirst(sql, id);
        response.put("items", Handler.findAll(items, id));
        return response;
    }

    public ResponseEntity<byte[]> downloadInvoice(long[] ids) {
        String html = "<html><head><style>div{page-break-inside:avoid;@media print {.pb {page-break-before:always;}}}</style></head><body>";
        String filename = "invoice.pdf";
        try {
            DbUtil.open();
            for (long id : ids) {
                MapResponse invoice = invoice(id);
                if (invoice == null) {
                    continue;
                }
                html += HionTemplateConfig.toString("invoice", invoice);
                if (ids.length == 1) {
                    filename = invoice.getString("invoice_id") + ".pdf";
                }
            }
            html += "</body></html>";
        } finally {
            DbUtil.close();
        }
        ITextRenderer renderer = new ITextRenderer();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextFontResolver resolver = renderer.getFontResolver();
            resolver.addFont(ResourceUtils.getFile("classpath:fonts/Inter-Regular.ttf").getPath(), true);
            resolver.addFont(ResourceUtils.getFile("classpath:fonts/Inter-Bold.ttf").getPath(), true);
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);
            byte[] bytes = outputStream.toByteArray();
            outputStream.close();
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
