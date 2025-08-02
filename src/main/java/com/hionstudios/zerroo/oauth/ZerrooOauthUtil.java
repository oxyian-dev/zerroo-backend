package com.hionstudios.zerroo.oauth;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.hionstudios.MapResponse;
import com.hionstudios.zerroo.model.Oauth;
import com.hionstudios.oauth.OAuthTokens;

/**
 * @author Karthikeyan K
 */
public class ZerrooOauthUtil {
    private static final String ZOHO = "zoho";
    private static final Logger LOGGER = Logger.getLogger(ZerrooOauthUtil.class.getName());

    public static String getURL(String provider) throws Exception {
        if (ZOHO.equals(provider)) {
            return new ZohoApp().getUrl();
        }
        return null;
    }

    public static MapResponse getTokens(String provider, HttpServletRequest request) {
        try {
            if (ZOHO.equals(provider)) {
                return new MapResponse(new ZohoApp().getOAuthTokens(request).toJSONObject());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return MapResponse.failure();
    }

    public static String getOAuthTokens(String provider) throws Exception {
        OAuthTokens tokens = null;
        if (ZOHO.equals(provider)) {
            tokens = new ZohoApp().getOAuthTokens();
        }
        return tokens != null ? tokens.toJSONObject().toJSONString() : null;
    }

    public static Oauth getOAuth(String provider) {
        return Oauth.findFirst("provider = ?", provider);
    }
}
