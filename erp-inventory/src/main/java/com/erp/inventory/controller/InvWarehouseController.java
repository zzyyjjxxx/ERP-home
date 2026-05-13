package com.erp.inventory.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.annotation.OperLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.base.BaseController;
import com.erp.common.base.SortHelper;
import com.erp.common.exception.BusinessException;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.inventory.entity.InvWarehouse;
import com.erp.inventory.mapper.InvWarehouseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory/warehouse")
@RequiredArgsConstructor
public class InvWarehouseController extends BaseController {

    private final InvWarehouseMapper warehouseMapper;

    @GetMapping("/list")
    @RequirePermission("inventory:warehouse:list")
    public Result<PageResult<InvWarehouse>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {
        Map<String, SFunction<InvWarehouse, ?>> fieldMap = Map.of(
            "code", InvWarehouse::getCode,
            "name", InvWarehouse::getName,
            "createTime", InvWarehouse::getCreateTime
        );
        LambdaQueryWrapper<InvWarehouse> wrapper = new LambdaQueryWrapper<InvWarehouse>()
                .like(keyword != null, InvWarehouse::getName, keyword)
                .or().like(keyword != null, InvWarehouse::getCode, keyword);
        SortHelper.applySort(wrapper, sortField, sortOrder, InvWarehouse::getCreateTime, fieldMap);
        Page<InvWarehouse> page = warehouseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
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
        if (warehouseMapper.selectCount(new LambdaQueryWrapper<InvWarehouse>()
                .eq(InvWarehouse::getCode, warehouse.getCode())) > 0) {
            throw new BusinessException("仓库编码已存在");
        }
        warehouseMapper.insert(warehouse);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @RequirePermission("inventory:warehouse:edit")
    public Result<?> update(@PathVariable Long id, @RequestBody InvWarehouse warehouse) {
        if (warehouseMapper.selectCount(new LambdaQueryWrapper<InvWarehouse>()
                .eq(InvWarehouse::getCode, warehouse.getCode()).ne(InvWarehouse::getId, id)) > 0) {
            throw new BusinessException("仓库编码已存在");
        }
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
