package com.erp.purchase.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.annotation.OperLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.base.BaseController;
import com.erp.common.base.SortHelper;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.purchase.entity.PurSupplier;
import com.erp.purchase.service.PurSupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

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
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {
        Map<String, SFunction<PurSupplier, ?>> fieldMap = Map.of(
            "code", PurSupplier::getCode,
            "name", PurSupplier::getName,
            "createTime", PurSupplier::getCreateTime
        );
        LambdaQueryWrapper<PurSupplier> wrapper = new LambdaQueryWrapper<PurSupplier>()
                .like(keyword != null, PurSupplier::getName, keyword)
                .or().like(keyword != null, PurSupplier::getCode, keyword);
        SortHelper.applySort(wrapper, sortField, sortOrder, PurSupplier::getCreateTime, fieldMap);
        Page<PurSupplier> page = supplierService.page(new Page<>(pageNum, pageSize), wrapper);
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
        supplierService.addSupplier(supplier);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @RequirePermission("purchase:supplier:edit")
    @OperLog(module = "采购管理", action = "编辑供应商")
    public Result<?> update(@PathVariable Long id, @RequestBody PurSupplier supplier) {
        supplier.setId(id);
        supplierService.updateSupplier(supplier);
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
