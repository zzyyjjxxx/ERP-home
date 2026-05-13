package com.erp.sales.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.base.SortHelper;
import com.erp.common.exception.BusinessException;
import com.erp.sales.entity.SalDelivery;
import com.erp.sales.entity.SalDeliveryItem;
import com.erp.sales.event.GoodsDeliveredEvent;
import com.erp.sales.mapper.SalDeliveryItemMapper;
import com.erp.sales.mapper.SalDeliveryMapper;
import com.erp.sales.service.SalDeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SalDeliveryServiceImpl extends ServiceImpl<SalDeliveryMapper, SalDelivery> implements SalDeliveryService {

    private final SalDeliveryItemMapper deliveryItemMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Page<SalDelivery> pageDeliveries(int pageNum, int pageSize, Integer status, String sortField, String sortOrder) {
        Map<String, SFunction<SalDelivery, ?>> fieldMap = Map.of(
            "deliveryNo", SalDelivery::getDeliveryNo,
            "createTime", SalDelivery::getCreateTime
        );
        LambdaQueryWrapper<SalDelivery> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null, SalDelivery::getStatus, status);
        SortHelper.applySort(wrapper, sortField, sortOrder, SalDelivery::getCreateTime, fieldMap);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void createDelivery(SalDelivery delivery, List<SalDeliveryItem> items) {
        delivery.setDeliveryNo(generateDeliveryNo());
        delivery.setStatus(0);
        BigDecimal totalQty = items.stream().map(SalDeliveryItem::getDeliveryQty).reduce(BigDecimal.ZERO, BigDecimal::add);
        delivery.setTotalQty(totalQty);
        save(delivery);
        for (SalDeliveryItem item : items) {
            item.setDeliveryId(delivery.getId());
            deliveryItemMapper.insert(item);
        }
    }

    @Override
    @Transactional
    public void auditDelivery(Long id) {
        SalDelivery delivery = getById(id);
        if (delivery == null) throw new BusinessException("发货单不存在");
        delivery.setStatus(1);
        delivery.setAuditTime(LocalDateTime.now());
        updateById(delivery);
        List<SalDeliveryItem> items = getDeliveryItems(id);
        for (SalDeliveryItem item : items) {
            eventPublisher.publishEvent(new GoodsDeliveredEvent(
                    item.getProductId(), delivery.getWarehouseId(), item.getDeliveryQty(), delivery.getDeliveryNo()));
        }
    }

    @Override
    @Transactional
    public void unauditDelivery(Long id) {
        SalDelivery delivery = getById(id);
        if (delivery == null) throw new BusinessException("发货单不存在");
        if (delivery.getStatus() != 1) throw new BusinessException("只能反审核已完成状态的发货单");
        delivery.setStatus(0);
        delivery.setAuditTime(null);
        delivery.setAuditorId(null);
        updateById(delivery);
    }

    @Override
    public List<SalDeliveryItem> getDeliveryItems(Long deliveryId) {
        LambdaQueryWrapper<SalDeliveryItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SalDeliveryItem::getDeliveryId, deliveryId);
        return deliveryItemMapper.selectList(wrapper);
    }

    private String generateDeliveryNo() {
        return "SD" + LocalDate.now().toString().replace("-", "") + System.nanoTime() % 100000;
    }
}
