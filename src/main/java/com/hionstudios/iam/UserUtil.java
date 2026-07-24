package com.hionstudios.iam;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
        if (principal instanceof HionUserDetails) {
            return (HionUserDetails) principal;
        }
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return loadUserDetails(username);
        }
        if (principal instanceof String) {
            return loadUserDetails((String) principal);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            if (username != null && !"anonymousUser".equalsIgnoreCase(username)) {
                return loadUserDetails(username);
            }
        }

        HionUserDetails requestUser = getUserDetailsFromRequest();
        if (requestUser != null) {
            return requestUser;
        }
        return new HionUserDetails();
    }

    private static HionUserDetails getUserDetailsFromRequest() {
        try {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (requestAttributes == null || requestAttributes.getRequest() == null) {
                return null;
            }
            javax.servlet.http.HttpServletRequest request = requestAttributes.getRequest();
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            } else {
                token = null;
                if (request.getCookies() != null) {
                    for (javax.servlet.http.Cookie cookie : request.getCookies()) {
                        if ("auth".equals(cookie.getName())) {
                            token = cookie.getValue();
                            break;
                        }
                    }
                }
            }
            if (token == null || token.trim().isEmpty()) {
                return null;
            }
            String username = new JwtTokenUtil().getUsernameFromToken(token);
            return loadUserDetails(username);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static HionUserDetails loadUserDetails(String username) {
        if (username == null || username.trim().isEmpty()) {
            return new HionUserDetails();
        }
        try {
            return new HionUserDetailsService().loadUserByUsername(username.trim());
        } catch (Exception ignored) {
            return new HionUserDetails();
        }
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
        HionUserDetails userDetails = getUserDetails();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("USERUTIL userid auth="
                + (authentication == null ? "null" : authentication.getClass().getName())
                + " principal="
                + (authentication == null || authentication.getPrincipal() == null
                        ? "null"
                        : authentication.getPrincipal().getClass().getName())
                + " name="
                + (authentication == null ? "null" : authentication.getName())
                + " resolved=" + userDetails.getUserid()
                + " type=" + userDetails.getType()
                + " username=" + userDetails.getUsername());
        return userDetails.getUserid();
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
        return UserType.DISTRIBUTOR.equals(getUserDetails().getType());
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
