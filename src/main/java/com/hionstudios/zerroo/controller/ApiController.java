package com.hionstudios.zerroo.controller;

import java.io.File;
import java.io.IOException;

import javax.annotation.security.PermitAll;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hionstudios.CachedSelect;
import com.hionstudios.CommonUtil;
import com.hionstudios.ListResponse;
import com.hionstudios.MapResponse;
import com.hionstudios.db.DbTransaction;

@RestController
@RequestMapping("api")
public class ApiController {

    @GetMapping("select/{select}")
    @PermitAll
    public ResponseEntity<MapResponse> select(@PathVariable String select) {
        return ((DbTransaction) () -> new CachedSelect().select(select)).read();
    }

    @GetMapping("ui/nav/categories")
    @PermitAll
    public ResponseEntity<MapResponse> navs() {
        return ((DbTransaction) () -> new CommonUtil().categories()).read();
    }

    @GetMapping("ui/featured")
    public ResponseEntity<MapResponse> featured() {
        return ((DbTransaction) () -> new CommonUtil().featured()).read();
    }

    @GetMapping("ifsc/{ifsc}")
    @PermitAll
    public ResponseEntity<MapResponse> ifsc(@PathVariable String ifsc) throws IOException {
        return ResponseEntity.ok(CommonUtil.ifsc(ifsc));
    }

    @GetMapping("file-path")
    public ResponseEntity<MapResponse> file() {
        File file = new File("");
        return ResponseEntity.ok(MapResponse.success(file.getAbsolutePath()));
    }

    @GetMapping("list-files")
    public ResponseEntity<MapResponse> file(@RequestParam String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        ListResponse list = new ListResponse();
        for (int i = 0; i < files.length; i++) {
            list.add(files[i].getName());
        }
        return ResponseEntity.ok(MapResponse.success().put("list", list));
    }

    @GetMapping("top-earners")
    @CrossOrigin("https://top-earners.zerroo.in")
    public ResponseEntity<MapResponse> topEarners() {
        return ((DbTransaction) () -> new CommonUtil().topEarners()).read();
    }
}
