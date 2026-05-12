package com.erp.system.controller;

import com.erp.common.response.Result;
import com.erp.system.entity.SysMenu;
import com.erp.system.service.SysMenuService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/menu")
public class SysMenuController {

    private final SysMenuService menuService;

    public SysMenuController(SysMenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/tree")
    public Result<List<SysMenu>> tree() {
        return Result.ok(menuService.getMenuTree());
    }

    @GetMapping("/user/{userId}")
    public Result<List<SysMenu>> userMenus(@PathVariable Long userId) {
        return Result.ok(menuService.getMenusByUserId(userId));
    }

    @PostMapping
    public Result<?> add(@RequestBody SysMenu menu) {
        menuService.save(menu);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody SysMenu menu) {
        menu.setId(id);
        menuService.updateById(menu);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        menuService.removeById(id);
        return Result.ok();
    }
}
