package com.erp.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.annotation.OperLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.base.BaseController;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.system.entity.SysUser;
import com.erp.system.service.SysUserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/user")
public class SysUserController extends BaseController {

    private final SysUserService userService;

    public SysUserController(SysUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/list")
    @RequirePermission("system:user:list")
    public Result<PageResult<SysUser>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {
        Page<SysUser> page = userService.pageUsers(pageNum, pageSize, keyword, deptId, status, sortField, sortOrder);
        return pageResult(page);
    }

    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Long id) {
        return Result.ok(userService.getById(id));
    }

    @PostMapping
    @RequirePermission("system:user:add")
    @OperLog(module = "用户管理", action = "新增用户")
    public Result<?> add(@RequestBody SysUser user) {
        userService.addUser(user);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @RequirePermission("system:user:edit")
    @OperLog(module = "用户管理", action = "编辑用户")
    public Result<?> update(@PathVariable Long id, @RequestBody SysUser user) {
        user.setId(id);
        userService.updateUser(user);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("system:user:delete")
    @OperLog(module = "用户管理", action = "删除用户")
    public Result<?> delete(@PathVariable Long id) {
        userService.deleteUsers(List.of(id));
        return Result.ok();
    }

    @PutMapping("/{id}/roles")
    @RequirePermission("system:user:edit")
    public Result<?> assignRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        userService.assignRoles(id, roleIds);
        return Result.ok();
    }
}
