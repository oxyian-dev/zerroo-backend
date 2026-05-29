package com.hionstudios.iam;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hionstudios.time.TimeUtil;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private HionUserDetailsService jwtUserDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private Authenticator authenticator;

    /**
     * If the JWT is about to expire, refresh it with a new token
     * If the token is valid, manually authenticate the request
     *
     * @param request  HttpServletRequest from Client
     * @param response HttpServletResponse to Client
     * @param chain    FilterChain to process the request further
     * @throws ServletException while processing the request further
     * @throws IOException      while decoding the Auth Value
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain chain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        long expiry = TimeUtil.currentTime();

        // Prefer explicit Authorization header so each frontend context
        // (admin/distributor) can select the correct token even when auth cookie exists.
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwtToken = authHeader.substring(7);
            if (authenticateToken(request, response, chain, jwtToken, expiry)) {
                return;
            }
        }

        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("auth")) {
                    if (authenticateToken(request, response, chain, cookie.getValue(), expiry)) {
                        return;
                    }
                }
            }
        }
        chain.doFilter(request, response);
    }

    private boolean authenticateToken(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            String jwtToken,
            long expiry) throws IOException, ServletException {
        try {
            if (jwtTokenUtil.getExpirationDateFromToken(jwtToken).getTime() <= expiry) {
                return false;
            }
            String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            if (username == null) {
                return false;
            }

            HionUserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);
            if (!jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                return false;
            }
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            WebAuthenticationDetails authenticationDetails = new WebAuthenticationDetailsSource()
                    .buildDetails(request);
            authenticationToken.setDetails(authenticationDetails);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            System.out.println("JWT AUTH SET principal=" + authenticationToken.getPrincipal().getClass().getName()
                    + " name=" + authenticationToken.getName());
            if (jwtTokenUtil.checkThreshold(jwtToken)) {
                JwtResponse jwtResponse = authenticator.construct(userDetails);
                response.addHeader(HttpHeaders.SET_COOKIE, jwtResponse.toCookieHeader(request));
            }
            chain.doFilter(request, response);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
