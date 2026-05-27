package com.hionstudios.zerroo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.DbTransaction;
import com.hionstudios.iam.IsAdmin;
import com.hionstudios.zerroo.flow.BranchTransaction;

@RestController
@RequestMapping("api/branches")
public class BranchController {
    @GetMapping
    @IsAdmin
    public ResponseEntity<MapResponse> view(DataGridParams params) {
        return ((DbTransaction) () -> new BranchTransaction().view(params)).read();
    }

    @PostMapping
    @IsAdmin
    public ResponseEntity<MapResponse> add(
            @RequestParam String branch,
            @RequestParam("source_of_supply") String sourceOfSupply,
            @RequestParam String phone,
            @RequestParam String email,
            @RequestParam("address_1") String address1,
            @RequestParam("address_2") String address2,
            @RequestParam String postcode,
            @RequestParam String landmark,
            @RequestParam String city,
            @RequestParam String state,
            @RequestParam(defaultValue = "India") String country,
            @RequestParam String gstin) {
        return ((DbTransaction) () -> new BranchTransaction().add(
                branch,
                sourceOfSupply,
                phone,
                email,
                address1,
                address2,
                postcode,
                landmark,
                city,
                state,
                country,
                gstin)).write();
    }
}
