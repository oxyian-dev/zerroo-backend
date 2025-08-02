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

import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.DbTransaction;
import com.hionstudios.iam.IsAdmin;
import com.hionstudios.zerroo.flow.item.SpecificationTransaction;

@RestController
@RequestMapping("api/item-specifications")
public class SpecificationController {
    @GetMapping
    @IsAdmin
    public ResponseEntity<MapResponse> view(DataGridParams params) {
        return ((DbTransaction) () -> new SpecificationTransaction().view(params)).read();
    }

    @PostMapping
    @IsAdmin
    public ResponseEntity<MapResponse> add(
            @RequestParam long type,
            @RequestParam String value) {
        return ((DbTransaction) () -> new SpecificationTransaction().addSpecification(type, value)).write();
    }

    @GetMapping("types")
    @IsAdmin
    public ResponseEntity<MapResponse> types(DataGridParams params) {
        return ((DbTransaction) () -> new SpecificationTransaction().types(params)).read();
    }

    @PostMapping("types")
    @IsAdmin
    public ResponseEntity<MapResponse> types(@RequestParam String type) {
        return ((DbTransaction) () -> new SpecificationTransaction().addType(type)).read();
    }

    @GetMapping("list")
    @IsAdmin
    public ResponseEntity<MapResponse> viewList(DataGridParams params) {
        return ((DbTransaction) () -> new SpecificationTransaction().viewList(params)).read();
    }

    @PostMapping("list")
    @IsAdmin
    public ResponseEntity<MapResponse> addSpecificationList(
            @RequestParam String name,
            @RequestParam long[] specifications) {
        return ((DbTransaction) () -> new SpecificationTransaction().addList(name, specifications)).write();
    }

    @GetMapping("list/{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> getSpecificationList(
            @PathVariable long id) {
        return ((DbTransaction) () -> new SpecificationTransaction().getList(id)).read();
    }

    @PutMapping("list/{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> editSpecificationList(
            @PathVariable long id,
            @RequestParam String name,
            @RequestParam long[] specifications) {
        return ((DbTransaction) () -> new SpecificationTransaction().editSpecificationList(id, name, specifications))
                .write();
    }

    @PostMapping("groups")
    @IsAdmin
    public ResponseEntity<MapResponse> addSpecificationGroup(@RequestParam long list,
            @RequestParam long specification) {
        return ((DbTransaction) () -> null).write();
    }

    @DeleteMapping("groups")
    @IsAdmin
    public ResponseEntity<MapResponse> deleteSpecificationGroup(@RequestParam long list,
            @RequestParam long specification) {
        return ((DbTransaction) () -> null).write();
    }
}
