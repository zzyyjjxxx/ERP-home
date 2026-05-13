package com.erp.sales.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.base.SortHelper;
import com.erp.common.exception.BusinessException;
import com.erp.sales.entity.SalDelivery;
import com.erp.sales.entity.SalDeliveryItem;
import com.erp.sales.entity.SalOrder;
import com.erp.sales.entity.SalOrderItem;
import com.erp.sales.mapper.SalDeliveryItemMapper;
import com.erp.sales.mapper.SalDeliveryMapper;
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
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SalOrderServiceImpl extends ServiceImpl<SalOrderMapper, SalOrder> implements SalOrderService {

    private final SalOrderItemMapper orderItemMapper;
    private final SalDeliveryMapper deliveryMapper;
    private final SalDeliveryItemMapper deliveryItemMapper;

    @Override
    public Page<SalOrder> pageOrders(int pageNum, int pageSize, Integer status, Long customerId, String sortField, String sortOrder) {
        Map<String, SFunction<SalOrder, ?>> fieldMap = Map.of(
            "orderNo", SalOrder::getOrderNo,
            "createTime", SalOrder::getCreateTime
        );
        LambdaQueryWrapper<SalOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null, SalOrder::getStatus, status)
                .eq(customerId != null, SalOrder::getCustomerId, customerId);
        SortHelper.applySort(wrapper, sortField, sortOrder, SalOrder::getCreateTime, fieldMap);
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
    @Transactional
    public void unauditOrder(Long id) {
        SalOrder order = getById(id);
        if (order == null) throw new BusinessException("订单不存在");
        if (order.getStatus() != 2) throw new BusinessException("只能反审核已审状态的订单");
        order.setStatus(1);
        order.setAuditTime(null);
        order.setAuditorId(null);
        updateById(order);
    }

    @Override
    @Transactional
    public SalDelivery pushDownToDelivery(Long orderId) {
        SalOrder order = getById(orderId);
        if (order == null) throw new BusinessException("订单不存在");
        if (order.getStatus() != 2) throw new BusinessException("只能下推已审核的订单");
        List<SalOrderItem> orderItems = getOrderItems(orderId);
        if (orderItems.isEmpty()) throw new BusinessException("订单无明细，不能下推");

        SalDelivery delivery = new SalDelivery();
        delivery.setDeliveryNo("SD" + LocalDate.now().toString().replace("-", "") + System.nanoTime() % 100000);
        delivery.setOrderId(orderId);
        delivery.setCustomerId(order.getCustomerId());
        delivery.setWarehouseId(order.getWarehouseId());
        delivery.setDeliveryDate(LocalDate.now());
        delivery.setStatus(0);
        BigDecimal totalQty = BigDecimal.ZERO;
        for (SalOrderItem item : orderItems) {
            totalQty = totalQty.add(item.getQuantity());
        }
        delivery.setTotalQty(totalQty);
        deliveryMapper.insert(delivery);

        for (SalOrderItem item : orderItems) {
            SalDeliveryItem deliveryItem = new SalDeliveryItem();
            deliveryItem.setDeliveryId(delivery.getId());
            deliveryItem.setProductId(item.getProductId());
            deliveryItem.setOrderQty(item.getQuantity());
            deliveryItem.setDeliveryQty(item.getQuantity().subtract(
                    item.getDeliveredQty() != null ? item.getDeliveredQty() : BigDecimal.ZERO));
            deliveryItem.setWarehouseId(order.getWarehouseId());
            deliveryItem.setUnitPrice(item.getUnitPrice());
            deliveryItem.setAmount(item.getAmount());
            deliveryItemMapper.insert(deliveryItem);
        }

        order.setStatus(3);
        updateById(order);
        return delivery;
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
