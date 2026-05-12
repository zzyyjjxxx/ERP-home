package com.erp.production.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.base.BaseController;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.production.entity.PrdPlanning;
import com.erp.production.mapper.PrdPlanningMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/production/planning")
@RequiredArgsConstructor
public class PrdPlanningController extends BaseController {
    private final PrdPlanningMapper planningMapper;

    @GetMapping("/list")
    public Result<PageResult<PrdPlanning>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Integer status) {
        Page<PrdPlanning> page = planningMapper.selectPage(new Page<>(pageNum, pageSize),
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PrdPlanning>()
                        .eq(status != null, PrdPlanning::getStatus, status)
                        .orderByDesc(PrdPlanning::getCreateTime));
        return pageResult(page);
    }

    @PostMapping
    public Result<?> add(@RequestBody PrdPlanning plan) {
        plan.setPlanNo("PL" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + String.format("%04d", System.currentTimeMillis() % 10000));
        plan.setStatus(0);
        planningMapper.insert(plan);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        planningMapper.deleteById(id);
        return Result.ok();
    }
}
