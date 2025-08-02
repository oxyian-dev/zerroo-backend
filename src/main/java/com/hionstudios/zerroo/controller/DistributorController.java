package com.hionstudios.zerroo.controller;

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
import com.hionstudios.iam.IsDistributor;
import com.hionstudios.zerroo.flow.DistributorTransaction;

@RestController
@RequestMapping("api/distributors")
public class DistributorController {
    @GetMapping("my-referrals")
    @IsDistributor
    public ResponseEntity<MapResponse> myReferrals(DataGridParams params) {
        return ((DbTransaction) () -> new DistributorTransaction().myReferrals(params)).read();
    }

    @GetMapping("genealogy")
    @IsDistributor
    public ResponseEntity<MapResponse> genealogy(@RequestParam(required = false) String username) {
        return ((DbTransaction) () -> new DistributorTransaction().genealogy(username)).read();
    }

    @PostMapping("refer")
    @IsDistributor
    public ResponseEntity<MapResponse> refer(
            @RequestParam long parent,
            @RequestParam int placement,
            @RequestParam String firstname,
            @RequestParam String lastname,
            @RequestParam String phone,
            @RequestParam String email,
            @RequestParam String referer) {
        return ((DbTransaction) () -> new DistributorTransaction().refer(
                parent, placement, firstname, lastname, phone, email, referer)).write();
    }

    @GetMapping("dashboard")
    @IsDistributor
    public ResponseEntity<MapResponse> dashboard() {
        return ((DbTransaction) () -> new DistributorTransaction().dashboard()).read();
    }

    @GetMapping("zid/{username}")
    public ResponseEntity<MapResponse> getName(@PathVariable String username, @RequestParam long upline) {
        return ((DbTransaction) () -> new DistributorTransaction().getName(upline, username)).read();
    }

    @GetMapping("declaration-status")
    public ResponseEntity<MapResponse> getDeclarationStatus() {
        return ((DbTransaction) () -> new DistributorTransaction().getDeclarationStatus()).read();
    }

    @PutMapping("declaration-status")
    @IsDistributor
    public ResponseEntity<MapResponse> declaration(@RequestParam boolean declaration_status) {
        return ((DbTransaction) () -> new DistributorTransaction().declaration(declaration_status)).write();
    }
}
