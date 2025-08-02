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
import com.hionstudios.zerroo.flow.item.variation.ColorTransaction;
import com.hionstudios.zerroo.flow.item.variation.SizeTransaction;

@RestController
@RequestMapping("api/variants")
public class VariationController {
    @GetMapping("colors/{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> getColor(
            @PathVariable long id) {
        return ((DbTransaction) () -> new ColorTransaction().get(id)).read();
    }

    @GetMapping("colors")
    @IsAdmin
    public ResponseEntity<MapResponse> viewColors(DataGridParams params) {
        return ((DbTransaction) () -> new ColorTransaction().viewColors(params)).read();
    }

    @PostMapping("colors")
    @IsAdmin
    public ResponseEntity<MapResponse> addColor(
            @RequestParam String color,
            @RequestParam String hex) {
        return ((DbTransaction) () -> new ColorTransaction().addColour(color, hex)).write();
    }

    @PutMapping("colors/{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> editColor(
            @PathVariable long id,
            @RequestParam String color,
            @RequestParam String hex) {
        return ((DbTransaction) () -> new ColorTransaction().editColour(id, color, hex)).write();
    }

    @GetMapping("sizes")
    @IsAdmin
    public ResponseEntity<MapResponse> viewSizes(DataGridParams params) {
        return ((DbTransaction) () -> new SizeTransaction().view(params)).read();
    }

    @GetMapping("sizes/{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> getSize(
            @PathVariable long id) {
        return ((DbTransaction) () -> new SizeTransaction().get(id)).read();
    }

    @PostMapping("sizes")
    @IsAdmin
    public ResponseEntity<MapResponse> addSize(
            @RequestParam String size,
            @RequestParam int index) {
        return ((DbTransaction) () -> new SizeTransaction().addSize(size, index)).write();
    }

    @PutMapping("sizes/{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> editSize(
            @PathVariable long id,
            @RequestParam String size,
            @RequestParam int index) {
        return ((DbTransaction) () -> new SizeTransaction().editSize(id, size, index)).write();
    }
}
