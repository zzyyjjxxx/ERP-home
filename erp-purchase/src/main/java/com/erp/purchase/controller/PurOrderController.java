package com.erp.purchase.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.annotation.OperLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.base.BaseController;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.purchase.dto.CreateOrderRequest;
import com.erp.purchase.entity.PurOrder;
import com.erp.purchase.entity.PurOrderItem;
import com.erp.purchase.entity.PurReceipt;
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
    public Result<?> add(@RequestBody CreateOrderRequest request) {
        orderService.createOrder(request.getOrder(), request.getItems() != null ? request.getItems() : List.of());
        return Result.ok();
    }

    @PutMapping("/{id}")
    @RequirePermission("purchase:order:edit")
    @OperLog(module = "采购管理", action = "编辑采购订单")
    public Result<?> update(@PathVariable Long id, @RequestBody PurOrder order) {
        order.setId(id);
        orderService.updateById(order);
        return Result.ok();
    }

    @PutMapping("/{id}/audit")
    @RequirePermission("purchase:order:audit")
    @OperLog(module = "采购管理", action = "审核采购订单")
    public Result<?> audit(@PathVariable Long id) {
        orderService.auditOrder(id);
        return Result.ok();
    }

    @PutMapping("/{id}/unaudit")
    @RequirePermission("purchase:order:audit")
    @OperLog(module = "采购管理", action = "反审核采购订单")
    public Result<?> unaudit(@PathVariable Long id) {
        orderService.unauditOrder(id);
        return Result.ok();
    }

    @PutMapping("/{id}/cancel")
    @RequirePermission("purchase:order:cancel")
    public Result<?> cancel(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return Result.ok();
    }

    @PostMapping("/{id}/push-receipt")
    @RequirePermission("purchase:order:audit")
    @OperLog(module = "采购管理", action = "下推生成收货单")
    public Result<PurReceipt> pushReceipt(@PathVariable Long id) {
        return Result.ok(orderService.pushDownToReceipt(id));
    }

    @DeleteMapping("/{id}")
    @RequirePermission("purchase:order:delete")
    public Result<?> delete(@PathVariable Long id) {
        orderService.removeById(id);
        return Result.ok();
    }
}
