package com.erp.finance.controller;

import com.erp.common.annotation.RequirePermission;
import com.erp.common.base.BaseController;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.finance.entity.FinVoucher;
import com.erp.finance.entity.FinVoucherItem;
import com.erp.finance.service.FinVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/finance/voucher")
@RequiredArgsConstructor
public class FinVoucherController extends BaseController {

    private final FinVoucherService voucherService;

    @GetMapping("/list")
    @RequirePermission("finance:voucher:list")
    public Result<PageResult<FinVoucher>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Integer status) {
        return pageResult(voucherService.pageVouchers(pageNum, pageSize, status));
    }

    @GetMapping("/{id}")
    public Result<FinVoucher> getById(@PathVariable Long id) {
        FinVoucher voucher = voucherService.getById(id);
        voucher.setChildren(voucherService.getVoucherItems(id));
        return Result.ok(voucher);
    }

    @GetMapping("/{id}/items")
    public Result<List<FinVoucherItem>> getItems(@PathVariable Long id) {
        return Result.ok(voucherService.getVoucherItems(id));
    }

    @PostMapping
    @RequirePermission("finance:voucher:add")
    public Result<?> create(@RequestBody FinVoucher voucher) {
        List<FinVoucherItem> items = voucher.getChildren() != null ? voucher.getChildren() : List.of();
        voucherService.createVoucher(voucher, items);
        return Result.ok();
    }

    @PutMapping("/{id}/audit")
    @RequirePermission("finance:voucher:audit")
    public Result<?> audit(@PathVariable Long id) {
        voucherService.auditVoucher(id);
        return Result.ok();
    }

    @PutMapping("/{id}/unaudit")
    @RequirePermission("finance:voucher:audit")
    public Result<?> unaudit(@PathVariable Long id) {
        voucherService.unauditVoucher(id);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @RequirePermission("finance:voucher:edit")
    public Result<?> update(@PathVariable Long id, @RequestBody FinVoucher voucher) {
        voucher.setId(id);
        voucherService.updateById(voucher);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("finance:voucher:delete")
    public Result<?> delete(@PathVariable Long id) {
        voucherService.removeById(id);
        return Result.ok();
    }
}
