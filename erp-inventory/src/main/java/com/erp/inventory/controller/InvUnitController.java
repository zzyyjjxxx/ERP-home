package com.erp.inventory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.annotation.OperLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.base.BaseController;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.inventory.entity.InvUnit;
import com.erp.inventory.mapper.InvUnitMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/unit")
@RequiredArgsConstructor
public class InvUnitController extends BaseController {

    private final InvUnitMapper unitMapper;

    @GetMapping("/list")
    @RequirePermission("inventory:unit:list")
    public Result<PageResult<InvUnit>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword) {
        Page<InvUnit> page = unitMapper.selectPage(new Page<>(pageNum, pageSize),
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<InvUnit>()
                        .like(keyword != null, InvUnit::getName, keyword)
                        .or().like(keyword != null, InvUnit::getCode, keyword)
                        .orderByDesc(InvUnit::getCreateTime));
        return pageResult(page);
    }

    @GetMapping("/all")
    public Result<List<InvUnit>> all() {
        return Result.ok(unitMapper.selectList(null));
    }

    @GetMapping("/{id}")
    public Result<InvUnit> getById(@PathVariable Long id) {
        return Result.ok(unitMapper.selectById(id));
    }

    @PostMapping
    @RequirePermission("inventory:unit:add")
    @OperLog(module = "库存管理", action = "新增计量单位")
    public Result<?> add(@RequestBody InvUnit unit) {
        unitMapper.insert(unit);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @RequirePermission("inventory:unit:edit")
    @OperLog(module = "库存管理", action = "编辑计量单位")
    public Result<?> update(@PathVariable Long id, @RequestBody InvUnit unit) {
        unit.setId(id);
        unitMapper.updateById(unit);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("inventory:unit:delete")
    public Result<?> delete(@PathVariable Long id) {
        unitMapper.deleteById(id);
        return Result.ok();
    }
}
