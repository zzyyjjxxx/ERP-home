package com.erp.inventory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.annotation.OperLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.base.BaseController;
import com.erp.common.exception.BusinessException;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.inventory.entity.InvTransfer;
import com.erp.inventory.entity.InvTransferItem;
import com.erp.inventory.mapper.InvTransferItemMapper;
import com.erp.inventory.mapper.InvTransferMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/transfer")
@RequiredArgsConstructor
public class InvTransferController extends BaseController {

    private final InvTransferMapper transferMapper;
    private final InvTransferItemMapper transferItemMapper;

    @GetMapping("/list")
    @RequirePermission("inventory:transfer:list")
    public Result<PageResult<InvTransfer>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Integer status) {
        Page<InvTransfer> page = transferMapper.selectPage(new Page<>(pageNum, pageSize),
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<InvTransfer>()
                        .eq(status != null, InvTransfer::getStatus, status)
                        .orderByDesc(InvTransfer::getCreateTime));
        return pageResult(page);
    }

    @GetMapping("/{id}")
    public Result<InvTransfer> getById(@PathVariable Long id) {
        return Result.ok(transferMapper.selectById(id));
    }

    @GetMapping("/{id}/items")
    public Result<List<InvTransferItem>> items(@PathVariable Long id) {
        List<InvTransferItem> items = transferItemMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<InvTransferItem>()
                        .eq(InvTransferItem::getTransferId, id));
        return Result.ok(items);
    }

    @PostMapping
    @RequirePermission("inventory:transfer:add")
    @OperLog(module = "库存管理", action = "新增调拨单")
    public Result<?> add(@RequestBody InvTransfer transfer) {
        transferMapper.insert(transfer);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @RequirePermission("inventory:transfer:edit")
    @OperLog(module = "库存管理", action = "编辑调拨单")
    public Result<?> update(@PathVariable Long id, @RequestBody InvTransfer transfer) {
        transfer.setId(id);
        transferMapper.updateById(transfer);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("inventory:transfer:delete")
    public Result<?> delete(@PathVariable Long id) {
        transferMapper.deleteById(id);
        return Result.ok();
    }

    @PostMapping("/{id}/items")
    @RequirePermission("inventory:transfer:edit")
    public Result<?> saveItems(@PathVariable Long id, @RequestBody List<InvTransferItem> items) {
        transferItemMapper.delete(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<InvTransferItem>()
                        .eq(InvTransferItem::getTransferId, id));
        for (InvTransferItem item : items) {
            item.setTransferId(id);
            transferItemMapper.insert(item);
        }
        return Result.ok();
    }

    @PutMapping("/{id}/audit")
    @RequirePermission("inventory:transfer:audit")
    @OperLog(module = "库存管理", action = "审核调拨单")
    @Transactional
    public Result<?> audit(@PathVariable Long id) {
        InvTransfer transfer = transferMapper.selectById(id);
        if (transfer == null) throw new BusinessException("调拨单不存在");
        int currentStatus = transfer.getStatus() != null ? transfer.getStatus() : 0;
        if (currentStatus >= 3) throw new BusinessException("调拨单已完成，不能继续审核");
        transfer.setStatus(currentStatus + 1);
        transferMapper.updateById(transfer);
        return Result.ok();
    }

    @PutMapping("/{id}/unaudit")
    @RequirePermission("inventory:transfer:audit")
    @OperLog(module = "库存管理", action = "反审核调拨单")
    @Transactional
    public Result<?> unaudit(@PathVariable Long id) {
        InvTransfer transfer = transferMapper.selectById(id);
        if (transfer == null) throw new BusinessException("调拨单不存在");
        int currentStatus = transfer.getStatus() != null ? transfer.getStatus() : 0;
        if (currentStatus <= 0) throw new BusinessException("调拨单为待审状态，不能反审核");
        transfer.setStatus(currentStatus - 1);
        transferMapper.updateById(transfer);
        return Result.ok();
    }
}
