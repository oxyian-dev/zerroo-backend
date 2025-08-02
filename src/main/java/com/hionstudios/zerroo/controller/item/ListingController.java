package com.hionstudios.zerroo.controller.item;

import java.util.List;

import javax.annotation.security.PermitAll;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hionstudios.MapResponse;
import com.hionstudios.db.DbTransaction;
import com.hionstudios.zerroo.flow.item.ListingTransaction;

@RequestMapping("api/listing")
@RestController
public class ListingController {
    @GetMapping("items/category/{category}")
    @PermitAll
    public ResponseEntity<MapResponse> listing(
            @PathVariable long category,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) List<String> f_category,
            @RequestParam(required = false) List<String> f_size,
            @RequestParam(required = false) List<String> f_color,
            @RequestParam(required = false) List<String> f_brand,
            @RequestParam(required = false) List<Double> f_price,
            @RequestParam(required = false) List<Integer> f_discount) {
        return ((DbTransaction) () -> new ListingTransaction().listing(
                category, sort, f_category, f_size, f_color, f_brand, f_price, f_discount)).read();
    }

    @PermitAll
    @GetMapping("filter")
    public ResponseEntity<MapResponse> filters(
            @RequestParam long category) {
        return ((DbTransaction) () -> new ListingTransaction().filters(category)).read();
    }

    @PermitAll
    @GetMapping("items/{id}")
    public ResponseEntity<MapResponse> item(@PathVariable long id) {
        return ((DbTransaction) () -> new ListingTransaction().item(id)).read();
    }

    @PermitAll
    @GetMapping("categories/{id}/children")
    public ResponseEntity<MapResponse> children(@PathVariable long id) {
        return ((DbTransaction) () -> new ListingTransaction().children(id)).read();
    }

    @PermitAll
    @GetMapping("combos")
    public ResponseEntity<MapResponse> combos(@RequestParam long category) {
        return ((DbTransaction) () -> new ListingTransaction().combos(category)).read();
    }

    @PermitAll
    @GetMapping("combos/{id}")
    public ResponseEntity<MapResponse> combo(@PathVariable long id) {
        return ((DbTransaction) () -> new ListingTransaction().combo(id)).read();
    }
}
