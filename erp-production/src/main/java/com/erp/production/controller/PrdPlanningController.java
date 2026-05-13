package com.erp.production.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.base.BaseController;
import com.erp.common.base.SortHelper;
import com.erp.common.exception.BusinessException;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.production.entity.PrdPlanning;
import com.erp.production.mapper.PrdPlanningMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/production/planning")
@RequiredArgsConstructor
public class PrdPlanningController extends BaseController {

    private final PrdPlanningMapper planningMapper;

    @GetMapping("/list")
    public Result<PageResult<PrdPlanning>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {
        Map<String, SFunction<PrdPlanning, ?>> fieldMap = Map.of(
            "planNo", PrdPlanning::getPlanNo,
            "createTime", PrdPlanning::getCreateTime
        );
        LambdaQueryWrapper<PrdPlanning> wrapper = new LambdaQueryWrapper<PrdPlanning>()
                .eq(status != null, PrdPlanning::getStatus, status);
        SortHelper.applySort(wrapper, sortField, sortOrder, PrdPlanning::getCreateTime, fieldMap);
        Page<PrdPlanning> page = planningMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return pageResult(page);
    }

    @GetMapping("/{id}")
    public Result<PrdPlanning> getById(@PathVariable Long id) {
        return Result.ok(planningMapper.selectById(id));
    }

    @PostMapping
    public Result<?> add(@RequestBody PrdPlanning plan) {
        plan.setPlanNo(generatePlanNo());
        plan.setStatus(0);
        planningMapper.insert(plan);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody PrdPlanning plan) {
        plan.setId(id);
        planningMapper.updateById(plan);
        return Result.ok();
    }

    @PutMapping("/{id}/audit")
    public Result<?> audit(@PathVariable Long id) {
        PrdPlanning plan = planningMapper.selectById(id);
        if (plan == null) {
            throw new BusinessException("生产计划不存在");
        }
        if (plan.getStatus() != 0) {
            throw new BusinessException("只有草稿状态的计划才能审核");
        }
        plan.setStatus(1);
        planningMapper.updateById(plan);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        planningMapper.deleteById(id);
        return Result.ok();
    }

    private String generatePlanNo() {
        return "PL" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + String.format("%04d", System.currentTimeMillis() % 10000);
    }
}
