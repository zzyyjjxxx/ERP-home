package com.erp.production.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.production.entity.PrdWorkOrder;
import com.erp.production.entity.PrdWorkOrderItem;

import java.math.BigDecimal;
import java.util.List;

public interface PrdWorkOrderService extends IService<PrdWorkOrder> {

    /**
     * Page query work orders
     */
    Page<PrdWorkOrder> pageOrders(int pageNum, int pageSize, Integer status);

    /**
     * Create work order with items. If bomId is set and items are empty, auto-generate from BOM.
     */
    void createWorkOrder(PrdWorkOrder order, List<PrdWorkOrderItem> items);

    /**
     * Start production: status 0→1, issue materials (update issuedQty on items)
     */
    void startProduction(Long id);

    /**
     * Complete production: status 1→2, write actualQty
     */
    void completeProduction(Long id, BigDecimal actualQty);

    /**
     * Close work order: status -> 3
     */
    void closeOrder(Long id);

    /**
     * Get work order items
     */
    List<PrdWorkOrderItem> getWorkOrderItems(Long workOrderId);
}
