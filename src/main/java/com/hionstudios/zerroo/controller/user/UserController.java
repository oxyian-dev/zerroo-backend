package com.hionstudios.zerroo.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hionstudios.MapResponse;
import com.hionstudios.db.DbTransaction;
import com.hionstudios.zerroo.flow.user.UserTransaction;

@RestController
@RequestMapping("api/users")
public class UserController {
    @GetMapping("profile")
    public ResponseEntity<MapResponse> profile() {
        return ((DbTransaction) () -> new UserTransaction().profile()).read();
    }

    @PutMapping("profile")
    public ResponseEntity<MapResponse> profile(
            @RequestParam String firstname,
            @RequestParam String lastname,
            @RequestParam String dob,
            @RequestParam String gender,
            @RequestParam String phone,
            @RequestParam String email,
            @RequestParam String address_1,
            @RequestParam String address_2,
            @RequestParam(required = false) String landmark,
            @RequestParam String postcode,
            @RequestParam String city,
            @RequestParam String state,
            @RequestParam String nominee_name,
            @RequestParam String nominee_relation) {
        return ((DbTransaction) () -> new UserTransaction().profile(
            firstname,
             lastname, 
             dob, 
             gender, 
             phone, 
             email,
             address_1,
             address_2,
             landmark,
             postcode,
             city,
             state,
             nominee_name,
             nominee_relation))
                .write();
    }

    @PutMapping("password")
    public ResponseEntity<MapResponse> password(
            @RequestParam String old,
            @RequestParam String password) {
        return ((DbTransaction) () -> new UserTransaction().password(old, password)).write();
    }

    @PostMapping("avatar")
    public ResponseEntity<MapResponse> avatar(@RequestParam MultipartFile avatar) {
        return ((DbTransaction) () -> new UserTransaction().avatar(avatar)).write();
    }
}
