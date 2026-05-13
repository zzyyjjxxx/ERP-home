package com.erp.sales.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.sales.entity.SalCustomer;

public interface SalCustomerService extends IService<SalCustomer> {
    void addCustomer(SalCustomer customer);
    void updateCustomer(SalCustomer customer);
}
