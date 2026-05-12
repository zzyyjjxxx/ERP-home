package com.erp.production.controller;

import com.erp.common.response.Result;
import com.erp.production.entity.PrdBom;
import com.erp.production.mapper.PrdBomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/production/bom")
@RequiredArgsConstructor
public class PrdBomController {
    private final PrdBomMapper bomMapper;

    @GetMapping("/list")
    public Result<List<PrdBom>> list() {
        return Result.ok(bomMapper.selectList(null));
    }

    @PostMapping
    public Result<?> add(@RequestBody PrdBom bom) {
        bomMapper.insert(bom);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody PrdBom bom) {
        bom.setId(id);
        bomMapper.updateById(bom);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        bomMapper.deleteById(id);
        return Result.ok();
    }
}
