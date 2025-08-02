package com.hionstudios.zerroo;

import java.util.HashMap;

import org.springframework.web.servlet.ModelAndView;

import com.hionstudios.MapResponse;
import com.hionstudios.StringUtil;
import com.hionstudios.db.DbUtil;
import com.hionstudios.db.Handler;
import com.hionstudios.zerroo.oauth.WorkDrive;

public class MetaObject {
    private final HashMap<String, Object> metaObject = new HashMap<>();
    private static final String TITLE = "Zerroo | Online Fashion Store";
    private static final String DESCRIPTION = "Zerroo is the first Community Commerce Business combined with Ecommerce having a wide variety of Products, provide Entrepreneurship training, to start their Business";
    private static final String URL = "https://www.zerroo.in";
    private static final String IMAGE = "https://www.zerroo.in/og.png";
    private static final String KEYWORDS = "zerroo,business,earn money,ecommerce,shopping,store,tshirt,shirt,pant,kurti,entrepreneurship";

    public static MetaObject preset() {
        return Generator.preset();
    }

    public static MetaObject login() {
        return Generator.login();
    }

    public static MetaObject admin() {
        return Generator.admin();
    }

    public HashMap<String, ?> toMap() {
        return metaObject;
    }

    public MetaObject setTitle(String title) {
        metaObject.put("title", title);
        return this;
    }

    public MetaObject setAllTitle(String title) {
        return setTitle(title).setOgTitle(title);
    }

    public MetaObject setDescription(String description) {
        metaObject.put("description", description);
        return this;
    }

    public MetaObject setAllDescription(String description) {
        return setDescription(description).setOgDescription(description);
    }

    public MetaObject setUrl(String url) {
        metaObject.put("url", url);
        return this;
    }

    public MetaObject setAllUrl(String url) {
        return setUrl(url).setOgUrl(url);
    }

    public MetaObject setImage(String image) {
        metaObject.put("image", image);
        return this;
    }

    public MetaObject setAllImage(String image) {
        return setImage(image).setOgImage(image);
    }

    public MetaObject setKeywords(String keywords) {
        metaObject.put("keywords", keywords);
        return this;
    }

    public MetaObject setOgTitle(String title) {
        metaObject.put("og_title", title);
        return this;
    }

    public MetaObject setOgDescription(String description) {
        metaObject.put("og_description", description);
        return this;
    }

    public MetaObject setOgImage(String image) {
        metaObject.put("og_image", image);
        return this;
    }

    public MetaObject setOgUrl(String url) {
        metaObject.put("og_url", url);
        return this;
    }

    public ModelAndView toModel() {
        return toModel("index");
    }

    public ModelAndView toModel(String view) {
        HashMap<String, ?> meta = toMap();
        ModelAndView modelAndView = new ModelAndView(view);
        meta.forEach(modelAndView::addObject);
        return modelAndView;
    }

    public static ModelAndView generate(String x, String y) {
        if (x == null) {
            return Generator.preset().toModel();
        }
        switch (x) {
            case "login":
                return Generator.login().toModel();
            case "admin":
                return Generator.admin().toModel();
            case "distributor":
                return Generator.distributor().toModel();
            case "c":
                return Generator.category(y).toModel();
            case "p":
                return Generator.product(y).toModel();
            default:
                return Generator.preset().toModel();
        }
    }

    private static class Generator {
        public static MetaObject login() {
            return new MetaObject()
                    .setAllTitle("Zerroo | Login")
                    .setAllDescription(
                            "Login into your Zerroo User Account and make Purchases, Track your Performances and Earn More")
                    .setAllUrl("https://www.zerroo.in/login")
                    .setAllImage(IMAGE).setKeywords(KEYWORDS);
        }

        public static MetaObject preset() {
            return new MetaObject()
                    .setAllTitle(TITLE)
                    .setAllDescription(DESCRIPTION)
                    .setAllUrl(URL)
                    .setAllImage(IMAGE)
                    .setKeywords(KEYWORDS);
        }

        public static MetaObject admin() {
            return new MetaObject()
                    .setAllTitle("Zerroo - Welcome Admin")
                    .setAllDescription("Zerroo - Admin")
                    .setAllUrl("https://www.zerroo.in/admin")
                    .setAllImage(IMAGE)
                    .setKeywords(KEYWORDS);

        }

        public static MetaObject distributor() {
            return new MetaObject()
                    .setAllTitle("Zerroo - Welcome Distributor")
                    .setAllDescription("Zerroo - Distributor")
                    .setAllUrl("https://www.zerroo.in/dashboard")
                    .setAllImage(IMAGE)
                    .setKeywords(KEYWORDS);

        }

        public static MetaObject category(String category) {
            try {
                long id = Long.parseLong(category);
                DbUtil.open();
                String sql = "Select Categories.Category, Categories.Id, Parent.Category Parent From Categories Left Join Categories Parent On Categories.Parent = Parent.Id Where Categories.Id = ?";
                MapResponse response = Handler.findFirst(sql, id);
                String categoryName = response.getString("category");
                String parent = response.getString("parent");
                String img = response.getString("image");

                String url = parent == null
                        ? "https://www.zerroo.in/c/" + id + "/" + StringUtil.seoEncode(categoryName)
                        : "https://www.zerroo.in/c/" + id + "/" + StringUtil.seoEncode(categoryName) + "/"
                                + StringUtil.seoEncode(parent);

                return new MetaObject()
                        .setAllTitle(String.format("Buy %s at Zerroo Online Store",
                                categoryName))
                        .setAllDescription(String.format("Buy %s at Zerroo Online Store",
                                categoryName))
                        .setAllUrl(url)
                        .setAllImage(WorkDrive.toImg(img))
                        .setKeywords(KEYWORDS);
            } catch (Exception e) {
                return Generator.preset();
            } finally {
                DbUtil.close();
            }
        }

        public static MetaObject product(String item) {
            try {
                long id = Long.parseLong(item);
                DbUtil.open();
                String sql = "Select Items.Title, Items.Description, Categories.Category, (Select Image From Images Where Images.List_Id = Items.Image_Id Order By Index Limit 1) Image From Items Join Item_Groups On Item_Groups.Id = Items.Group_Id Join Categories On Categories.Id = Item_Groups.Category_Id Where Items.Id = ?";
                MapResponse response = Handler.findFirst(sql, id);
                String itemName = response.getString("title");
                String description = response.getString("description");
                String img = response.getString("image");
                String category = response.getString("category");

                return new MetaObject()
                        .setAllTitle(String.format("Buy %s at Zerroo Online Store",
                                itemName))
                        .setAllDescription(description)
                        .setAllUrl("https://www.zerroo.in/p/" + id + "/" + StringUtil.seoEncode(category))
                        .setAllImage(WorkDrive.toImg(img))
                        .setKeywords(KEYWORDS);
            } catch (Exception e) {
                return Generator.preset();
            } finally {
                DbUtil.close();
            }
        }
    }
}
