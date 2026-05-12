package com.erp.finance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.base.BaseController;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.finance.entity.FinPayable;
import com.erp.finance.mapper.FinPayableMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/finance/payable")
@RequiredArgsConstructor
public class FinPayableController extends BaseController {

    private final FinPayableMapper payableMapper;

    @GetMapping("/list")
    public Result<PageResult<FinPayable>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Integer status) {
        Page<FinPayable> page = payableMapper.selectPage(new Page<>(pageNum, pageSize),
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<FinPayable>()
                        .eq(status != null, FinPayable::getStatus, status)
                        .orderByDesc(FinPayable::getCreateTime));
        return pageResult(page);
    }

    @GetMapping("/{id}")
    public Result<FinPayable> getById(@PathVariable Long id) {
        return Result.ok(payableMapper.selectById(id));
    }

    @PostMapping
    public Result<?> add(@RequestBody FinPayable payable) {
        payableMapper.insert(payable);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody FinPayable payable) {
        payable.setId(id);
        payableMapper.updateById(payable);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        payableMapper.deleteById(id);
        return Result.ok();
    }
}
