package com.erp.sales.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.annotation.OperLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.base.BaseController;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.sales.dto.CreateOrderRequest;
import com.erp.sales.entity.SalDelivery;
import com.erp.sales.entity.SalOrder;
import com.erp.sales.entity.SalOrderItem;
import com.erp.sales.service.SalOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sales/order")
@RequiredArgsConstructor
public class SalOrderController extends BaseController {

    private final SalOrderService orderService;

    @GetMapping("/list")
    @RequirePermission("sales:order:list")
    public Result<PageResult<SalOrder>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long customerId) {
        Page<SalOrder> page = orderService.pageOrders(pageNum, pageSize, status, customerId);
        return pageResult(page);
    }

    @GetMapping("/{id}")
    public Result<SalOrder> getById(@PathVariable Long id) {
        return Result.ok(orderService.getById(id));
    }

    @GetMapping("/{id}/items")
    public Result<List<SalOrderItem>> getItems(@PathVariable Long id) {
        return Result.ok(orderService.getOrderItems(id));
    }

    @PostMapping
    @RequirePermission("sales:order:add")
    @OperLog(module = "销售管理", action = "新增销售订单")
    public Result<?> add(@RequestBody CreateOrderRequest request) {
        orderService.createOrder(request.getOrder(), request.getItems() != null ? request.getItems() : List.of());
        return Result.ok();
    }

    @PutMapping("/{id}")
    @RequirePermission("sales:order:edit")
    @OperLog(module = "销售管理", action = "编辑销售订单")
    public Result<?> update(@PathVariable Long id, @RequestBody SalOrder order) {
        order.setId(id);
        orderService.updateById(order);
        return Result.ok();
    }

    @PutMapping("/{id}/audit")
    @RequirePermission("sales:order:audit")
    @OperLog(module = "销售管理", action = "审核销售订单")
    public Result<?> audit(@PathVariable Long id) {
        orderService.auditOrder(id);
        return Result.ok();
    }

    @PutMapping("/{id}/unaudit")
    @RequirePermission("sales:order:audit")
    @OperLog(module = "销售管理", action = "反审核销售订单")
    public Result<?> unaudit(@PathVariable Long id) {
        orderService.unauditOrder(id);
        return Result.ok();
    }

    @PutMapping("/{id}/cancel")
    @RequirePermission("sales:order:cancel")
    public Result<?> cancel(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return Result.ok();
    }

    @PostMapping("/{id}/push-delivery")
    @RequirePermission("sales:order:audit")
    @OperLog(module = "销售管理", action = "下推生成发货单")
    public Result<SalDelivery> pushDelivery(@PathVariable Long id) {
        return Result.ok(orderService.pushDownToDelivery(id));
    }

    @DeleteMapping("/{id}")
    @RequirePermission("sales:order:delete")
    public Result<?> delete(@PathVariable Long id) {
        orderService.removeById(id);
        return Result.ok();
    }
}
