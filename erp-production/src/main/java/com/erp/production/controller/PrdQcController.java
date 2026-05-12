package com.erp.production.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.base.BaseController;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.production.entity.PrdQc;
import com.erp.production.mapper.PrdQcMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/production/qc")
@RequiredArgsConstructor
public class PrdQcController extends BaseController {
    private final PrdQcMapper qcMapper;

    @GetMapping("/list")
    public Result<PageResult<PrdQc>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<PrdQc> page = qcMapper.selectPage(new Page<>(pageNum, pageSize),
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PrdQc>()
                        .orderByDesc(PrdQc::getCreateTime));
        return pageResult(page);
    }

    @PostMapping
    public Result<?> add(@RequestBody PrdQc qc) {
        qc.setQcNo("QC" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + String.format("%04d", System.currentTimeMillis() % 10000));
        qcMapper.insert(qc);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        qcMapper.deleteById(id);
        return Result.ok();
    }
}
