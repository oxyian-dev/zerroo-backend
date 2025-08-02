package com.hionstudios.zerroo.controller.item;

import javax.validation.constraints.Size;

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
import com.hionstudios.zerroo.flow.item.ItemTransaction;

@RestController
@RequestMapping("api/items")
public class ItemController {
    @GetMapping
    @IsAdmin
    public ResponseEntity<MapResponse> view(DataGridParams params) {
        return ((DbTransaction) () -> new ItemTransaction().view(params)).read();
    }

    @PostMapping
    @IsAdmin
    public ResponseEntity<MapResponse> addItem(
            @RequestParam long group,
            @RequestParam String sku,
            @RequestParam @Size(min = 6, message = "HSN Should be minimum 6 digit") String hsn,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam long price,
            @RequestParam long image,
            @RequestParam(required = false) Long size,
            @RequestParam(required = false) Long color,
            @RequestParam(required = false) Double weight,
            @RequestParam(required = false) Double length,
            @RequestParam(required = false) Double breadth,
            @RequestParam(required = false) Double height) {
        return ((DbTransaction) () -> new ItemTransaction().addItem(
                group,
                sku,
                hsn,
                title,
                description,
                price,
                image,
                size,
                color,
                weight,
                length,
                breadth,
                height)).write();
    }

    @PutMapping("{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> editItem(
            @PathVariable long id,
            @RequestParam long group,
            @RequestParam String sku,
            @RequestParam @Size(min = 6, message = "HSN Should be minimum 6 digit") String hsn,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam long price,
            @RequestParam long image,
            @RequestParam(required = false) Long size,
            @RequestParam(required = false) Long color,
            @RequestParam(required = false) Double weight,
            @RequestParam(required = false) Double length,
            @RequestParam(required = false) Double breadth,
            @RequestParam(required = false) Double height,
            @RequestParam String reason) {
        return ((DbTransaction) () -> new ItemTransaction().edit(
                id,
                group,
                sku,
                hsn,
                title,
                description,
                price,
                image,
                size,
                color,
                weight,
                length,
                breadth,
                height,
                reason)).write();
    }

    @GetMapping("{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> get(
            @PathVariable long id) {
        return ((DbTransaction) () -> new ItemTransaction().get(id)).read();
    }

    @GetMapping("sku/{sku}")
    @IsAdmin
    public ResponseEntity<MapResponse> get(
            @PathVariable String sku) {
        return ((DbTransaction) () -> new ItemTransaction().get(sku)).read();
    }

    @PutMapping("{id}/status")
    @IsAdmin
    public ResponseEntity<MapResponse> status(
            @PathVariable long id,
            @RequestParam boolean status,
            @RequestParam String reason) {
        return ((DbTransaction) () -> new ItemTransaction().status(id, status, reason)).write();
    }

    @PutMapping("{id}/featured_status")
    @IsAdmin
    public ResponseEntity<MapResponse> featured_status(
            @PathVariable long id,
            @RequestParam boolean featured_status) {
        return ((DbTransaction) () -> new ItemTransaction().featured_status(id, featured_status)).write();
    }
}
