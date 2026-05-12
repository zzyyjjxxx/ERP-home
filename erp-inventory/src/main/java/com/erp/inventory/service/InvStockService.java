package com.erp.inventory.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.inventory.entity.InvStock;
import java.math.BigDecimal;

public interface InvStockService extends IService<InvStock> {
    Page<InvStock> pageStock(int pageNum, int pageSize, Long warehouseId, Long categoryId);
    void increaseStock(Long productId, Long warehouseId, BigDecimal qty, String bizType, String bizNo);
    void decreaseStock(Long productId, Long warehouseId, BigDecimal qty, String bizType, String bizNo);
    void lockStock(Long productId, Long warehouseId, BigDecimal qty);
    void unlockStock(Long productId, Long warehouseId, BigDecimal qty);
}
