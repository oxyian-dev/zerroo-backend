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
import com.hionstudios.zerroo.flow.item.CategoryTransaction;

@RequestMapping("api/categories")
@RestController
public class CategoryController {
    @GetMapping
    @IsAdmin
    public ResponseEntity<MapResponse> view(DataGridParams params) {
        return ((DbTransaction) () -> new CategoryTransaction().view(params)).read();
    }

    @GetMapping("{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> get(@PathVariable long id) {
        return ((DbTransaction) () -> new CategoryTransaction().view(id)).read();
    }

    @PostMapping
    @IsAdmin
    public ResponseEntity<MapResponse> add(
            @RequestParam String category,
            @RequestParam(required = false) Long parent,
            @RequestParam(required = false) MultipartFile image) {
        return ((DbTransaction) () -> new CategoryTransaction().add(category, parent, image)).write();
    }

    @PutMapping("{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> edit(
            @PathVariable long id,
            @RequestParam String category,
            @RequestParam(required = false) Long parent,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam boolean removed) {
        return ((DbTransaction) () -> new CategoryTransaction().edit(id, category, parent, image, removed)).write();
    }

    @PutMapping("{id}/image")
    @IsAdmin
    public ResponseEntity<MapResponse> updateCategoryImage(
            @PathVariable long id,
            @RequestParam MultipartFile image) {
        return ((DbTransaction) () -> new CategoryTransaction().updateImage(id, image)).write();
    }

    @PutMapping("{id}/display")
    @IsAdmin
    public ResponseEntity<MapResponse> display(
            @PathVariable long id,
            @RequestParam boolean display) {
        return ((DbTransaction) () -> new CategoryTransaction().display(id, display)).write();
    }
}
