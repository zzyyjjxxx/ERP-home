package com.erp.purchase.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.annotation.OperLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.base.BaseController;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.purchase.entity.PurOrder;
import com.erp.purchase.entity.PurOrderItem;
import com.erp.purchase.service.PurOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/purchase/order")
@RequiredArgsConstructor
public class PurOrderController extends BaseController {

    private final PurOrderService orderService;

    @GetMapping("/list")
    @RequirePermission("purchase:order:list")
    public Result<PageResult<PurOrder>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long supplierId) {
        Page<PurOrder> page = orderService.pageOrders(pageNum, pageSize, status, supplierId);
        return pageResult(page);
    }

    @GetMapping("/{id}")
    public Result<PurOrder> getById(@PathVariable Long id) {
        return Result.ok(orderService.getById(id));
    }

    @GetMapping("/{id}/items")
    public Result<List<PurOrderItem>> getItems(@PathVariable Long id) {
        return Result.ok(orderService.getOrderItems(id));
    }

    @PostMapping
    @RequirePermission("purchase:order:add")
    @OperLog(module = "采购管理", action = "新增采购订单")
    public Result<?> add(@RequestBody PurOrder order) {
        orderService.createOrder(order, List.of());
        return Result.ok();
    }

    @PutMapping("/{id}/audit")
    @RequirePermission("purchase:order:audit")
    @OperLog(module = "采购管理", action = "审核采购订单")
    public Result<?> audit(@PathVariable Long id) {
        orderService.auditOrder(id);
        return Result.ok();
    }

    @PutMapping("/{id}/cancel")
    @RequirePermission("purchase:order:cancel")
    public Result<?> cancel(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("purchase:order:delete")
    public Result<?> delete(@PathVariable Long id) {
        orderService.removeById(id);
        return Result.ok();
    }
}
