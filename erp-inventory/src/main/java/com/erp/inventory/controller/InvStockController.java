package com.erp.inventory.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.base.BaseController;
import com.erp.common.base.SortHelper;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.inventory.entity.InvStock;
import com.erp.inventory.entity.InvStockFlow;
import com.erp.inventory.mapper.InvStockFlowMapper;
import com.erp.inventory.service.InvStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/inventory/stock")
@RequiredArgsConstructor
public class InvStockController extends BaseController {

    private final InvStockService stockService;
    private final InvStockFlowMapper stockFlowMapper;

    @GetMapping("/list")
    @RequirePermission("inventory:stock:list")
    public Result<PageResult<InvStock>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {
        Page<InvStock> page = stockService.pageStock(pageNum, pageSize, warehouseId, categoryId, sortField, sortOrder);
        return pageResult(page);
    }

    @GetMapping("/flow")
    @RequirePermission("inventory:stock:list")
    public Result<PageResult<InvStockFlow>> flow(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {
        Map<String, SFunction<InvStockFlow, ?>> fieldMap = Map.of(
            "createTime", InvStockFlow::getCreateTime
        );
        LambdaQueryWrapper<InvStockFlow> wrapper = new LambdaQueryWrapper<InvStockFlow>()
                .eq(productId != null, InvStockFlow::getProductId, productId);
        SortHelper.applySort(wrapper, sortField, sortOrder, InvStockFlow::getCreateTime, fieldMap);
        Page<InvStockFlow> page = stockFlowMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return pageResult(page);
    }
}
