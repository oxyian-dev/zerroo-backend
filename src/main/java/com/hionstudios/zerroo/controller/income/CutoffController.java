package com.hionstudios.zerroo.controller.income;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.DbTransaction;
import com.hionstudios.iam.IsAdmin;
import com.hionstudios.zerroo.flow.cutoff.CutoffTransaction;

@RestController
@RequestMapping("api/cutoffs")
public class CutoffController {
    @GetMapping
    @IsAdmin
    public ResponseEntity<MapResponse> cutoffs(DataGridParams params) {
        return ((DbTransaction) () -> new CutoffTransaction().cutoffs(params)).read();
    }

    @GetMapping("{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> cutoff(@PathVariable long id, DataGridParams params) {
        return ((DbTransaction) () -> new CutoffTransaction().cutoff(id, params)).read();
    }

    @PostMapping("initiate")
    @IsAdmin
    public synchronized ResponseEntity<MapResponse> initiate() {
        return ((DbTransaction) () -> new CutoffTransaction().initiate()).write();
    }

    @GetMapping("distributors/{id}")
    @IsAdmin
    public synchronized ResponseEntity<MapResponse> entries(@PathVariable long id, DataGridParams params) {
        return ((DbTransaction) () -> new CutoffTransaction().entries(id, params)).write();
    }
}
