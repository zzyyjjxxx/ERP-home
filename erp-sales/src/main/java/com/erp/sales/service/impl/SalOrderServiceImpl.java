package com.erp.sales.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.exception.BusinessException;
import com.erp.sales.entity.SalOrder;
import com.erp.sales.entity.SalOrderItem;
import com.erp.sales.mapper.SalOrderItemMapper;
import com.erp.sales.mapper.SalOrderMapper;
import com.erp.sales.service.SalOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalOrderServiceImpl extends ServiceImpl<SalOrderMapper, SalOrder> implements SalOrderService {

    private final SalOrderItemMapper orderItemMapper;

    @Override
    public Page<SalOrder> pageOrders(int pageNum, int pageSize, Integer status, Long customerId) {
        LambdaQueryWrapper<SalOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null, SalOrder::getStatus, status)
                .eq(customerId != null, SalOrder::getCustomerId, customerId)
                .orderByDesc(SalOrder::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void createOrder(SalOrder order, List<SalOrderItem> items) {
        order.setOrderNo(generateOrderNo());
        order.setOrderDate(LocalDate.now());
        order.setStatus(0);
        BigDecimal totalQty = items.stream().map(SalOrderItem::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalAmount = items.stream().map(SalOrderItem::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalQty(totalQty);
        order.setTotalAmount(totalAmount);
        save(order);
        for (SalOrderItem item : items) {
            item.setOrderId(order.getId());
            item.setDeliveredQty(BigDecimal.ZERO);
            orderItemMapper.insert(item);
        }
    }

    @Override
    public void auditOrder(Long id) {
        SalOrder order = getById(id);
        if (order == null) throw new BusinessException("订单不存在");
        if (order.getStatus() != 1) throw new BusinessException("只能审核待审状态的订单");
        order.setStatus(2);
        order.setAuditTime(LocalDateTime.now());
        updateById(order);
    }

    @Override
    public void cancelOrder(Long id) {
        SalOrder order = getById(id);
        if (order == null) throw new BusinessException("订单不存在");
        if (order.getStatus() >= 4) throw new BusinessException("已完成或已取消的订单不能取消");
        order.setStatus(5);
        updateById(order);
    }

    @Override
    public List<SalOrderItem> getOrderItems(Long orderId) {
        LambdaQueryWrapper<SalOrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SalOrderItem::getOrderId, orderId);
        return orderItemMapper.selectList(wrapper);
    }

    private String generateOrderNo() {
        return "SO" + LocalDate.now().toString().replace("-", "") + System.nanoTime() % 100000;
    }
}
