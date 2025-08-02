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
import com.hionstudios.iam.IsAdmin;
import com.hionstudios.zerroo.flow.item.PriceListTransaction;

@RestController
@RequestMapping("api/price-lists")
public class PriceListController {
    @GetMapping("{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> get(@PathVariable long id) {
        return ((DbTransaction) () -> new PriceListTransaction().view(id)).read();
    }

    @GetMapping
    @IsAdmin
    public ResponseEntity<MapResponse> view(DataGridParams params) {
        return ((DbTransaction) () -> new PriceListTransaction().view(params)).read();
    }

    @PostMapping
    @IsAdmin
    public ResponseEntity<MapResponse> add(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam double mrp,
            @RequestParam double price,
            @RequestParam double cost,
            @RequestParam int gst,
            @RequestParam Double pv) {
        return ((DbTransaction) () -> new PriceListTransaction().add(
                name,
                description,
                mrp,
                price,
                cost,
                gst,
                pv)).write();
    }

    @PutMapping("{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> edit(
            @PathVariable long id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam double mrp,
            @RequestParam double price,
            @RequestParam double cost,
            @RequestParam int gst_percent,
            @RequestParam Double pv) {
        return ((DbTransaction) () -> new PriceListTransaction().edit(
                id,
                name,
                description,
                mrp,
                price,
                cost,
                gst_percent,
                pv)).write();
    }
}
