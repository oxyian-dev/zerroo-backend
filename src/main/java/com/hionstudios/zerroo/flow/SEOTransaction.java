package com.hionstudios.zerroo.flow;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.hionstudios.MapResponse;
import com.hionstudios.StringUtil;
import com.hionstudios.db.DbUtil;
import com.hionstudios.db.Handler;

public class SEOTransaction {

    public static void product() throws IOException {
        try {
            StringBuilder xml = new StringBuilder();
            DbUtil.open();
            String sql = "Select Items.Id, Items.Title, Categories.Category, (Select Image From Images Where Images.List_Id = Items.Image_Id Order By Index Limit 1) Image From Items Join Item_Groups On Item_Groups.Id = Items.Group_Id Join Categories On Categories.Id = Item_Groups.Category_Id";

            List<MapResponse> list = Handler.findAll(sql);
            for (MapResponse response : list) {
                xml
                        .append("<url><loc>https://www.zerroo.in/p/")
                        .append(response.get("id"))
                        .append("/")
                        .append(StringUtil.seoEncode(response.getString("category")))
                        .append("/")
                        .append(StringUtil.seoEncode(response.getString("title")))
                        .append("</loc><lastmod>2024-01-25</lastmod><changefreq>monthly</changefreq><priority>1.0</priority></url>");
            }
            try (FileWriter writer = new FileWriter(new File("files/xml/product.xml"))) {
                writer.write(xml.toString().toCharArray());
            }
        } finally {
            DbUtil.close();
        }
    }

    public static void category() throws IOException {
        try {
            StringBuilder xml = new StringBuilder();
            DbUtil.open();
            String sql = "Select Categories.Category, Categories.Id, Parent.Category Parent From Categories Left Join Categories Parent On Categories.Parent = Parent.Id";
            List<MapResponse> list = Handler.findAll(sql);
            for (MapResponse response : list) {
                String parent = response.getString("parent");
                xml
                        .append("<url><loc>https://www.zerroo.in/c/")
                        .append(response.get("id"))
                        .append("/")
                        .append(StringUtil.seoEncode(response.getString("category")));
                if (parent != null) {
                    xml
                            .append("/")
                            .append(StringUtil.seoEncode(parent));
                }
                xml.append(
                        "</loc><lastmod>2024-01-25</lastmod><changefreq>monthly</changefreq><priority>1.0</priority></url>");
            }
            try (FileWriter writer = new FileWriter(new File("files/xml/category.xml"))) {
                writer.write(xml.toString().toCharArray());
            }
        } finally {
            DbUtil.close();
        }
    }
}
