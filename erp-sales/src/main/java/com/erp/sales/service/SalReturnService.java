package com.erp.sales.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.sales.entity.SalReturn;
import com.erp.sales.entity.SalReturnItem;
import java.util.List;

public interface SalReturnService extends IService<SalReturn> {
    Page<SalReturn> pageReturns(int pageNum, int pageSize, Integer status, String sortField, String sortOrder);
    void createReturn(SalReturn salReturn, List<SalReturnItem> items);
    void completeReturn(Long id);
    void unauditReturn(Long id);
    List<SalReturnItem> getReturnItems(Long returnId);
}
