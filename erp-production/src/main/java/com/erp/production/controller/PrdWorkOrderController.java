package com.erp.production.controller;

import com.erp.common.base.BaseController;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.production.entity.PrdWorkOrder;
import com.erp.production.entity.PrdWorkOrderItem;
import com.erp.production.service.PrdWorkOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/production/workorder")
@RequiredArgsConstructor
public class PrdWorkOrderController extends BaseController {

    private final PrdWorkOrderService workOrderService;

    @GetMapping("/list")
    public Result<PageResult<PrdWorkOrder>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Integer status) {
        return pageResult(workOrderService.pageOrders(pageNum, pageSize, status));
    }

    @GetMapping("/{id}")
    public Result<PrdWorkOrder> getById(@PathVariable Long id) {
        return Result.ok(workOrderService.getById(id));
    }

    @GetMapping("/{id}/items")
    public Result<List<PrdWorkOrderItem>> getItems(@PathVariable Long id) {
        return Result.ok(workOrderService.getWorkOrderItems(id));
    }

    @PostMapping
    public Result<?> add(@RequestBody PrdWorkOrder wo) {
        workOrderService.createWorkOrder(wo, null);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody PrdWorkOrder wo) {
        wo.setId(id);
        workOrderService.updateById(wo);
        return Result.ok();
    }

    @PutMapping("/{id}/start")
    public Result<?> start(@PathVariable Long id) {
        workOrderService.startProduction(id);
        return Result.ok();
    }

    @PutMapping("/{id}/complete")
    public Result<?> complete(@PathVariable Long id,
                              @RequestParam(required = false) BigDecimal actualQty) {
        workOrderService.completeProduction(id, actualQty);
        return Result.ok();
    }

    @PutMapping("/{id}/close")
    public Result<?> close(@PathVariable Long id) {
        workOrderService.closeOrder(id);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        workOrderService.removeById(id);
        return Result.ok();
    }
}
