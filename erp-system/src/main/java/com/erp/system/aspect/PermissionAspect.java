package com.erp.system.aspect;

import com.erp.common.annotation.RequirePermission;
import com.erp.common.exception.BusinessException;
import com.erp.system.service.SysMenuService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Aspect
@Component
public class PermissionAspect {

    private final SysMenuService menuService;

    public PermissionAspect(SysMenuService menuService) {
        this.menuService = menuService;
    }

    @Before("@annotation(rp)")
    public void checkPermission(RequirePermission rp) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return;
        HttpServletRequest request = attrs.getRequest();
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) throw new BusinessException(401, "未登录");

        List<String> perms = menuService.getPermsByUserId(userId);
        if (!perms.contains(rp.value())) {
            throw new BusinessException(403, "无操作权限");
        }
    }
}
