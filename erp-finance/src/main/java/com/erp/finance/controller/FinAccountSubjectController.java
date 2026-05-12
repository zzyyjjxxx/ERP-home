package com.erp.finance.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.common.response.Result;
import com.erp.finance.entity.FinAccountSubject;
import com.erp.finance.mapper.FinAccountSubjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/finance/subject")
@RequiredArgsConstructor
public class FinAccountSubjectController {

    private final FinAccountSubjectMapper subjectMapper;

    @GetMapping("/tree")
    public Result<List<FinAccountSubject>> tree() {
        List<FinAccountSubject> list = subjectMapper.selectList(
                new LambdaQueryWrapper<FinAccountSubject>()
                        .orderByAsc(FinAccountSubject::getSort));
        return Result.ok(list);
    }

    @PostMapping
    public Result<?> add(@RequestBody FinAccountSubject subject) {
        subjectMapper.insert(subject);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody FinAccountSubject subject) {
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
