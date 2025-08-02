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
import com.hionstudios.zerroo.flow.TransporterTransaction;

@RestController
@RequestMapping("api/transporters")
public class TransporterController {
    @GetMapping
    @IsAdmin
    public ResponseEntity<MapResponse> view(DataGridParams params) {
        return ((DbTransaction) () -> new TransporterTransaction().view(params)).read();
    }

    @GetMapping("{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> view(@PathVariable long id) {
        return ((DbTransaction) () -> new TransporterTransaction().view(id)).read();
    }

    @PostMapping
    @IsAdmin
    public ResponseEntity<MapResponse> add(
            @RequestParam String transporter,
            @RequestParam long[] couriers,
            @RequestParam long inventory_id) {
        return ((DbTransaction) () -> new TransporterTransaction().add(transporter, couriers, inventory_id)).write();
    }

    @PutMapping("{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> edit(
            @PathVariable long id,
            @RequestParam String transporter,
            @RequestParam long[] couriers,
            @RequestParam long inventory_id) {
        return ((DbTransaction) () -> new TransporterTransaction().edit(
                id,
                transporter,
                couriers,
                inventory_id)).write();
    }

    @GetMapping("couriers")
    @IsAdmin
    public ResponseEntity<MapResponse> couriers(DataGridParams params) {
        return ((DbTransaction) () -> new TransporterTransaction().couriers(params)).read();
    }

    @GetMapping("couriers/{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> couriers(@PathVariable long id) {
        return ((DbTransaction) () -> new TransporterTransaction().courier(id)).read();
    }

    @PostMapping("couriers")
    @IsAdmin
    public ResponseEntity<MapResponse> couriers(
            @RequestParam String courier,
            @RequestParam String display,
            @RequestParam String tracking) {
        return ((DbTransaction) () -> new TransporterTransaction().courier(courier, display, tracking)).write();
    }

    @PutMapping("couriers/{id}")
    @IsAdmin
    public ResponseEntity<MapResponse> couriers(
            @PathVariable long id,
            @RequestParam String courier,
            @RequestParam String display,
            @RequestParam String tracking) {
        return ((DbTransaction) () -> new TransporterTransaction().courier(id, courier, display, tracking)).write();
    }

    @PostMapping("couriers/{id}/serviceable-postcodes")
    @IsAdmin
    public ResponseEntity<MapResponse> serviceablePostcodes(
            @PathVariable long id,
            @RequestParam MultipartFile excel) {
        return ((DbTransaction) () -> new TransporterTransaction().serviceablePostcodes(id, excel)).write();
    }
}
