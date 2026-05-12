package com.erp.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.system.entity.SysRole;

import java.util.List;

public interface SysRoleService extends IService<SysRole> {
    List<Long> getRoleMenus(Long roleId);
    void assignMenus(Long roleId, List<Long> menuIds);
}
