package com.hionstudios.zerroo.controller.item;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.hionstudios.zerroo.flow.item.ImageListTransaction;


@RestController
@RequestMapping("api/image-lists")
public class ImageListController {
    @GetMapping
    @IsAdmin
    public ResponseEntity<MapResponse> get(DataGridParams params) {
        return ((DbTransaction) () -> new ImageListTransaction().view(params)).read();
    }

    @GetMapping("{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> get(@PathVariable long id) {
        return ((DbTransaction) () -> new ImageListTransaction().view(id)).read();
    }

    @PutMapping("{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> update(
            @PathVariable long id,
            @RequestParam String name) {
        return ((DbTransaction) () -> new ImageListTransaction().update(id, name)).write();
    }

    @PostMapping
    @IsAdmin
    public ResponseEntity<MapResponse> add(
            @RequestParam String name) {
        return ((DbTransaction) () -> new ImageListTransaction().add(name)).write();
    }

    @PostMapping("{id}/images")
    @IsAdmin
    public ResponseEntity<MapResponse> images(
            @PathVariable long id,
            @RequestParam MultipartFile[] images) {
        return ((DbTransaction) () -> new ImageListTransaction().addImages(id, images)).write();
    }

    @PutMapping("{id}/images/reorder")
    @IsAdmin
    public ResponseEntity<MapResponse> reorder(
            @PathVariable long id,
            @RequestParam String[] images) {
        return ((DbTransaction) () -> new ImageListTransaction().reorder(id, images)).write();
    }

    @DeleteMapping("{id}/images")
    @IsAdmin
    public ResponseEntity<MapResponse> delete(
            @PathVariable long id,
            @RequestParam String image) {
        return ((DbTransaction) () -> new ImageListTransaction().removeImage(id, image)).write();
    }

}
