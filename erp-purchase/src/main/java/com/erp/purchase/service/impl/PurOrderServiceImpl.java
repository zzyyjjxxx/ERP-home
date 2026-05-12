package com.erp.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.exception.BusinessException;
import com.erp.purchase.entity.PurOrder;
import com.erp.purchase.entity.PurOrderItem;
import com.erp.purchase.entity.PurReceipt;
import com.erp.purchase.entity.PurReceiptItem;
import com.erp.purchase.event.OrderAuditedEvent;
import com.erp.purchase.mapper.PurOrderItemMapper;
import com.erp.purchase.mapper.PurOrderMapper;
import com.erp.purchase.mapper.PurReceiptItemMapper;
import com.erp.purchase.mapper.PurReceiptMapper;
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
    private final PurReceiptMapper receiptMapper;
    private final PurReceiptItemMapper receiptItemMapper;
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
        order.setOrderDate(LocalDate.now());
        order.setStatus(0);
        BigDecimal totalQty = items.stream().map(PurOrderItem::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalAmount = items.stream().map(PurOrderItem::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
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
        if (order.getStatus() != 1) throw new BusinessException("只能审核待审状态的订单");
        order.setStatus(2);
        order.setAuditTime(LocalDateTime.now());
        updateById(order);
        eventPublisher.publishEvent(new OrderAuditedEvent(order.getId(), order.getOrderNo()));
    }

    @Override
    @Transactional
    public void unauditOrder(Long id) {
        PurOrder order = getById(id);
        if (order == null) throw new BusinessException("订单不存在");
        if (order.getStatus() != 2) throw new BusinessException("只能反审核已审状态的订单");
        order.setStatus(1);
        order.setAuditTime(null);
        order.setAuditorId(null);
        updateById(order);
    }

    @Override
    @Transactional
    public PurReceipt pushDownToReceipt(Long orderId) {
        PurOrder order = getById(orderId);
        if (order == null) throw new BusinessException("订单不存在");
        if (order.getStatus() != 2) throw new BusinessException("只能下推已审核的订单");
        List<PurOrderItem> orderItems = getOrderItems(orderId);
        if (orderItems.isEmpty()) throw new BusinessException("订单无明细，不能下推");

        PurReceipt receipt = new PurReceipt();
        receipt.setReceiptNo("RC" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
                String.format("%04d", System.currentTimeMillis() % 10000));
        receipt.setOrderId(orderId);
        receipt.setSupplierId(order.getSupplierId());
        receipt.setWarehouseId(order.getWarehouseId());
        receipt.setReceiptDate(LocalDate.now());
        receipt.setStatus(0);
        BigDecimal totalQty = BigDecimal.ZERO;
        for (PurOrderItem item : orderItems) {
            totalQty = totalQty.add(item.getQuantity());
        }
        receipt.setTotalQty(totalQty);
        receiptMapper.insert(receipt);

        for (PurOrderItem item : orderItems) {
            PurReceiptItem receiptItem = new PurReceiptItem();
            receiptItem.setReceiptId(receipt.getId());
            receiptItem.setProductId(item.getProductId());
            receiptItem.setOrderQty(item.getQuantity());
            receiptItem.setReceiptQty(item.getQuantity().subtract(
                    item.getReceivedQty() != null ? item.getReceivedQty() : BigDecimal.ZERO));
            receiptItem.setWarehouseId(order.getWarehouseId());
            receiptItem.setUnitPrice(item.getUnitPrice());
            receiptItem.setAmount(item.getAmount());
            receiptItemMapper.insert(receiptItem);
        }

        order.setStatus(3);
        updateById(order);
        return receipt;
    }

    @Override
    public void cancelOrder(Long id) {
        PurOrder order = getById(id);
        if (order == null) throw new BusinessException("订单不存在");
        if (order.getStatus() >= 4) throw new BusinessException("已完成或已取消的订单不能取消");
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
        return "PO" + LocalDate.now().toString().replace("-", "") + System.nanoTime() % 100000;
    }
}
