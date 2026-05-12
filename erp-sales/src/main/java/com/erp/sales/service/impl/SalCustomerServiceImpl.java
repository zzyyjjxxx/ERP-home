package com.erp.sales.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.sales.entity.SalCustomer;
import com.erp.sales.mapper.SalCustomerMapper;
import com.erp.sales.service.SalCustomerService;
import org.springframework.stereotype.Service;

@Service
public class SalCustomerServiceImpl extends ServiceImpl<SalCustomerMapper, SalCustomer> implements SalCustomerService {
}
