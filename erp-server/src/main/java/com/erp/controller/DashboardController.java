package com.erp.controller;

import com.erp.common.response.Result;
import com.erp.inventory.mapper.InvProductMapper;
import com.erp.purchase.mapper.PurSupplierMapper;
import com.erp.purchase.mapper.PurOrderMapper;
import com.erp.sales.mapper.SalCustomerMapper;
import com.erp.sales.mapper.SalOrderMapper;
import com.erp.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final InvProductMapper productMapper;
    private final PurSupplierMapper supplierMapper;
    private final SalCustomerMapper customerMapper;
    private final SysUserMapper userMapper;
    private final PurOrderMapper purOrderMapper;
    private final SalOrderMapper salOrderMapper;

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> map = new HashMap<>();
        map.put("productCount", productMapper.selectCount(null));
        map.put("supplierCount", supplierMapper.selectCount(null));
        map.put("customerCount", customerMapper.selectCount(null));
        map.put("userCount", userMapper.selectCount(null));
        map.put("pendingPurchase", purOrderMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.erp.purchase.entity.PurOrder>()
                        .eq(com.erp.purchase.entity.PurOrder::getStatus, 1)));
        map.put("pendingSales", salOrderMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.erp.sales.entity.SalOrder>()
                        .eq(com.erp.sales.entity.SalOrder::getStatus, 1)));
        return Result.ok(map);
    }
}
