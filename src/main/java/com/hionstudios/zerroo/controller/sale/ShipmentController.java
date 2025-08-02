package com.hionstudios.zerroo.controller.sale;

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
import com.hionstudios.iam.IsAdmin;
import com.hionstudios.zerroo.flow.sale.ShipmentTransaction;

@RestController
@RequestMapping("api/shipments")
public class ShipmentController {
    @GetMapping
    @IsAdmin
    public ResponseEntity<MapResponse> shipments(DataGridParams params) {
        return ((DbTransaction) () -> new ShipmentTransaction().allShipments(params)).read();
    }

    @GetMapping("{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> get(@PathVariable long id) {
        return ((DbTransaction) () -> new ShipmentTransaction().get(id)).read();
    }

    @PutMapping("{id}/dispatch")
    @IsAdmin
    public ResponseEntity<MapResponse> dispatch(
            @PathVariable long id,
            @RequestParam long transporter,
            @RequestParam long courier,
            @RequestParam String awb,
            @RequestParam(required = false) Double weight,
            @RequestParam(required = false) Double length,
            @RequestParam(required = false) Double breadth,
            @RequestParam(required = false) Double height) {
        return ((DbTransaction) () -> new ShipmentTransaction().dispatch(
                id,
                transporter,
                courier,
                awb,
                weight,
                length,
                breadth,
                height)).write();
    }

    @GetMapping("status/{status}")
    @IsAdmin
    public ResponseEntity<MapResponse> pendingShipments(
            @PathVariable String status,
            DataGridParams params) {
        return ((DbTransaction) () -> new ShipmentTransaction().shipments(status, params)).read();
    }

    @GetMapping("count")
    @IsAdmin
    public ResponseEntity<MapResponse> count() {
        return ((DbTransaction) () -> new ShipmentTransaction().count()).read();
    }

    @PostMapping("generate")
    @IsAdmin
    public synchronized ResponseEntity<MapResponse> generate() {
        return ((DbTransaction) () -> new ShipmentTransaction().generate()).read();
    }
}
