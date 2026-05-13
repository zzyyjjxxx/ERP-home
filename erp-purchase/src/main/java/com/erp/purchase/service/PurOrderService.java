package com.erp.purchase.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.purchase.entity.PurOrder;
import com.erp.purchase.entity.PurOrderItem;
import com.erp.purchase.entity.PurReceipt;
import java.util.List;

public interface PurOrderService extends IService<PurOrder> {
    Page<PurOrder> pageOrders(int pageNum, int pageSize, Integer status, Long supplierId, String sortField, String sortOrder);
    void createOrder(PurOrder order, List<PurOrderItem> items);
    void auditOrder(Long id);
    void unauditOrder(Long id);
    void cancelOrder(Long id);
    PurReceipt pushDownToReceipt(Long orderId);
    List<PurOrderItem> getOrderItems(Long orderId);
}
