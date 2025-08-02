package com.hionstudios.zerroo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.DbTransaction;
import com.hionstudios.iam.IsDistributor;
import com.hionstudios.zerroo.flow.TransactionTransaction;

@RestController
@RequestMapping("api/transactions")
public class TransactionController {
    @GetMapping("income")
    @IsDistributor
    public ResponseEntity<MapResponse> incomeTransactions(DataGridParams params) {
        return ((DbTransaction) () -> new TransactionTransaction().incomeTransaction(params)).read();
    }

    @GetMapping("purchase")
    @IsDistributor
    public ResponseEntity<MapResponse> purchaseTransactions(DataGridParams params) {
        return ((DbTransaction) () -> new TransactionTransaction().purchaseTransactions(params)).read();
    }

    @GetMapping("payout")
    @IsDistributor
    public ResponseEntity<MapResponse> payoutTransactions(DataGridParams params) {
        return ((DbTransaction) () -> new TransactionTransaction().payoutTransactions(params)).read();
    }
  
    @GetMapping("month_income")
    @IsDistributor
    public ResponseEntity<MapResponse> month_incomeTransactions() {
        return ((DbTransaction) () -> new TransactionTransaction().month_incomeTransactions()).read();
    }
      
}
