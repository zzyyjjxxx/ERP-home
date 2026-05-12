package com.erp.purchase.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.annotation.OperLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.base.BaseController;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.purchase.dto.CreateReturnRequest;
import com.erp.purchase.entity.PurReturn;
import com.erp.purchase.entity.PurReturnItem;
import com.erp.purchase.service.PurReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/purchase/return")
@RequiredArgsConstructor
public class PurReturnController extends BaseController {

    private final PurReturnService returnService;

    @GetMapping("/list")
    @RequirePermission("purchase:return:list")
    public Result<PageResult<PurReturn>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Integer status) {
        Page<PurReturn> page = returnService.pageReturns(pageNum, pageSize, status);
        return pageResult(page);
    }

    @GetMapping("/{id}")
    public Result<PurReturn> getById(@PathVariable Long id) {
        return Result.ok(returnService.getById(id));
    }

    @GetMapping("/{id}/items")
    public Result<List<PurReturnItem>> getItems(@PathVariable Long id) {
        return Result.ok(returnService.getReturnItems(id));
    }

    @PostMapping
    @RequirePermission("purchase:return:add")
    @OperLog(module = "采购管理", action = "新增退货单")
    public Result<?> add(@RequestBody CreateReturnRequest request) {
        returnService.createReturn(request.getPurReturn(), request.getItems() != null ? request.getItems() : List.of());
        return Result.ok();
    }

    @PutMapping("/{id}/complete")
    @RequirePermission("purchase:return:audit")
    public Result<?> complete(@PathVariable Long id) {
        returnService.completeReturn(id);
        return Result.ok();
    }

    @PutMapping("/{id}/unaudit")
    @RequirePermission("purchase:return:audit")
    @OperLog(module = "采购管理", action = "反审核退货单")
    public Result<?> unaudit(@PathVariable Long id) {
        returnService.unauditReturn(id);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @RequirePermission("purchase:return:edit")
    @OperLog(module = "采购管理", action = "编辑退货单")
    public Result<?> update(@PathVariable Long id, @RequestBody PurReturn purReturn) {
        purReturn.setId(id);
        returnService.updateById(purReturn);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("purchase:return:delete")
    public Result<?> delete(@PathVariable Long id) {
        returnService.removeById(id);
        return Result.ok();
    }
}
