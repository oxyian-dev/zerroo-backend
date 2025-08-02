package com.hionstudios.zerroo.controller.admin.users;

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
import com.hionstudios.zerroo.flow.AdminUserTransaction;

@RestController
@RequestMapping("api/admin/users")
public class AdminUserController {
    @GetMapping("org-users")
    @IsAdmin
    public ResponseEntity<MapResponse> orgUsers(DataGridParams params) {
        return ((DbTransaction) () -> new AdminUserTransaction().viewOrgUsers(params)).read();
    }

    @GetMapping("distributors")
    @IsAdmin
    public ResponseEntity<MapResponse> distributors(DataGridParams params) {
        return ((DbTransaction) () -> new AdminUserTransaction().viewDistributors(params)).read();
    }

    @GetMapping("distributors/{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> distributors(@PathVariable long id) {
        return ((DbTransaction) () -> new AdminUserTransaction().viewDistributor(id)).read();
    }

    @PutMapping("distributors/{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> distributors(
            @PathVariable long id,
            @RequestParam String firstname,
            @RequestParam String lastname,
            @RequestParam String phone,
            @RequestParam String email,
            @RequestParam String referer) {
        return ((DbTransaction) () -> new AdminUserTransaction().editDistributor(id, firstname, lastname, phone, email,
                referer)).write();
    }

    @DeleteMapping("distributors/{id}/avatar")
    @IsAdmin
    public ResponseEntity<MapResponse> avatar(@PathVariable long id) {
        return ((DbTransaction) () -> new AdminUserTransaction().removeAvatar(id)).write();
    }

    @DeleteMapping("distributors/{id}/kyc")
    @IsAdmin
    public ResponseEntity<MapResponse> kyc(@PathVariable long id) {
        return ((DbTransaction) () -> new AdminUserTransaction().removeKyc(id)).write();
    }

    @DeleteMapping("distributors/{id}/bank")
    @IsAdmin
    public ResponseEntity<MapResponse> bank(@PathVariable long id) {
        return ((DbTransaction) () -> new AdminUserTransaction().removeBank(id)).write();
    }

    @GetMapping("distributors/{id}/uplines")
    @IsAdmin
    public ResponseEntity<MapResponse> uplines(@PathVariable long id, DataGridParams params) {
        return ((DbTransaction) () -> new AdminUserTransaction().uplines(id, params)).write();
    }

    @PutMapping("distributors/{id}/status")
    @IsAdmin
    public ResponseEntity<MapResponse> status(@PathVariable long id, @RequestParam boolean status) {
        return ((DbTransaction) () -> new AdminUserTransaction().status(id, status)).write();
    }

    @PostMapping("distributors/{id}/pv")
    @IsAdmin
    public ResponseEntity<MapResponse> addPv(
            @PathVariable long id,
            @RequestParam double pv,
            @RequestParam boolean recursive) {
        return ((DbTransaction) () -> new AdminUserTransaction().addPv(id, pv, recursive)).write();
    }

    @PostMapping("distributors/{id}/wallet")
    @IsAdmin
    public ResponseEntity<MapResponse> wallet(
            @PathVariable long id,
            @RequestParam double amount,
            @RequestParam String type) {
        return ((DbTransaction) () -> new AdminUserTransaction().wallet(id, amount, type)).write();
    }

    @PutMapping("distributors/{id}/bank")
    @IsAdmin
    public ResponseEntity<MapResponse> bank(
            @PathVariable long id,
            @RequestParam String ifsc,
            @RequestParam String bank,
            @RequestParam String branch,
            @RequestParam String account_no) {
        return ((DbTransaction) () -> new AdminUserTransaction().bank(id, ifsc, bank, branch, account_no)).write();
    }

    @PutMapping("distributors/{id}/kyc")
    @IsAdmin
    public ResponseEntity<MapResponse> kyc(
            @PathVariable long id,
            @RequestParam String aadhaar,
            @RequestParam String pan,
            @RequestParam String pan_firstname,
            @RequestParam String pan_lastname) {
        return ((DbTransaction) () -> new AdminUserTransaction().kyc(id, aadhaar, pan, pan_firstname, pan_lastname))
                .write();
    }

    @PostMapping("distributors/transfer")
    @IsAdmin
    public ResponseEntity<MapResponse> transfer(
            @RequestParam String username,
            @RequestParam String destination,
            @RequestParam boolean placement,
            @RequestParam String referer) {
        return ((DbTransaction) () -> new AdminUserTransaction().transfer(username, destination, placement, referer))
                .write();
    }

    @GetMapping("org-users/{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> orgUser(@PathVariable long id) {
        return ((DbTransaction) () -> new AdminUserTransaction().viewOrgUser(id)).read();
    }

    @PutMapping("org-users/{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> edit(
            @PathVariable long id,
            @RequestParam String firstname,
            @RequestParam String lastname,
            @RequestParam String email,
            @RequestParam long[] roles) {
        return ((DbTransaction) () -> new AdminUserTransaction().editUser(
                id, firstname, lastname, email, roles)).write();
    }

    @PostMapping
    @IsAdmin
    public ResponseEntity<MapResponse> addUser(
            @RequestParam String firstname,
            @RequestParam String lastname,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam long[] roles) {
        return ((DbTransaction) () -> new AdminUserTransaction().addUser(
                firstname, lastname, email, password, roles)).write();
    }
}
