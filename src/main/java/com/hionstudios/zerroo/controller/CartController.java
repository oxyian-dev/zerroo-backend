package com.hionstudios.zerroo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hionstudios.MapResponse;
import com.hionstudios.db.DbTransaction;
import com.hionstudios.zerroo.flow.CartTransaction;

@RestController
@RequestMapping("api/carts")
public class CartController {
    @PostMapping
    public ResponseEntity<MapResponse> add(
            @RequestParam long item) {
        return ((DbTransaction) () -> new CartTransaction().add(item)).write();
    }

    @PostMapping("combo")
    public ResponseEntity<MapResponse> combo(
            @RequestParam long[] items,
            @RequestParam long[] combos,
            @RequestParam long[] groups) {
        return ((DbTransaction) () -> new CartTransaction().addCombo(items, combos, groups)).write();
    }

    @PutMapping("sync")
    public ResponseEntity<MapResponse> sync(
            @RequestParam long[] items,
            @RequestParam int[] quantities,
            @RequestParam long[] times) {
        return ((DbTransaction) () -> new CartTransaction().sync(items, quantities, times)).write();
    }

    @GetMapping
    public ResponseEntity<MapResponse> get() {
        return ((DbTransaction) () -> new CartTransaction().get()).read();
    }

    @GetMapping("count")
    public ResponseEntity<MapResponse> count() {
        return ((DbTransaction) () -> new CartTransaction().count()).read();
    }

    @PutMapping("quantity")
    public ResponseEntity<MapResponse> count(
            @RequestParam long item,
            @RequestParam int quantity) {
        return ((DbTransaction) () -> new CartTransaction().setCount(item, quantity)).read();
    }

    @DeleteMapping
    public ResponseEntity<MapResponse> delete(
            @RequestParam long item,
            @RequestParam(required = false) String uid) {
        return ((DbTransaction) () -> new CartTransaction().delete(item, uid)).read();
    }
}
