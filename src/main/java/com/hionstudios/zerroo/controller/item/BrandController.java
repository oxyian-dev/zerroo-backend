package com.hionstudios.zerroo.controller.item;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.DbTransaction;
import com.hionstudios.zerroo.flow.item.BrandTransaction;

@RestController
@RequestMapping("api/brands")
public class BrandController {
    @GetMapping
    public ResponseEntity<MapResponse> get(DataGridParams params) {
        return ((DbTransaction) () -> new BrandTransaction().get(params)).read();
    }

    @GetMapping("{id}")
    public ResponseEntity<MapResponse> get(@PathVariable long id) {
        return ((DbTransaction) () -> new BrandTransaction().get(id)).read();
    }

    @PostMapping
    public ResponseEntity<MapResponse> get(@RequestParam String brand) {
        return ((DbTransaction) () -> new BrandTransaction().add(brand)).write();
    }

    @PutMapping("{id}")
    public ResponseEntity<MapResponse> edit(@PathVariable long id, @RequestParam String brand) {
        return ((DbTransaction) () -> new BrandTransaction().edit(id, brand)).write();
    }
}
