package com.hionstudios.zerroo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.DbTransaction;
import com.hionstudios.db.DbUtil;
import com.hionstudios.iam.IsAuthenticatedUser;
import com.hionstudios.iam.IsDistributor;
import com.hionstudios.iam.JwtTokenUtil;
import com.hionstudios.iam.UserUtil;
import com.hionstudios.zerroo.flow.DistributorTransaction;

@RestController
@RequestMapping("api/distributors")
public class DistributorController {
    @GetMapping("my-referrals")
    @IsDistributor
    public ResponseEntity<MapResponse> myReferrals(DataGridParams params) {
        return ((DbTransaction) () -> new DistributorTransaction().myReferrals(params)).read();
    }

    @GetMapping("genealogy")
    @IsDistributor
    public ResponseEntity<String> genealogy(HttpServletRequest request, @RequestParam(required = false) String username) {
        try {
            String currentUsername = resolveUsername(request);
            if (currentUsername == null || currentUsername.trim().isEmpty()) {
                return ResponseEntity.ok(toJson(MapResponse.failure("Unable to load genealogy")));
            }
            DbUtil.open();
            try {
                MapResponse payload = new DistributorTransaction().genealogy(currentUsername, username);
                return ResponseEntity.ok(toJson(payload == null ? MapResponse.failure("Unable to load genealogy") : payload));
            } finally {
                DbUtil.close();
            }
        } catch (Exception exception) {
            return ResponseEntity.ok(toJson(MapResponse.failure(exception.getMessage() == null
                    ? "Unable to load genealogy"
                    : exception.getMessage())));
        }
    }

    @PostMapping("refer")
    @IsDistributor
    public ResponseEntity<MapResponse> refer(
            @RequestParam long parent,
            @RequestParam int placement,
            @RequestParam String firstname,
            @RequestParam String lastname,
            @RequestParam String phone,
            @RequestParam String email,
            @RequestParam String referer) {
        return ((DbTransaction) () -> new DistributorTransaction().refer(
                parent, placement, firstname, lastname, phone, email, referer)).write();
    }

    @GetMapping("dashboard")
    @IsAuthenticatedUser
    public ResponseEntity<String> dashboard(HttpServletRequest request) {
        try {
            String currentUsername = resolveUsername(request);
            if (currentUsername == null || currentUsername.trim().isEmpty()) {
                return ResponseEntity.ok(toJson(MapResponse.failure("Unable to load dashboard")));
            }
            DbUtil.open();
            try {
                long userId = UserUtil.getUserid();
                if (userId <= 0) {
                    return ResponseEntity.ok(toJson(MapResponse.failure("Unable to load dashboard")));
                }
                MapResponse payload = new DistributorTransaction().dashboard(userId);
                return ResponseEntity.ok(toJson(payload == null ? MapResponse.failure("Unable to load dashboard") : payload));
            } finally {
                DbUtil.close();
            }
        } catch (Exception exception) {
            return ResponseEntity.ok(toJson(MapResponse.failure(exception.getMessage() == null
                    ? "Unable to load dashboard"
                    : exception.getMessage())));
        }
    }

    @GetMapping("zid/{username}")
    public ResponseEntity<MapResponse> getName(@PathVariable String username, @RequestParam long upline) {
        return ((DbTransaction) () -> new DistributorTransaction().getName(upline, username)).read();
    }

    @GetMapping("declaration-status")
    public ResponseEntity<MapResponse> getDeclarationStatus() {
        return ((DbTransaction) () -> new DistributorTransaction().getDeclarationStatus()).read();
    }

    @PutMapping("declaration-status")
    @IsAuthenticatedUser
    public ResponseEntity<MapResponse> declaration(@RequestParam boolean declaration_status) {
        return ((DbTransaction) () -> new DistributorTransaction().declaration(declaration_status)).write();
    }

    private static String resolveUsername(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        System.out.println("DISTRIBUTOR resolve auth=" + request.getHeader("Authorization"));
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            token = null;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
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
        try {
            return new JwtTokenUtil().getUsernameFromToken(token);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String toJson(MapResponse payload) {
        try {
            return new ObjectMapper().writeValueAsString(payload);
        } catch (Exception ignored) {
            return "{}";
        }
    }
}
