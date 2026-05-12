package com.erp.sales.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.sales.entity.SalDelivery;
import com.erp.sales.entity.SalDeliveryItem;
import java.util.List;

public interface SalDeliveryService extends IService<SalDelivery> {
    Page<SalDelivery> pageDeliveries(int pageNum, int pageSize, Integer status);
    void createDelivery(SalDelivery delivery, List<SalDeliveryItem> items);
    void auditDelivery(Long id);
    List<SalDeliveryItem> getDeliveryItems(Long deliveryId);
}
