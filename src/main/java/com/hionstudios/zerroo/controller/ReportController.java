package com.hionstudios.zerroo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hionstudios.MapResponse;
import com.hionstudios.db.DbTransaction;
import com.hionstudios.db.Handler;
import com.hionstudios.iam.IsAdmin;

@RestController
@RequestMapping("api/reports")
public class ReportController {
    @PostMapping("query")
    @IsAdmin
    public ResponseEntity<MapResponse> query(
            @RequestParam String query) {
        return ((DbTransaction) () -> Handler.toDataGrid(query)).read();
    }
}
