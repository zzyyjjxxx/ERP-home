package com.erp.sales.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.annotation.OperLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.base.BaseController;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.sales.entity.SalDelivery;
import com.erp.sales.entity.SalDeliveryItem;
import com.erp.sales.service.SalDeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sales/delivery")
@RequiredArgsConstructor
public class SalDeliveryController extends BaseController {

    private final SalDeliveryService deliveryService;

    @GetMapping("/list")
    @RequirePermission("sales:delivery:list")
    public Result<PageResult<SalDelivery>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Integer status) {
        Page<SalDelivery> page = deliveryService.pageDeliveries(pageNum, pageSize, status);
        return pageResult(page);
    }

    @GetMapping("/{id}/items")
    public Result<List<SalDeliveryItem>> getItems(@PathVariable Long id) {
        return Result.ok(deliveryService.getDeliveryItems(id));
    }

    @PostMapping
    @RequirePermission("sales:delivery:add")
    @OperLog(module = "销售管理", action = "新增发货单")
    public Result<?> add(@RequestBody SalDelivery delivery) {
        deliveryService.createDelivery(delivery, List.of());
        return Result.ok();
    }

    @PutMapping("/{id}/audit")
    @RequirePermission("sales:delivery:audit")
    @OperLog(module = "销售管理", action = "审核发货单")
    public Result<?> audit(@PathVariable Long id) {
        deliveryService.auditDelivery(id);
        return Result.ok();
    }
}
