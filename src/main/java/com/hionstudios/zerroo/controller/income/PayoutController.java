package com.hionstudios.zerroo.controller.income;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.DbTransaction;
import com.hionstudios.iam.IsAdmin;
import com.hionstudios.zerroo.flow.income.PayoutTransaction;

@RestController
@RequestMapping("api/payouts")
public class PayoutController {
    @GetMapping
    @IsAdmin
    public ResponseEntity<MapResponse> payouts(DataGridParams params) {
        return ((DbTransaction) () -> new PayoutTransaction().payouts(params)).read();
    }

    @GetMapping("{id}/entries/axis")
    @IsAdmin
    public ResponseEntity<MapResponse> axisBankEntries(@PathVariable long id, DataGridParams params) {
        return ((DbTransaction) () -> new PayoutTransaction().axisBankEntries(id, params)).read();
    }

    @GetMapping("{id}/entries/non-axis")
    @IsAdmin
    public ResponseEntity<MapResponse> nonAxisBankEntries(@PathVariable long id, DataGridParams params) {
        return ((DbTransaction) () -> new PayoutTransaction().nonAxisBankEntries(id, params)).read();
    }

    @GetMapping("distributors/{id}/entries")
    @IsAdmin
    public ResponseEntity<MapResponse> distributors(@PathVariable long id, DataGridParams params) {
        return ((DbTransaction) () -> new PayoutTransaction().distributors(id, params)).read();
    }

    @PostMapping("initiate")
    @IsAdmin
    public ResponseEntity<MapResponse> initiate() {
        return ((DbTransaction) () -> new PayoutTransaction().initiate()).write();
    }
}
