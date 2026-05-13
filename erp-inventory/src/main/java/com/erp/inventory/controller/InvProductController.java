package com.erp.inventory.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.annotation.OperLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.base.BaseController;
import com.erp.common.base.SortHelper;
import com.erp.common.exception.BusinessException;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.inventory.entity.InvProduct;
import com.erp.inventory.mapper.InvProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory/product")
@RequiredArgsConstructor
public class InvProductController extends BaseController {

    private final InvProductMapper productMapper;

    @GetMapping("/list")
    @RequirePermission("inventory:product:list")
    public Result<PageResult<InvProduct>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {
        Map<String, SFunction<InvProduct, ?>> fieldMap = Map.of(
            "code", InvProduct::getCode,
            "name", InvProduct::getName,
            "createTime", InvProduct::getCreateTime
        );
        LambdaQueryWrapper<InvProduct> wrapper = new LambdaQueryWrapper<InvProduct>()
                .like(keyword != null, InvProduct::getName, keyword)
                .or().like(keyword != null, InvProduct::getCode, keyword);
        SortHelper.applySort(wrapper, sortField, sortOrder, InvProduct::getCreateTime, fieldMap);
        Page<InvProduct> page = productMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return pageResult(page);
    }

    @GetMapping("/all")
    public Result<List<InvProduct>> all() {
        return Result.ok(productMapper.selectList(null));
    }

    @GetMapping("/{id}")
    public Result<InvProduct> getById(@PathVariable Long id) {
        return Result.ok(productMapper.selectById(id));
    }

    @PostMapping
    @RequirePermission("inventory:product:add")
    @OperLog(module = "库存管理", action = "新增商品")
    public Result<?> add(@RequestBody InvProduct product) {
        if (productMapper.selectCount(new LambdaQueryWrapper<InvProduct>()
                .eq(InvProduct::getCode, product.getCode())) > 0) {
            throw new BusinessException("商品编码已存在");
        }
        productMapper.insert(product);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @RequirePermission("inventory:product:edit")
    @OperLog(module = "库存管理", action = "编辑商品")
    public Result<?> update(@PathVariable Long id, @RequestBody InvProduct product) {
        if (productMapper.selectCount(new LambdaQueryWrapper<InvProduct>()
                .eq(InvProduct::getCode, product.getCode()).ne(InvProduct::getId, id)) > 0) {
            throw new BusinessException("商品编码已存在");
        }
        product.setId(id);
        productMapper.updateById(product);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("inventory:product:delete")
    public Result<?> delete(@PathVariable Long id) {
        productMapper.deleteById(id);
        return Result.ok();
    }
}
