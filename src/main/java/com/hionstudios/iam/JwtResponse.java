package com.hionstudios.iam;

import java.io.Serializable;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.hionstudios.time.TimeUtil;

public class JwtResponse implements Serializable {
    private static final long serialVersionUID = -8091879091924046844L;
    private final String jwt;
    private final Date expiry;

    public JwtResponse(String jwt, Date expiry) {
        this.jwt = jwt;
        this.expiry = expiry;
    }

    public String getJwt() {
        return jwt;
    }

    public Date getExpiry() {
        return expiry;
    }

    public String toCookieHeader(HttpServletRequest request) {
        long expiry = getExpiry().getTime();
        long maxAge = (expiry - TimeUtil.currentTime()) / 1000;
        boolean secure = isSecureRequest(request);
        String domain = getCookieDomain(request);

        StringBuilder cookie = new StringBuilder("auth=")
                .append(getJwt())
                .append(";Path=/")
                .append(";HttpOnly")
                .append(";Max-Age=")
                .append(maxAge);

        if (domain != null) {
            cookie.append(";Domain=").append(domain);
        }

        if (secure) {
            // Cross-site frontend/backend auth needs SameSite=None with Secure.
            cookie.append(";SameSite=None;Secure");
        } else {
            // Local HTTP development fallback.
            cookie.append(";SameSite=Lax");
        }
        return cookie.toString();
    }

    public static String toHostCookieDeletionHeader(HttpServletRequest request) {
        boolean secure = isSecureRequest(request);
        StringBuilder cookie = new StringBuilder("auth=;Path=/;HttpOnly;Max-Age=0");
        if (secure) {
            cookie.append(";SameSite=None;Secure");
        } else {
            cookie.append(";SameSite=Lax");
        }
        return cookie.toString();
    }

    private static boolean isSecureRequest(HttpServletRequest request) {
        if (request == null) {
            return true;
        }
        if (request.isSecure()) {
            return true;
        }
        String forwardedProto = request.getHeader("X-Forwarded-Proto");
        return forwardedProto != null && "https".equalsIgnoreCase(forwardedProto);
    }

    private static String getCookieDomain(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String host = request.getServerName();
        if (host == null) {
            return null;
        }
        host = host.toLowerCase();
        if (host.equals("victoryworld.in") || host.equals("www.victoryworld.in") || host.endsWith(".victoryworld.in")) {
            return ".victoryworld.in";
        }
        return null;
    }
}
