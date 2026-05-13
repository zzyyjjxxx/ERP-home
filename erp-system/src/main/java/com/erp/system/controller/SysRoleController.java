package com.erp.system.controller;

import com.erp.common.annotation.RequirePermission;
import com.erp.common.response.Result;
import com.erp.system.entity.SysRole;
import com.erp.system.service.SysRoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/role")
public class SysRoleController {

    private final SysRoleService roleService;

    public SysRoleController(SysRoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/list")
    @RequirePermission("system:role:list")
    public Result<List<SysRole>> list() {
        return Result.ok(roleService.lambdaQuery().orderByDesc(SysRole::getCreateTime).list());
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
