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

        StringBuilder cookie = new StringBuilder("auth=")
                .append(getJwt())
                .append(";Path=/")
                .append(";HttpOnly")
                .append(";Max-Age=")
                .append(maxAge);

        if (secure) {
            // Cross-site frontend/backend auth needs SameSite=None with Secure.
            cookie.append(";SameSite=None;Secure");
        } else {
            // Local HTTP development fallback.
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
}
