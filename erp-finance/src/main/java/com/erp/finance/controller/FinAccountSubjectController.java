package com.erp.finance.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.base.BaseController;
import com.erp.common.exception.BusinessException;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.finance.entity.FinAccountSubject;
import com.erp.finance.mapper.FinAccountSubjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/finance/subject")
@RequiredArgsConstructor
public class FinAccountSubjectController extends BaseController {

    private final FinAccountSubjectMapper subjectMapper;

    @GetMapping("/list")
    public Result<PageResult<FinAccountSubject>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword) {
        Page<FinAccountSubject> page = subjectMapper.selectPage(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<FinAccountSubject>()
                        .like(keyword != null, FinAccountSubject::getName, keyword)
                        .or().like(keyword != null, FinAccountSubject::getCode, keyword)
                        .orderByAsc(FinAccountSubject::getSort));
        return pageResult(page);
    }

    @GetMapping("/tree")
    public Result<List<FinAccountSubject>> tree() {
        List<FinAccountSubject> list = subjectMapper.selectList(
                new LambdaQueryWrapper<FinAccountSubject>()
                        .orderByAsc(FinAccountSubject::getSort));
        return Result.ok(list);
    }

    @GetMapping("/{id}")
    public Result<FinAccountSubject> getById(@PathVariable Long id) {
        return Result.ok(subjectMapper.selectById(id));
    }

    @PostMapping
    public Result<?> add(@RequestBody FinAccountSubject subject) {
        if (subjectMapper.selectCount(new LambdaQueryWrapper<FinAccountSubject>()
                .eq(FinAccountSubject::getCode, subject.getCode())) > 0) {
            throw new BusinessException("科目编码已存在");
        }
        subjectMapper.insert(subject);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody FinAccountSubject subject) {
        if (subjectMapper.selectCount(new LambdaQueryWrapper<FinAccountSubject>()
                .eq(FinAccountSubject::getCode, subject.getCode()).ne(FinAccountSubject::getId, id)) > 0) {
            throw new BusinessException("科目编码已存在");
        }
        subject.setId(id);
        subjectMapper.updateById(subject);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        subjectMapper.deleteById(id);
        return Result.ok();
    }
}
