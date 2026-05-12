package com.erp.purchase.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.purchase.entity.PurReturn;
import com.erp.purchase.entity.PurReturnItem;
import java.util.List;

public interface PurReturnService extends IService<PurReturn> {
    Page<PurReturn> pageReturns(int pageNum, int pageSize, Integer status);
    void createReturn(PurReturn purReturn, List<PurReturnItem> items);
    void completeReturn(Long id);
    void unauditReturn(Long id);
    List<PurReturnItem> getReturnItems(Long returnId);
}
