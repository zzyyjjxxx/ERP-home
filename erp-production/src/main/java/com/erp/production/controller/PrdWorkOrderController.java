package com.erp.production.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.base.BaseController;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.production.entity.PrdWorkOrder;
import com.erp.production.mapper.PrdWorkOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/production/workorder")
@RequiredArgsConstructor
public class PrdWorkOrderController extends BaseController {
    private final PrdWorkOrderMapper workOrderMapper;

    @GetMapping("/list")
    public Result<PageResult<PrdWorkOrder>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Integer status) {
        Page<PrdWorkOrder> page = workOrderMapper.selectPage(new Page<>(pageNum, pageSize),
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PrdWorkOrder>()
                        .eq(status != null, PrdWorkOrder::getStatus, status)
                        .orderByDesc(PrdWorkOrder::getCreateTime));
        return pageResult(page);
    }

    @PostMapping
    public Result<?> add(@RequestBody PrdWorkOrder wo) {
        wo.setOrderNo("WO" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + String.format("%04d", System.currentTimeMillis() % 10000));
        wo.setStatus(0);
        workOrderMapper.insert(wo);
        return Result.ok();
    }

    @PutMapping("/{id}/complete")
    public Result<?> complete(@PathVariable Long id) {
        PrdWorkOrder wo = workOrderMapper.selectById(id);
        wo.setStatus(2);
        workOrderMapper.updateById(wo);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        workOrderMapper.deleteById(id);
        return Result.ok();
    }
}
