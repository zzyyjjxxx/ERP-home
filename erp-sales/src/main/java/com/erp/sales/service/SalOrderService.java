package com.erp.sales.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.sales.entity.SalDelivery;
import com.erp.sales.entity.SalOrder;
import com.erp.sales.entity.SalOrderItem;
import java.util.List;

public interface SalOrderService extends IService<SalOrder> {
    Page<SalOrder> pageOrders(int pageNum, int pageSize, Integer status, Long customerId, String sortField, String sortOrder);
    void createOrder(SalOrder order, List<SalOrderItem> items);
    void auditOrder(Long id);
    void unauditOrder(Long id);
    void cancelOrder(Long id);
    SalDelivery pushDownToDelivery(Long orderId);
    List<SalOrderItem> getOrderItems(Long orderId);
}
