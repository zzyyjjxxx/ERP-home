package com.erp.finance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.base.BaseController;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.finance.entity.FinReceivable;
import com.erp.finance.mapper.FinReceivableMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/finance/receivable")
@RequiredArgsConstructor
public class FinReceivableController extends BaseController {

    private final FinReceivableMapper receivableMapper;

    @GetMapping("/list")
    public Result<PageResult<FinReceivable>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Integer status) {
        Page<FinReceivable> page = receivableMapper.selectPage(new Page<>(pageNum, pageSize),
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<FinReceivable>()
                        .eq(status != null, FinReceivable::getStatus, status)
                        .orderByDesc(FinReceivable::getCreateTime));
        return pageResult(page);
    }

    @GetMapping("/{id}")
    public Result<FinReceivable> getById(@PathVariable Long id) {
        return Result.ok(receivableMapper.selectById(id));
    }

    @PostMapping
    public Result<?> add(@RequestBody FinReceivable receivable) {
        receivableMapper.insert(receivable);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody FinReceivable receivable) {
        receivable.setId(id);
        receivableMapper.updateById(receivable);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        receivableMapper.deleteById(id);
        return Result.ok();
    }
}
