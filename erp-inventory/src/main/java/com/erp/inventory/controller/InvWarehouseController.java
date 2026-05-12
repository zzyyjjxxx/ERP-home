package com.erp.inventory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.annotation.OperLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.base.BaseController;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.inventory.entity.InvWarehouse;
import com.erp.inventory.mapper.InvWarehouseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventory/warehouse")
@RequiredArgsConstructor
public class InvWarehouseController extends BaseController {

    private final InvWarehouseMapper warehouseMapper;

    @GetMapping("/list")
    @RequirePermission("inventory:warehouse:list")
    public Result<PageResult<InvWarehouse>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<InvWarehouse> page = warehouseMapper.selectPage(new Page<>(pageNum, pageSize),
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<InvWarehouse>()
                        .orderByDesc(InvWarehouse::getCreateTime));
        return pageResult(page);
    }

    @GetMapping("/all")
    public Result<List<InvWarehouse>> all() {
        return Result.ok(warehouseMapper.selectList(null));
    }

    @GetMapping("/{id}")
    public Result<InvWarehouse> getById(@PathVariable Long id) {
        return Result.ok(warehouseMapper.selectById(id));
    }

    @PostMapping
    @RequirePermission("inventory:warehouse:add")
    @OperLog(module = "库存管理", action = "新增仓库")
    public Result<?> add(@RequestBody InvWarehouse warehouse) {
        warehouseMapper.insert(warehouse);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @RequirePermission("inventory:warehouse:edit")
    public Result<?> update(@PathVariable Long id, @RequestBody InvWarehouse warehouse) {
        warehouse.setId(id);
        warehouseMapper.updateById(warehouse);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("inventory:warehouse:delete")
    public Result<?> delete(@PathVariable Long id) {
        warehouseMapper.deleteById(id);
        return Result.ok();
    }
}
