package com.erp.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.exception.BusinessException;
import com.erp.purchase.entity.PurOrder;
import com.erp.purchase.entity.PurOrderItem;
import com.erp.purchase.event.OrderAuditedEvent;
import com.erp.purchase.mapper.PurOrderItemMapper;
import com.erp.purchase.mapper.PurOrderMapper;
import com.erp.purchase.service.PurOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurOrderServiceImpl extends ServiceImpl<PurOrderMapper, PurOrder> implements PurOrderService {

    private final PurOrderItemMapper orderItemMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Page<PurOrder> pageOrders(int pageNum, int pageSize, Integer status, Long supplierId) {
        LambdaQueryWrapper<PurOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null, PurOrder::getStatus, status)
                .eq(supplierId != null, PurOrder::getSupplierId, supplierId)
                .orderByDesc(PurOrder::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void createOrder(PurOrder order, List<PurOrderItem> items) {
        order.setOrderNo(generateOrderNo());
        order.setStatus(0);
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (PurOrderItem item : items) {
            totalQty = totalQty.add(item.getQuantity());
            totalAmount = totalAmount.add(item.getAmount());
        }
        order.setTotalQty(totalQty);
        order.setTotalAmount(totalAmount);
        save(order);
        for (PurOrderItem item : items) {
            item.setOrderId(order.getId());
            item.setReceivedQty(BigDecimal.ZERO);
            orderItemMapper.insert(item);
        }
    }

    @Override
    public void auditOrder(Long id) {
        PurOrder order = getById(id);
        if (order == null) throw new BusinessException("订单不存在");
        if (order.getStatus() != 1) throw new BusinessException("只有待审状态的订单才能审核");
        order.setStatus(2);
        order.setAuditTime(LocalDateTime.now());
        updateById(order);
        eventPublisher.publishEvent(new OrderAuditedEvent(order.getId(), order.getOrderNo()));
    }

    @Override
    public void cancelOrder(Long id) {
        PurOrder order = getById(id);
        if (order == null) throw new BusinessException("订单不存在");
        order.setStatus(5);
        updateById(order);
    }

    @Override
    public List<PurOrderItem> getOrderItems(Long orderId) {
        LambdaQueryWrapper<PurOrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurOrderItem::getOrderId, orderId);
        return orderItemMapper.selectList(wrapper);
    }

    private String generateOrderNo() {
        return "PO" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
                String.format("%04d", System.currentTimeMillis() % 10000);
    }
}
