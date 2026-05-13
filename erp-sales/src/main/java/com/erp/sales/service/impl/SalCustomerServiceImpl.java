package com.erp.sales.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.exception.BusinessException;
import com.erp.sales.entity.SalCustomer;
import com.erp.sales.mapper.SalCustomerMapper;
import com.erp.sales.service.SalCustomerService;
import org.springframework.stereotype.Service;

@Service
public class SalCustomerServiceImpl extends ServiceImpl<SalCustomerMapper, SalCustomer> implements SalCustomerService {

    @Override
    public void addCustomer(SalCustomer customer) {
        if (lambdaQuery().eq(SalCustomer::getCode, customer.getCode()).count() > 0) {
            throw new BusinessException("客户编码已存在");
        }
        save(customer);
    }

    @Override
    public void updateCustomer(SalCustomer customer) {
        if (lambdaQuery().eq(SalCustomer::getCode, customer.getCode()).ne(SalCustomer::getId, customer.getId()).count() > 0) {
            throw new BusinessException("客户编码已存在");
        }
        updateById(customer);
    }
}
