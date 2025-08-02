package com.hionstudios;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.hionstudios.db.Handler;
import com.hionstudios.http.HttpUtil;
import com.hionstudios.zerroo.Constants;

public class CommonUtil {
    private static final Logger LOGGER = Logger.getLogger(CommonUtil.class.getName());
    public static ListResponse categories = null;

    public MapResponse categories() {
        if (categories == null) {
            categories = new ListResponse();
            String sql = "Select Id, Category, Image From Categories Where Parent is Null And Display";
            List<MapResponse> cat = Handler.findAll(sql);
            for (MapResponse c : cat) {
                MapResponse category = new MapResponse();
                category.put("parent", c);
                category.put("children", childCategories(c.getLong("id")));
                categories.add(category);
            }
        }
        return new MapResponse().put("categories", categories);
    }

    public List<MapResponse> childCategories(long id) {
        String sql = "Select Id, Category, Image, Display From Categories Where Parent = ?";
        List<MapResponse> children = new ArrayList<>();
        List<MapResponse> categories = Handler.findAll(sql, id);
        for (MapResponse category : categories) {
            if (category.getBoolean("display")) {
                children.add(category);
            }
            children.addAll(childCategories(category.getLong("id")));
        }
        return children;
    }

    public MapResponse featured() {
        String sql = "Select Items.Id, Items.Title, Categories.Category, (Select Image From Images Where List_Id = Items.Image_Id Order By Index Limit 1) Image, Items.Group_Id, Items.Color_Id From Items Join Item_Groups On Item_Groups.Id = Items.Group_Id Join Categories On Categories.Id = Item_Groups.Category_Id Where Featured_Status is True Limit 25";
        return new MapResponse().put("featured", Handler.findAll(sql));
    }

    public static double removeGst(double price, double gst) {
        return round((100 * price) / (100 + gst));
    }

    public static double addGst(double price, double gst) {
        return round(price + (price * gst / 100));
    }

    public static String nullIfEmpty(String string) {
        return "".equals(string) ? null : string;
    }

    public static boolean isTn(String postcode) throws IOException {
        return state(postcode).equals("Tamil Nadu");
    }

    public static boolean isKar(String postcode) throws IOException {
        return state(postcode).equals("Karnataka");
    }

    public static String state(String postcode) throws IOException {
        return postal(postcode).getList("postal").getMap(0).getList("PostOffice").getMap(0)
                .getString("State");
    }

    public static MapResponse postal(String postcode) throws IOException {
        return new MapResponse().put("postal", HttpUtil.toList("https://api.postalpincode.in/pincode/" + postcode));
    }

    public static MapResponse ifsc(String ifsc) throws IOException {
        return HttpUtil.toMap("https://ifsc.razorpay.com/" + ifsc);
    }

    public static double round(double value) {
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.parseDouble(df.format(value));
    }

    public static String getIp() {
        String ip = null;
        try {
            ip = getIp(getRequest());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return ip;
    }

    public static String getUserAgent() {
        HttpServletRequest request = getRequest();
        return request == null ? null : request.getHeader("User-Agent");
    }

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        return attributes == null ? null : attributes.getRequest();
    }

    public static String getIp(HttpServletRequest request) {
        String[] HEADERS_TO_TRY = {
                "X-Real-Ip",
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR" };
        for (String header : HEADERS_TO_TRY) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }

    public static boolean validateGender(String gender) {
        return Arrays.asList("Male", "Female", "Other").contains(gender);
    }

    public static boolean validateNomineerelation(String nominee_relation) {
        return Arrays.asList("Mother",
                "Father",
                "Brother",
                "Sister",
                "Wife",
                "Hushband",
                "Son",
                "Daughter").contains(nominee_relation);
    }

    public MapResponse topEarners() {
        String sql = "Select Distributors.Id, Users.Username, Users.Avatar, Users.Firstname, Users.Lastname, Distributors.Cutoff_Left_Pv, Distributors.Cutoff_Right_Pv, Distributors.Total_Income, Distributors.Total_Income + Floor((Least((Distributors.Cutoff_Left_Pv + Distributors.Carry_Left_Pv), (Distributors.Cutoff_Right_Pv + Distributors.Carry_Right_Pv)) / ?)) * ? Net_Income From Distributors Join Users On Users.Id = Distributors.Id Where Distributors.Id > 8 And Distributors.Self_Pv >= ? Order BY Net_Income Desc Limit 25";
        List<MapResponse> list = Handler.findAll(sql, Constants.MIN_PAIR_MATCH, Constants.PAIR_MATCH_INCOME,
                Constants.ACTIVATION_PV);
        MapResponse response = new MapResponse();
        response.put("hew", list);
        return response;
    }
}
