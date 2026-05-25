package com.hionstudios.iam;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.hionstudios.MapResponse;
import com.hionstudios.db.Handler;
import com.hionstudios.zerroo.mail.MailUtil;
import com.hionstudios.zerroo.model.UserType;

public class UserUtil {
    private static Object getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getPrincipal() : null;
    }

    public static HionUserDetails getUserDetails() {
        Object principal = getPrincipal();
        return (principal instanceof HionUserDetails) ? (HionUserDetails) principal : new HionUserDetails();
    }

    public static boolean isLoggedIn() {
        return getPrincipal() instanceof HionUserDetails;
    }

    public static MapResponse auth() {
        if (isLoggedIn()) {
            HionUserDetails user = getUserDetails();
            MapResponse response = MapResponse.success();
            response.put("id", user.getUserid());
            response.put("firstname", user.getFirstame());
            response.put("lastname", user.getLastame());
            response.put("phone", user.getPhone());
            response.put("email", user.getEmail());
            response.put("type", user.getType());
            response.put("roles", user.getRoles());
            response.put("avatar", user.getAvatar());
            response.put("username", user.getUsername());
            return response;
        } else {
            return new MapResponse();
        }
    }

    public static long getUserid() {
        return getUserDetails().getUserid();
    }

    public static String getUsername() {
        return getUserDetails().getUsername();
    }

    public static String getFirstname() {
        return getUserDetails().getFirstame();
    }

    public static String getEmail() {
        return getUserDetails().getEmail();
    }

    public static boolean isDistributor() {
        return getUserDetails().getType().equals(UserType.DISTRIBUTOR);
    }

    public static MapResponse forgotPassword(String username) {
        String sql = "Select Email, Password From Users Where Username = ?";
        MapResponse response = Handler.findFirst(sql, username);
        if (response == null) {
            return MapResponse.failure("Check ID");
        }
        String password = response.getString("password");
        String email = response.getString("email");
        MailUtil.resetPassword(email, password);
        return MapResponse.success();
    }

    public static Long getIdFromUsername(String username) {
        String sql = "Select Id From Users Where Username = ?";
        return Handler.getLong(sql, username);
    }
}
