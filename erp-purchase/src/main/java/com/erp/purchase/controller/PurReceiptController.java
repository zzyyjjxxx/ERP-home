package com.erp.purchase.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.annotation.OperLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.base.BaseController;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.purchase.dto.CreateReceiptRequest;
import com.erp.purchase.entity.PurReceipt;
import com.erp.purchase.entity.PurReceiptItem;
import com.erp.purchase.service.PurReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/purchase/receipt")
@RequiredArgsConstructor
public class PurReceiptController extends BaseController {

    private final PurReceiptService receiptService;

    @GetMapping("/list")
    @RequirePermission("purchase:receipt:list")
    public Result<PageResult<PurReceipt>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {
        Page<PurReceipt> page = receiptService.pageReceipts(pageNum, pageSize, status, sortField, sortOrder);
        return pageResult(page);
    }

    @GetMapping("/{id}")
    public Result<PurReceipt> getById(@PathVariable Long id) {
        return Result.ok(receiptService.getById(id));
    }

    @GetMapping("/{id}/items")
    public Result<List<PurReceiptItem>> getItems(@PathVariable Long id) {
        return Result.ok(receiptService.getReceiptItems(id));
    }

    @PostMapping
    @RequirePermission("purchase:receipt:add")
    @OperLog(module = "采购管理", action = "新增收货单")
    public Result<?> add(@RequestBody CreateReceiptRequest request) {
        receiptService.createReceipt(request.getReceipt(), request.getItems() != null ? request.getItems() : List.of());
        return Result.ok();
    }

    @PutMapping("/{id}/audit")
    @RequirePermission("purchase:receipt:audit")
    @OperLog(module = "采购管理", action = "审核收货单")
    public Result<?> audit(@PathVariable Long id) {
        receiptService.auditReceipt(id);
        return Result.ok();
    }

    @PutMapping("/{id}/unaudit")
    @RequirePermission("purchase:receipt:audit")
    @OperLog(module = "采购管理", action = "反审核收货单")
    public Result<?> unaudit(@PathVariable Long id) {
        receiptService.unauditReceipt(id);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @RequirePermission("purchase:receipt:edit")
    @OperLog(module = "采购管理", action = "编辑收货单")
    public Result<?> update(@PathVariable Long id, @RequestBody PurReceipt receipt) {
        receipt.setId(id);
        receiptService.updateById(receipt);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("purchase:receipt:delete")
    public Result<?> delete(@PathVariable Long id) {
        receiptService.removeById(id);
        return Result.ok();
    }
}
