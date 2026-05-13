package com.erp.production.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.base.BaseController;
import com.erp.common.base.SortHelper;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.production.entity.PrdQc;
import com.erp.production.mapper.PrdQcMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/production/qc")
@RequiredArgsConstructor
public class PrdQcController extends BaseController {

    private final PrdQcMapper qcMapper;

    @GetMapping("/list")
    public Result<PageResult<PrdQc>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Long workOrderId,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {
        Map<String, SFunction<PrdQc, ?>> fieldMap = Map.of(
            "qcNo", PrdQc::getQcNo,
            "createTime", PrdQc::getCreateTime
        );
        LambdaQueryWrapper<PrdQc> wrapper = new LambdaQueryWrapper<PrdQc>()
                .eq(workOrderId != null, PrdQc::getWorkOrderId, workOrderId);
        SortHelper.applySort(wrapper, sortField, sortOrder, PrdQc::getCreateTime, fieldMap);
        Page<PrdQc> page = qcMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return pageResult(page);
    }

    @GetMapping("/{id}")
    public Result<PrdQc> getById(@PathVariable Long id) {
        return Result.ok(qcMapper.selectById(id));
    }

    @PostMapping
    public Result<?> add(@RequestBody PrdQc qc) {
        qc.setQcNo(generateQcNo());
        qcMapper.insert(qc);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody PrdQc qc) {
        qc.setId(id);
        qcMapper.updateById(qc);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        qcMapper.deleteById(id);
        return Result.ok();
    }

    private String generateQcNo() {
        return "QC" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + String.format("%04d", System.currentTimeMillis() % 10000);
    }
}
