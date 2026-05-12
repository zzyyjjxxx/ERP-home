package com.erp.inventory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.annotation.OperLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.base.BaseController;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.inventory.entity.InvCategory;
import com.erp.inventory.mapper.InvCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/category")
@RequiredArgsConstructor
public class InvCategoryController extends BaseController {

    private final InvCategoryMapper categoryMapper;

    @GetMapping("/list")
    @RequirePermission("inventory:category:list")
    public Result<PageResult<InvCategory>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword) {
        Page<InvCategory> page = categoryMapper.selectPage(new Page<>(pageNum, pageSize),
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<InvCategory>()
                        .like(keyword != null, InvCategory::getName, keyword)
                        .or().like(keyword != null, InvCategory::getCode, keyword)
                        .orderByAsc(InvCategory::getSort));
        return pageResult(page);
    }

    @GetMapping("/all")
    public Result<List<InvCategory>> all() {
        return Result.ok(categoryMapper.selectList(null));
    }

    @GetMapping("/{id}")
    public Result<InvCategory> getById(@PathVariable Long id) {
        return Result.ok(categoryMapper.selectById(id));
    }

    @PostMapping
    @RequirePermission("inventory:category:add")
    @OperLog(module = "库存管理", action = "新增商品分类")
    public Result<?> add(@RequestBody InvCategory category) {
        categoryMapper.insert(category);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @RequirePermission("inventory:category:edit")
    @OperLog(module = "库存管理", action = "编辑商品分类")
    public Result<?> update(@PathVariable Long id, @RequestBody InvCategory category) {
        category.setId(id);
        categoryMapper.updateById(category);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("inventory:category:delete")
    public Result<?> delete(@PathVariable Long id) {
        categoryMapper.deleteById(id);
        return Result.ok();
    }
}
