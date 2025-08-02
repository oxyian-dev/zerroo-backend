package com.hionstudios.zerroo.controller.income;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.DbTransaction;
import com.hionstudios.iam.IsAdmin;
import com.hionstudios.zerroo.flow.income.WalletStatements;

@RestController
@RequestMapping("api/wallet-statements")
public class WalletStatementController {
    @GetMapping("income")
    @IsAdmin
    public ResponseEntity<MapResponse> incomes(DataGridParams params) {
        return ((DbTransaction) () -> new WalletStatements().incomes(params)).read();
    }

    @GetMapping("purchase")
    @IsAdmin
    public ResponseEntity<MapResponse> purchase(DataGridParams params) {
        return ((DbTransaction) () -> new WalletStatements().purchase(params)).read();
    }

    @GetMapping("income/distributors/{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> incomes(@PathVariable long id, DataGridParams params) {
        return ((DbTransaction) () -> new WalletStatements().incomes(id, params)).read();
    }

    @GetMapping("purchase/distributors/{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> purchase(@PathVariable long id, DataGridParams params) {
        return ((DbTransaction) () -> new WalletStatements().purchase(id, params)).read();
    }
}
