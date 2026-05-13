package com.erp.production.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.base.SortHelper;
import com.erp.common.exception.BusinessException;
import com.erp.production.entity.PrdBomItem;
import com.erp.production.entity.PrdWorkOrder;
import com.erp.production.entity.PrdWorkOrderItem;
import com.erp.production.mapper.PrdBomItemMapper;
import com.erp.production.mapper.PrdWorkOrderItemMapper;
import com.erp.production.mapper.PrdWorkOrderMapper;
import com.erp.production.service.PrdWorkOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PrdWorkOrderServiceImpl extends ServiceImpl<PrdWorkOrderMapper, PrdWorkOrder> implements PrdWorkOrderService {

    private final PrdWorkOrderItemMapper workOrderItemMapper;
    private final PrdBomItemMapper bomItemMapper;

    @Override
    public Page<PrdWorkOrder> pageOrders(int pageNum, int pageSize, Integer status, String sortField, String sortOrder) {
        Map<String, SFunction<PrdWorkOrder, ?>> fieldMap = Map.of(
            "orderNo", PrdWorkOrder::getOrderNo,
            "createTime", PrdWorkOrder::getCreateTime
        );
        LambdaQueryWrapper<PrdWorkOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null, PrdWorkOrder::getStatus, status);
        SortHelper.applySort(wrapper, sortField, sortOrder, PrdWorkOrder::getCreateTime, fieldMap);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void createWorkOrder(PrdWorkOrder order, List<PrdWorkOrderItem> items) {
        order.setOrderNo(generateOrderNo());
        if (order.getStatus() == null) {
            order.setStatus(0);
        }
        if (order.getActualQty() == null) {
            order.setActualQty(BigDecimal.ZERO);
        }
        if (order.getPriority() == null) {
            order.setPriority(0);
        }
        save(order);

        // If no items provided but BOM is specified, explode BOM to generate items
        if ((items == null || items.isEmpty()) && order.getBomId() != null) {
            items = explodeBomItems(order.getBomId(), order.getPlanQty());
        }

        if (items != null && !items.isEmpty()) {
            for (PrdWorkOrderItem item : items) {
                item.setWorkOrderId(order.getId());
                if (item.getIssuedQty() == null) {
                    item.setIssuedQty(BigDecimal.ZERO);
                }
                if (item.getConsumedQty() == null) {
                    item.setConsumedQty(BigDecimal.ZERO);
                }
                workOrderItemMapper.insert(item);
            }
        }
    }

    @Override
    @Transactional
    public void startProduction(Long id) {
        PrdWorkOrder wo = getById(id);
        if (wo == null) {
            throw new BusinessException("工单不存在");
        }
        if (wo.getStatus() != 0) {
            throw new BusinessException("只有待领料状态的工单才能开始生产");
        }
        wo.setStatus(1);
        wo.setStartDate(LocalDate.now());
        updateById(wo);

        // Issue materials: set issuedQty = requireQty for all items
        List<PrdWorkOrderItem> items = getWorkOrderItems(id);
        for (PrdWorkOrderItem item : items) {
            item.setIssuedQty(item.getRequireQty());
            workOrderItemMapper.updateById(item);
        }
    }

    @Override
    @Transactional
    public void completeProduction(Long id, BigDecimal actualQty) {
        PrdWorkOrder wo = getById(id);
        if (wo == null) {
            throw new BusinessException("工单不存在");
        }
        if (wo.getStatus() != 1) {
            throw new BusinessException("只有生产中的工单才能完成");
        }
        wo.setStatus(2);
        wo.setActualQty(actualQty != null ? actualQty : wo.getPlanQty());
        wo.setEndDate(LocalDate.now());
        updateById(wo);
    }

    @Override
    @Transactional
    public void closeOrder(Long id) {
        PrdWorkOrder wo = getById(id);
        if (wo == null) {
            throw new BusinessException("工单不存在");
        }
        if (wo.getStatus() >= 2) {
            throw new BusinessException("已完成或已关闭的工单无需重复关闭");
        }
        wo.setStatus(3);
        updateById(wo);
    }

    @Override
    public List<PrdWorkOrderItem> getWorkOrderItems(Long workOrderId) {
        LambdaQueryWrapper<PrdWorkOrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PrdWorkOrderItem::getWorkOrderId, workOrderId);
        return workOrderItemMapper.selectList(wrapper);
    }

    /**
     * BOM explosion: expand BOM items into work order items,
     * scaling quantities by the work order plan quantity (relative to standard single-unit BOM)
     */
    private List<PrdWorkOrderItem> explodeBomItems(Long bomId, BigDecimal planQty) {
        LambdaQueryWrapper<PrdBomItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PrdBomItem::getBomId, bomId)
                .orderByAsc(PrdBomItem::getSeq);
        List<PrdBomItem> bomItems = bomItemMapper.selectList(wrapper);

        List<PrdWorkOrderItem> woItems = new ArrayList<>();
        for (PrdBomItem bomItem : bomItems) {
            PrdWorkOrderItem woItem = new PrdWorkOrderItem();
            woItem.setMaterialId(bomItem.getMaterialId());
            // Scale BOM standard quantity by the planned production quantity
            woItem.setRequireQty(bomItem.getQuantity().multiply(planQty).setScale(4, RoundingMode.HALF_UP));
            woItem.setIssuedQty(BigDecimal.ZERO);
            woItem.setConsumedQty(BigDecimal.ZERO);
            woItems.add(woItem);
        }
        return woItems;
    }

    private String generateOrderNo() {
        return "WO" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + String.format("%04d", System.nanoTime() % 10000);
    }
}
