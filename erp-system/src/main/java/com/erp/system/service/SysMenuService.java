package com.erp.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.system.entity.SysMenu;

import java.util.List;

public interface SysMenuService extends IService<SysMenu> {
    List<SysMenu> getMenuTree();
    List<SysMenu> getMenusByUserId(Long userId);
    List<String> getPermsByUserId(Long userId);
}
