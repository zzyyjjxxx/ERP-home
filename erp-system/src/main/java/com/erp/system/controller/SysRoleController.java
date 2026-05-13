package com.erp.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.base.BaseController;
import com.erp.common.base.SortHelper;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.system.entity.SysRole;
import com.erp.system.service.SysRoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system/role")
public class SysRoleController extends BaseController {

    private final SysRoleService roleService;

    public SysRoleController(SysRoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/list")
    @RequirePermission("system:role:list")
    public Result<PageResult<SysRole>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {
        Map<String, SFunction<SysRole, ?>> fieldMap = Map.of(
            "roleCode", SysRole::getRoleCode,
            "roleName", SysRole::getRoleName,
            "createTime", SysRole::getCreateTime
        );
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<SysRole>()
                .like(keyword != null, SysRole::getRoleName, keyword)
                .or().like(keyword != null, SysRole::getRoleCode, keyword);
        SortHelper.applySort(wrapper, sortField, sortOrder, SysRole::getCreateTime, fieldMap);
        Page<SysRole> page = roleService.page(new Page<>(pageNum, pageSize), wrapper);
        return pageResult(page);
    }

    @PostMapping
    @RequirePermission("system:role:add")
    public Result<?> add(@RequestBody SysRole role) {
        roleService.addRole(role);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @RequirePermission("system:role:edit")
    public Result<?> update(@PathVariable Long id, @RequestBody SysRole role) {
        role.setId(id);
        roleService.updateRole(role);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("system:role:delete")
    public Result<?> delete(@PathVariable Long id) {
        roleService.removeById(id);
        return Result.ok();
    }

    @GetMapping("/{id}/menus")
    public Result<List<Long>> getRoleMenus(@PathVariable Long id) {
        return Result.ok(roleService.getRoleMenus(id));
    }

    @PutMapping("/{id}/menus")
    @RequirePermission("system:role:edit")
    public Result<?> assignMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        roleService.assignMenus(id, menuIds);
        return Result.ok();
    }
}
