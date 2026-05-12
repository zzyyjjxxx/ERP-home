package com.erp.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.exception.BusinessException;
import com.erp.inventory.entity.InvStock;
import com.erp.inventory.entity.InvStockFlow;
import com.erp.inventory.mapper.InvStockFlowMapper;
import com.erp.inventory.mapper.InvStockMapper;
import com.erp.inventory.service.InvStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class InvStockServiceImpl extends ServiceImpl<InvStockMapper, InvStock> implements InvStockService {

    private final InvStockFlowMapper stockFlowMapper;

    @Override
    public Page<InvStock> pageStock(int pageNum, int pageSize, Long warehouseId, Long categoryId) {
        LambdaQueryWrapper<InvStock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(warehouseId != null, InvStock::getWarehouseId, warehouseId)
                .orderByDesc(InvStock::getQuantity);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void increaseStock(Long productId, Long warehouseId, BigDecimal qty, String bizType, String bizNo) {
        InvStock stock = getOrCreateStock(productId, warehouseId);
        BigDecimal before = stock.getQuantity();
        stock.setQuantity(before.add(qty));
        updateById(stock);
        saveFlow(productId, warehouseId, bizType, bizNo, before, qty, stock.getQuantity());
    }

    @Override
    @Transactional
    public void decreaseStock(Long productId, Long warehouseId, BigDecimal qty, String bizType, String bizNo) {
        InvStock stock = getOrCreateStock(productId, warehouseId);
        BigDecimal available = stock.getQuantity().subtract(stock.getLockedQty() != null ? stock.getLockedQty() : BigDecimal.ZERO);
        if (available.compareTo(qty) < 0) {
            throw new BusinessException("库存不足，当前可用库存：" + available);
        }
        BigDecimal before = stock.getQuantity();
        stock.setQuantity(before.subtract(qty));
        updateById(stock);
        saveFlow(productId, warehouseId, bizType, bizNo, before, qty.negate(), stock.getQuantity());
    }

    @Override
    public InvStock getOrCreateStock(Long productId, Long warehouseId) {
        LambdaQueryWrapper<InvStock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InvStock::getProductId, productId).eq(InvStock::getWarehouseId, warehouseId);
        InvStock stock = getOne(wrapper);
        if (stock == null) {
            stock = new InvStock();
            stock.setProductId(productId);
            stock.setWarehouseId(warehouseId);
            stock.setQuantity(BigDecimal.ZERO);
            stock.setLockedQty(BigDecimal.ZERO);
            save(stock);
        }
        return stock;
    }

    @Override
    @Transactional
    public void lockStock(Long productId, Long warehouseId, BigDecimal qty) {
        InvStock stock = getOrCreateStock(productId, warehouseId);
        BigDecimal available = stock.getQuantity().subtract(stock.getLockedQty() != null ? stock.getLockedQty() : BigDecimal.ZERO);
        if (available.compareTo(qty) < 0) {
            throw new BusinessException("可锁定库存不足");
        }
        stock.setLockedQty(stock.getLockedQty().add(qty));
        updateById(stock);
    }

    @Override
    @Transactional
    public void unlockStock(Long productId, Long warehouseId, BigDecimal qty) {
        InvStock stock = getOrCreateStock(productId, warehouseId);
        stock.setLockedQty(stock.getLockedQty().subtract(qty));
        updateById(stock);
    }

    private void saveFlow(Long productId, Long warehouseId, String bizType, String bizNo,
                          BigDecimal beforeQty, BigDecimal changeQty, BigDecimal afterQty) {
        InvStockFlow flow = new InvStockFlow();
        flow.setProductId(productId);
        flow.setWarehouseId(warehouseId);
        flow.setBizType(bizType);
        flow.setBizNo(bizNo);
        flow.setBeforeQty(beforeQty);
        flow.setChangeQty(changeQty);
        flow.setAfterQty(afterQty);
        stockFlowMapper.insert(flow);
    }
}
