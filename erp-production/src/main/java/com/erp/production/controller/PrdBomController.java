package com.erp.production.controller;

import com.erp.common.response.Result;
import com.erp.production.entity.PrdBom;
import com.erp.production.entity.PrdBomItem;
import com.erp.production.service.PrdBomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/production/bom")
@RequiredArgsConstructor
public class PrdBomController {

    private final PrdBomService bomService;

    @GetMapping("/list")
    public Result<List<PrdBom>> list() {
        return Result.ok(bomService.lambdaQuery().orderByDesc(PrdBom::getCreateTime).list());
    }

    @GetMapping("/{id}")
    public Result<PrdBom> getById(@PathVariable Long id) {
        return Result.ok(bomService.getById(id));
    }

    @GetMapping("/{id}/tree")
    public Result<PrdBom> getTree(@PathVariable Long id) {
        PrdBom bom = bomService.getBomWithItems(id);
        List<PrdBomItem> items = bomService.getBomItems(id);
        return Result.ok(bom);
    }

    @GetMapping("/{id}/items")
    public Result<List<PrdBomItem>> getItems(@PathVariable Long id) {
        return Result.ok(bomService.getBomItems(id));
    }

    @GetMapping("/{id}/explode")
    public Result<List<PrdBomItem>> explode(@PathVariable Long id) {
        return Result.ok(bomService.explodeBom(id));
    }

    @PostMapping
    public Result<?> add(@RequestBody PrdBom bom) {
        bomService.createBom(bom, null);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody PrdBom bom) {
        bom.setId(id);
        bomService.updateBom(bom, null);
        return Result.ok();
    }

    @PutMapping("/{id}/enable")
    public Result<?> enable(@PathVariable Long id) {
        bomService.enableBom(id);
        return Result.ok();
    }

    @PutMapping("/{id}/disable")
    public Result<?> disable(@PathVariable Long id) {
        bomService.disableBom(id);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        bomService.removeById(id);
        return Result.ok();
    }
}
