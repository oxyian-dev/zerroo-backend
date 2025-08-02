package com.hionstudios.zerroo.controller;

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
import com.hionstudios.db.DbTransaction;
import com.hionstudios.iam.IsDistributor;
import com.hionstudios.zerroo.flow.AddressTransaction;

@RestController
@RequestMapping("api/addresses")
public class AddressController {
    @GetMapping
    @IsDistributor
    public ResponseEntity<MapResponse> view() {
        return ((DbTransaction) () -> new AddressTransaction().view()).read();
    }

    @GetMapping("{id}")
    @IsDistributor
    public ResponseEntity<MapResponse> view(@PathVariable long id) {
        return ((DbTransaction) () -> new AddressTransaction().view(id)).read();
    }

    @PostMapping
    @IsDistributor
    public ResponseEntity<MapResponse> add(
            @RequestParam String saved_name,
            @RequestParam String firstname,
            @RequestParam String lastname,
            @RequestParam String phone,
            @RequestParam(required = false, name = "alt_phone") String altPhone,
            @RequestParam String email,
            @RequestParam String address_1,
            @RequestParam String address_2,
            @RequestParam String postcode,
            @RequestParam(required = false) String landmark,
            @RequestParam String city,
            @RequestParam String state,
            @RequestParam("is_default") boolean isDefault) {
        return ((DbTransaction) () -> new AddressTransaction().addAddress(
                saved_name,
                firstname,
                lastname,
                phone,
                altPhone,
                email,
                address_1,
                address_2,
                postcode,
                landmark,
                city,
                state,
                isDefault)).write();
    }

    @PutMapping("{id}")
    @IsDistributor
    public ResponseEntity<MapResponse> edit(
            @PathVariable long id,
            @RequestParam String saved_name,
            @RequestParam String firstname,
            @RequestParam String lastname,
            @RequestParam String phone,
            @RequestParam(required = false, name = "alt_phone") String altPhone,
            @RequestParam String email,
            @RequestParam String address_1,
            @RequestParam String address_2,
            @RequestParam String postcode,
            @RequestParam(required = false) String landmark,
            @RequestParam String city,
            @RequestParam String state,
            @RequestParam boolean is_default) {
        return ((DbTransaction) () -> new AddressTransaction().editAddress(
                id,
                saved_name,
                firstname,
                lastname,
                phone,
                altPhone,
                email,
                address_1,
                address_2,
                postcode,
                landmark, city,
                state,
                is_default)).write();
    }

    @PutMapping("{id}/default")
    @IsDistributor
    public ResponseEntity<MapResponse> makeDefault(@PathVariable long id) {
        return ((DbTransaction) () -> new AddressTransaction().makeDefault(id)).write();
    }

    @DeleteMapping("{id}")
    @IsDistributor
    public ResponseEntity<MapResponse> delete(@PathVariable long id) {
        return ((DbTransaction) () -> new AddressTransaction().delete(id)).write();
    }
}
