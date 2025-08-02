package com.hionstudios.zerroo.controller;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hionstudios.MapResponse;
import com.hionstudios.db.DbTransaction;
import com.hionstudios.iam.Authenticator;
import com.hionstudios.iam.JwtRequest;
import com.hionstudios.iam.UserUtil;

@RestController
public class AuthController {
    @Autowired
    Authenticator authenticator;

    @PostMapping("/authenticate")
    @PermitAll
    public ResponseEntity<?> createAuthenticationToken(
            @RequestBody JwtRequest authenticationRequest,
            HttpServletResponse response) {
        return authenticator.authenticate(authenticationRequest, response);
    }

    @GetMapping("api/auth")
    public ResponseEntity<MapResponse> auth() {
        return ((DbTransaction) UserUtil::auth).read();
    }

    @PostMapping("forgot-password")
    @PermitAll
    public ResponseEntity<MapResponse> forgotPassword(@RequestParam String username) {
        return ((DbTransaction) () -> UserUtil.forgotPassword(username)).read();
    }
}
