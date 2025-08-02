package com.hionstudios.zerroo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.DbTransaction;
import com.hionstudios.iam.IsAdmin;
import com.hionstudios.iam.IsDistributor;
import com.hionstudios.zerroo.flow.VerificationTransaction;

@RestController
@RequestMapping("api/verification")
public class VerificationController {
    @PostMapping("phone")
    @IsDistributor
    public ResponseEntity<MapResponse> verifyPhone() {
        return ((DbTransaction) () -> new VerificationTransaction().verifyPhone()).write();
    }

    @PostMapping("phone/{id}")
    @IsDistributor
    public ResponseEntity<MapResponse> verifyPhone(@PathVariable long id, @RequestParam String username) {
        return ((DbTransaction) () -> new VerificationTransaction().verifyPhone(id, username)).write();
    }

    @PostMapping("email")
    @IsDistributor
    public ResponseEntity<MapResponse> verifyEmail() {
        return ((DbTransaction) () -> new VerificationTransaction().verifyEmail()).write();
    }

    @PostMapping("email/{id}")
    @IsDistributor
    public ResponseEntity<MapResponse> verifyEmail(@PathVariable long id, @RequestParam String username) {
        return ((DbTransaction) () -> new VerificationTransaction().verifyEmail(id, username)).write();
    }

    // User
    @PostMapping("kyc")
    @IsDistributor
    public ResponseEntity<MapResponse> kyc(
            @RequestParam String aadhaar,
            @RequestParam MultipartFile aadhaar_front,
            @RequestParam MultipartFile aadhaar_back,
            @RequestParam String pan,
            @RequestParam String pan_firstname,
            @RequestParam String pan_lastname,
            @RequestParam MultipartFile pan_image) {
        return ((DbTransaction) () -> new VerificationTransaction().kyc(
                aadhaar, aadhaar_front, aadhaar_back, pan, pan_firstname, pan_lastname, pan_image)).write();
    }

    // Admin
    @PutMapping("kyc/{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> kyc(
            @PathVariable long id,
            @RequestParam boolean status,
            @RequestParam(required = false) String reason) {
        return ((DbTransaction) () -> new VerificationTransaction().kyc(id, status, reason)).write();
    }

    // Admin
    @GetMapping("kyc/pending")
    @IsAdmin
    public ResponseEntity<MapResponse> pendingKyc(DataGridParams params) {
        return ((DbTransaction) () -> new VerificationTransaction().pendingKyc(params)).read();
    }

    @GetMapping("kyc/verified")
    @IsAdmin
    public ResponseEntity<MapResponse> verifiedKyc(DataGridParams params) {
        return ((DbTransaction) () -> new VerificationTransaction().verifiedKyc(params)).read();
    }

    @GetMapping("kyc/rejected")
    @IsAdmin
    public ResponseEntity<MapResponse> rejectedKyc(DataGridParams params) {
        return ((DbTransaction) () -> new VerificationTransaction().rejectedKyc(params)).read();
    }

    // Admin
    @GetMapping("kyc/{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> kyc(@PathVariable long id) {
        return ((DbTransaction) () -> new VerificationTransaction().kycDetails(id)).read();
    }

    // User
    @PostMapping("bank")
    @IsDistributor
    public ResponseEntity<MapResponse> bank(
            @RequestParam String acc,
            @RequestParam String ifsc,
            @RequestParam String bank,
            @RequestParam String branch,
            @RequestParam MultipartFile image) {
        return ((DbTransaction) () -> new VerificationTransaction().bank(acc, ifsc, bank, branch, image)).write();
    }

    @PutMapping("bank/{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> bank(
            @PathVariable long id,
            @RequestParam boolean status,
            @RequestParam(required = false) String reason) {
        return ((DbTransaction) () -> new VerificationTransaction().bank(id, status, reason)).write();
    }

    @GetMapping("bank/pending")
    @IsAdmin
    public ResponseEntity<MapResponse> pendingBank(DataGridParams params) {
        return ((DbTransaction) () -> new VerificationTransaction().pendingBank(params)).read();
    }

    @GetMapping("bank/verified")
    @IsAdmin
    public ResponseEntity<MapResponse> verifiedBank(DataGridParams params) {
        return ((DbTransaction) () -> new VerificationTransaction().verifiedBank(params)).read();
    }

    @GetMapping("bank/rejected")
    @IsAdmin
    public ResponseEntity<MapResponse> rejectedBank(DataGridParams params) {
        return ((DbTransaction) () -> new VerificationTransaction().rejectedBank(params)).read();
    }

    @GetMapping("bank/{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> bank(@PathVariable long id) {
        return ((DbTransaction) () -> new VerificationTransaction().bankDetails(id)).read();
    }

    @PostMapping("wallet")
    @IsDistributor
    public ResponseEntity<MapResponse> walletRequest(
            @RequestParam String method,
            @RequestParam double amount,
            @RequestParam long date,
            @RequestParam String depositor,
            @RequestParam String transaction,
            @RequestParam(required = false) MultipartFile proof) {
        return ((DbTransaction) () -> new VerificationTransaction().walletRequest(
                method,
                amount,
                date,
                depositor,
                transaction,
                proof)).write();
    }

    @GetMapping("wallet/pending")
    @IsAdmin
    public ResponseEntity<MapResponse> pendingWallet(DataGridParams params) {
        return ((DbTransaction) () -> new VerificationTransaction().pendingWallet(params)).read();
    }

    @GetMapping("wallet/rejected")
    @IsAdmin
    public ResponseEntity<MapResponse> rejectedWallet(DataGridParams params) {
        return ((DbTransaction) () -> new VerificationTransaction().rejectedWallet(params)).read();
    }

    @GetMapping("wallet/approved")
    @IsAdmin
    public ResponseEntity<MapResponse> approvedWallet(DataGridParams params) {
        return ((DbTransaction) () -> new VerificationTransaction().approvedWallet(params)).read();
    }

    @GetMapping("wallet/{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> wallet(@PathVariable long id) {
        return ((DbTransaction) () -> new VerificationTransaction().wallet(id)).read();
    }

    @PutMapping("wallet/{id}")
    @IsAdmin
    public synchronized ResponseEntity<MapResponse> walletStatus(
            @PathVariable long id,
            @RequestParam boolean status,
            @RequestParam(required = false) String reason) {
        return ((DbTransaction) () -> new VerificationTransaction().walletStatus(id, status, reason)).write();
    }

    @GetMapping("wallet-requests")
    @IsDistributor
    public ResponseEntity<MapResponse> walletRequests(DataGridParams params) {
        return ((DbTransaction) () -> new VerificationTransaction().walletRequests(params)).read();
    }
}
