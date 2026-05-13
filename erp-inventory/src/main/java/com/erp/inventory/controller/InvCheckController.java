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
import com.erp.inventory.entity.InvCheck;
import com.erp.inventory.entity.InvCheckItem;
import com.erp.inventory.mapper.InvCheckItemMapper;
import com.erp.inventory.mapper.InvCheckMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory/check")
@RequiredArgsConstructor
public class InvCheckController extends BaseController {

    private final InvCheckMapper checkMapper;
    private final InvCheckItemMapper checkItemMapper;

    @GetMapping("/list")
    @RequirePermission("inventory:check:list")
    public Result<PageResult<InvCheck>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {
        Map<String, SFunction<InvCheck, ?>> fieldMap = Map.of(
            "checkNo", InvCheck::getCheckNo,
            "createTime", InvCheck::getCreateTime
        );
        LambdaQueryWrapper<InvCheck> wrapper = new LambdaQueryWrapper<InvCheck>()
                .eq(warehouseId != null, InvCheck::getWarehouseId, warehouseId)
                .eq(status != null, InvCheck::getStatus, status);
        SortHelper.applySort(wrapper, sortField, sortOrder, InvCheck::getCreateTime, fieldMap);
        Page<InvCheck> page = checkMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return pageResult(page);
    }

    @GetMapping("/{id}")
    public Result<InvCheck> getById(@PathVariable Long id) {
        return Result.ok(checkMapper.selectById(id));
    }

    @GetMapping("/{id}/items")
    public Result<List<InvCheckItem>> items(@PathVariable Long id) {
        List<InvCheckItem> items = checkItemMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<InvCheckItem>()
                        .eq(InvCheckItem::getCheckId, id));
        return Result.ok(items);
    }

    @PostMapping
    @RequirePermission("inventory:check:add")
    @OperLog(module = "库存管理", action = "新增盘点单")
    public Result<?> add(@RequestBody InvCheck check) {
        checkMapper.insert(check);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @RequirePermission("inventory:check:edit")
    @OperLog(module = "库存管理", action = "编辑盘点单")
    public Result<?> update(@PathVariable Long id, @RequestBody InvCheck check) {
        check.setId(id);
        checkMapper.updateById(check);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("inventory:check:delete")
    public Result<?> delete(@PathVariable Long id) {
        checkMapper.deleteById(id);
        return Result.ok();
    }

    @PostMapping("/{id}/items")
    @RequirePermission("inventory:check:edit")
    public Result<?> saveItems(@PathVariable Long id, @RequestBody List<InvCheckItem> items) {
        checkItemMapper.delete(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<InvCheckItem>()
                        .eq(InvCheckItem::getCheckId, id));
        for (InvCheckItem item : items) {
            item.setCheckId(id);
            checkItemMapper.insert(item);
        }
        return Result.ok();
    }

    @PutMapping("/{id}/audit")
    @RequirePermission("inventory:check:audit")
    @OperLog(module = "库存管理", action = "审核盘点单")
    @Transactional
    public Result<?> audit(@PathVariable Long id) {
        InvCheck check = checkMapper.selectById(id);
        if (check == null) throw new BusinessException("盘点单不存在");
        if (check.getStatus() != null && check.getStatus() != 0) throw new BusinessException("只能审核待审状态的盘点单");
        check.setStatus(1);
        checkMapper.updateById(check);
        return Result.ok();
    }

    @PutMapping("/{id}/unaudit")
    @RequirePermission("inventory:check:audit")
    @OperLog(module = "库存管理", action = "反审核盘点单")
    @Transactional
    public Result<?> unaudit(@PathVariable Long id) {
        InvCheck check = checkMapper.selectById(id);
        if (check == null) throw new BusinessException("盘点单不存在");
        if (check.getStatus() == null || check.getStatus() != 1) throw new BusinessException("只能反审核已审状态的盘点单");
        check.setStatus(0);
        checkMapper.updateById(check);
        return Result.ok();
    }
}
