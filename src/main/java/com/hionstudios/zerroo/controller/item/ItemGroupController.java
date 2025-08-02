package com.hionstudios.zerroo.controller.item;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.DbTransaction;
import com.hionstudios.iam.IsAdmin;
import com.hionstudios.zerroo.flow.item.ItemGroupTransaction;

@RestController
@RequestMapping("api/item-groups")
public class ItemGroupController {
    @PostMapping
    @IsAdmin
    public ResponseEntity<MapResponse> addGroup(
            @RequestParam String name,
            @RequestParam long category,
            @RequestParam long brand,
            @RequestParam(required = false) Long specification,
            @RequestParam(required = false) MultipartFile size) {
        return ((DbTransaction) () -> new ItemGroupTransaction().addGroup(
                name, category, brand, specification, size)).write();
    }

    @PutMapping("{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> editGroup(
            @PathVariable long id,
            @RequestParam String name,
            @RequestParam long category,
            @RequestParam long brand,
            @RequestParam(required = false) Long specification,
            @RequestParam(required = false) MultipartFile size,
            @RequestParam boolean removed) {
        return ((DbTransaction) () -> new ItemGroupTransaction().editGroup(
                id, name, category, brand, specification, size, removed))
                .write();
    }

    @GetMapping("{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> view(@PathVariable long id) {
        return ((DbTransaction) () -> new ItemGroupTransaction().view(id)).read();
    }

    @GetMapping()
    @IsAdmin
    public ResponseEntity<MapResponse> view(DataGridParams params) {
        return ((DbTransaction) () -> new ItemGroupTransaction().view(params)).read();
    }
}
