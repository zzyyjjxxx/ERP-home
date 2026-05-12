package com.erp.purchase.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.annotation.OperLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.base.BaseController;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.purchase.entity.PurSupplier;
import com.erp.purchase.service.PurSupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/purchase/supplier")
@RequiredArgsConstructor
public class PurSupplierController extends BaseController {

    private final PurSupplierService supplierService;

    @GetMapping("/list")
    @RequirePermission("purchase:supplier:list")
    public Result<PageResult<PurSupplier>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword) {
        Page<PurSupplier> page = supplierService.lambdaQuery()
                .like(keyword != null, PurSupplier::getName, keyword)
                .or().like(keyword != null, PurSupplier::getCode, keyword)
                .orderByDesc(PurSupplier::getCreateTime)
                .page(new Page<>(pageNum, pageSize));
        return pageResult(page);
    }

    @GetMapping("/all")
    public Result<List<PurSupplier>> all() {
        return Result.ok(supplierService.list());
    }

    @GetMapping("/{id}")
    public Result<PurSupplier> getById(@PathVariable Long id) {
        return Result.ok(supplierService.getById(id));
    }

    @PostMapping
    @RequirePermission("purchase:supplier:add")
    @OperLog(module = "采购管理", action = "新增供应商")
    public Result<?> add(@RequestBody PurSupplier supplier) {
        supplierService.save(supplier);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @RequirePermission("purchase:supplier:edit")
    @OperLog(module = "采购管理", action = "编辑供应商")
    public Result<?> update(@PathVariable Long id, @RequestBody PurSupplier supplier) {
        supplier.setId(id);
        supplierService.updateById(supplier);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("purchase:supplier:delete")
    @OperLog(module = "采购管理", action = "删除供应商")
    public Result<?> delete(@PathVariable Long id) {
        supplierService.removeById(id);
        return Result.ok();
    }
}
