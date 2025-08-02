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
import com.hionstudios.zerroo.flow.item.ComboTransaction;

@RestController
@RequestMapping("api/combos")
public class ComboController {
    @GetMapping
    @IsAdmin
    public ResponseEntity<MapResponse> combos(DataGridParams params) {
        return ((DbTransaction) () -> new ComboTransaction().combos(params)).read();
    }

    @GetMapping("{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> combo(@PathVariable long id) {
        return ((DbTransaction) () -> new ComboTransaction().combo(id)).read();
    }

    @PostMapping
    @IsAdmin
    public ResponseEntity<MapResponse> addCombo(
            @RequestParam String name,
            @RequestParam long category,
            @RequestParam String description,
            @RequestParam MultipartFile image) {
        return ((DbTransaction) () -> new ComboTransaction().combo(name, category, description, image)).write();
    }

    @PutMapping("{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> editCombo(
            @PathVariable long id,
            @RequestParam String name,
            @RequestParam long category,
            @RequestParam String description,
            @RequestParam MultipartFile image,
            @RequestParam boolean imageChanged) {
        return ((DbTransaction) () -> new ComboTransaction().editCombo(
                id,
                name,
                category,
                description,
                image,
                imageChanged)).write();
    }

    @GetMapping("groups")
    @IsAdmin
    public ResponseEntity<MapResponse> groups(DataGridParams params) {
        return ((DbTransaction) () -> new ComboTransaction().groups(params)).read();
    }

    @GetMapping("groups/{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> group(@PathVariable long id) {
        return ((DbTransaction) () -> new ComboTransaction().group(id)).read();
    }

    @PostMapping("groups")
    @IsAdmin
    public ResponseEntity<MapResponse> groups(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam long price,
            @RequestParam long[] items) {
        return ((DbTransaction) () -> new ComboTransaction().group(name, description, price, items)).write();
    }

    @PutMapping("groups/{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> groups(
            @PathVariable long id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam long price,
            @RequestParam long[] items) {
        return ((DbTransaction) () -> new ComboTransaction().group(id, name, description, price, items)).write();
    }

    @GetMapping("{id}/mapping")
    @IsAdmin
    public ResponseEntity<MapResponse> mapping(@PathVariable long id, DataGridParams params) {
        return ((DbTransaction) () -> new ComboTransaction().mapping(id, params)).read();
    }

    @PostMapping("{id}/mapping")
    @IsAdmin
    public ResponseEntity<MapResponse> mapping(
            @PathVariable long id,
            @RequestParam long group,
            @RequestParam int quantity) {
        return ((DbTransaction) () -> new ComboTransaction().mapping(id, group, quantity)).write();
    }
}
