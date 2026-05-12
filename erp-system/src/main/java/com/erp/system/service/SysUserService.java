package com.erp.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.system.entity.SysUser;

import java.util.List;

public interface SysUserService extends IService<SysUser> {
    Page<SysUser> pageUsers(int pageNum, int pageSize, String keyword, Long deptId, Integer status);
    SysUser getByUsername(String username);
    void addUser(SysUser user);
    void updateUser(SysUser user);
    void deleteUsers(List<Long> ids);
    void assignRoles(Long userId, List<Long> roleIds);
}
