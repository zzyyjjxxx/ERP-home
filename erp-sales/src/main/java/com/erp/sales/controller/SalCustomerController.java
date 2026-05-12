package com.erp.sales.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.annotation.OperLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.base.BaseController;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.sales.entity.SalCustomer;
import com.erp.sales.service.SalCustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sales/customer")
@RequiredArgsConstructor
public class SalCustomerController extends BaseController {

    private final SalCustomerService customerService;

    @GetMapping("/list")
    @RequirePermission("sales:customer:list")
    public Result<PageResult<SalCustomer>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword) {
        Page<SalCustomer> page = customerService.lambdaQuery()
                .like(keyword != null, SalCustomer::getName, keyword)
                .or().like(keyword != null, SalCustomer::getCode, keyword)
                .orderByDesc(SalCustomer::getCreateTime)
                .page(new Page<>(pageNum, pageSize));
        return pageResult(page);
    }

    @GetMapping("/all")
    public Result<List<SalCustomer>> all() {
        return Result.ok(customerService.list());
    }

    @PostMapping
    @RequirePermission("sales:customer:add")
    @OperLog(module = "销售管理", action = "新增客户")
    public Result<?> add(@RequestBody SalCustomer customer) {
        customerService.save(customer);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @RequirePermission("sales:customer:edit")
    public Result<?> update(@PathVariable Long id, @RequestBody SalCustomer customer) {
        customer.setId(id);
        customerService.updateById(customer);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("sales:customer:delete")
    public Result<?> delete(@PathVariable Long id) {
        customerService.removeById(id);
        return Result.ok();
    }
}
