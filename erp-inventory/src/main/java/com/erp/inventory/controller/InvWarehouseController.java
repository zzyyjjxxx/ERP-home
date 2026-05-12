package com.erp.inventory.controller;

import com.erp.common.annotation.OperLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.response.Result;
import com.erp.inventory.entity.InvWarehouse;
import com.erp.inventory.mapper.InvWarehouseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventory/warehouse")
@RequiredArgsConstructor
public class InvWarehouseController {

    private final InvWarehouseMapper warehouseMapper;

    @GetMapping("/list")
    @RequirePermission("inventory:warehouse:list")
    public Result<List<InvWarehouse>> list() {
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
