package com.erp.sales.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.annotation.OperLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.base.BaseController;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.sales.dto.CreateReturnRequest;
import com.erp.sales.entity.SalReturn;
import com.erp.sales.entity.SalReturnItem;
import com.erp.sales.service.SalReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sales/return")
@RequiredArgsConstructor
public class SalReturnController extends BaseController {

    private final SalReturnService returnService;

    @GetMapping("/list")
    @RequirePermission("sales:return:list")
    public Result<PageResult<SalReturn>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Integer status) {
        Page<SalReturn> page = returnService.pageReturns(pageNum, pageSize, status);
        return pageResult(page);
    }

    @GetMapping("/{id}")
    public Result<SalReturn> getById(@PathVariable Long id) {
        return Result.ok(returnService.getById(id));
    }

    @GetMapping("/{id}/items")
    public Result<List<SalReturnItem>> getItems(@PathVariable Long id) {
        return Result.ok(returnService.getReturnItems(id));
    }

    @PostMapping
    @RequirePermission("sales:return:add")
    @OperLog(module = "销售管理", action = "新增销售退货单")
    public Result<?> add(@RequestBody CreateReturnRequest request) {
        returnService.createReturn(request.getSalReturn(), request.getItems() != null ? request.getItems() : List.of());
        return Result.ok();
    }

    @PutMapping("/{id}/complete")
    @RequirePermission("sales:return:audit")
    public Result<?> complete(@PathVariable Long id) {
        returnService.completeReturn(id);
        return Result.ok();
    }
}
