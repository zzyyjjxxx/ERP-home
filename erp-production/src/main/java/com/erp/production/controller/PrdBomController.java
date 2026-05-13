package com.erp.production.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.base.BaseController;
import com.erp.common.base.SortHelper;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.production.entity.PrdBom;
import com.erp.production.entity.PrdBomItem;
import com.erp.production.service.PrdBomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/production/bom")
@RequiredArgsConstructor
public class PrdBomController extends BaseController {

    private final PrdBomService bomService;

    @GetMapping("/list")
    public Result<PageResult<PrdBom>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {
        Map<String, SFunction<PrdBom, ?>> fieldMap = Map.of(
            "createTime", PrdBom::getCreateTime
        );
        LambdaQueryWrapper<PrdBom> wrapper = new LambdaQueryWrapper<PrdBom>();
        SortHelper.applySort(wrapper, sortField, sortOrder, PrdBom::getCreateTime, fieldMap);
        Page<PrdBom> page = bomService.page(new Page<>(pageNum, pageSize), wrapper);
        return pageResult(page);
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
