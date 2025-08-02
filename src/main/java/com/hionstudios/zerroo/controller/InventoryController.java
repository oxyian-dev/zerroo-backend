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
import com.hionstudios.zerroo.flow.InventoryTransaction;


@RestController
@RequestMapping("api/inventories")
public class InventoryController {
    @GetMapping
    @IsAdmin
    public ResponseEntity<MapResponse> view(DataGridParams params) {
        return ((DbTransaction) () -> new InventoryTransaction().view(params)).read();
    }

    @PostMapping
    @IsAdmin
    public ResponseEntity<MapResponse> add(
            @RequestParam String inventory,
            @RequestParam String contact,
            @RequestParam String phone,
            @RequestParam String address1,
            @RequestParam String address2,
            @RequestParam String postcode,
            @RequestParam String landmark,
            @RequestParam String city,
            @RequestParam String state,
            @RequestParam long branch) {
        return ((DbTransaction) () -> new InventoryTransaction().add(inventory, contact, phone, address1, address2,
                postcode, landmark, city, state, branch)).write();
    }
}
