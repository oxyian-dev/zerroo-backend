package com.hionstudios.zerroo.controller.admin;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hionstudios.MapResponse;
import com.hionstudios.db.DbConnection;
import com.hionstudios.db.DbTransaction;
import com.hionstudios.iam.IsAdmin;
import com.hionstudios.zerroo.flow.AdminTransaction;

@RestController
@RequestMapping("api/admin")
public class AdminController {
    @GetMapping("dashboard")
    @IsAdmin
    public ResponseEntity<MapResponse> dashboard() {
        return ((DbTransaction) () -> new AdminTransaction().dashboard()).read();
    }

    @PostMapping("tds")
    @IsAdmin
    public void tds(@RequestParam long from,
            @RequestParam long to,
            HttpServletResponse response) {
        ((DbConnection) () -> new AdminTransaction().tds(from, to, response)).read();
    }
}
