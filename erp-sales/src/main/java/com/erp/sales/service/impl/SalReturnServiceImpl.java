package com.erp.sales.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.base.SortHelper;
import com.erp.common.exception.BusinessException;
import com.erp.sales.entity.SalReturn;
import com.erp.sales.entity.SalReturnItem;
import com.erp.sales.event.GoodsDeliveredEvent;
import com.erp.sales.mapper.SalReturnItemMapper;
import com.erp.sales.mapper.SalReturnMapper;
import com.erp.sales.service.SalReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SalReturnServiceImpl extends ServiceImpl<SalReturnMapper, SalReturn> implements SalReturnService {

    private final SalReturnItemMapper returnItemMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Page<SalReturn> pageReturns(int pageNum, int pageSize, Integer status, String sortField, String sortOrder) {
        Map<String, SFunction<SalReturn, ?>> fieldMap = Map.of(
            "returnNo", SalReturn::getReturnNo,
            "createTime", SalReturn::getCreateTime
        );
        LambdaQueryWrapper<SalReturn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null, SalReturn::getStatus, status);
        SortHelper.applySort(wrapper, sortField, sortOrder, SalReturn::getCreateTime, fieldMap);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void createReturn(SalReturn salReturn, List<SalReturnItem> items) {
        salReturn.setReturnNo(generateReturnNo());
        salReturn.setStatus(0);
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (SalReturnItem item : items) {
            totalQty = totalQty.add(item.getQuantity());
            totalAmount = totalAmount.add(item.getAmount());
        }
        salReturn.setTotalQty(totalQty);
        salReturn.setTotalAmount(totalAmount);
        save(salReturn);
        for (SalReturnItem item : items) {
            item.setReturnId(salReturn.getId());
            returnItemMapper.insert(item);
        }
    }

    @Override
    @Transactional
    public void completeReturn(Long id) {
        SalReturn salReturn = getById(id);
        if (salReturn == null) throw new BusinessException("退货单不存在");
        salReturn.setStatus(1);
        updateById(salReturn);
        List<SalReturnItem> items = getReturnItems(id);
        for (SalReturnItem item : items) {
            eventPublisher.publishEvent(new GoodsDeliveredEvent(
                    item.getProductId(), salReturn.getWarehouseId(), item.getQuantity(), salReturn.getReturnNo()));
        }
    }

    @Override
    @Transactional
    public void unauditReturn(Long id) {
        SalReturn salReturn = getById(id);
        if (salReturn == null) throw new BusinessException("退货单不存在");
        if (salReturn.getStatus() != 1) throw new BusinessException("只能反审核已完成状态的退货单");
        salReturn.setStatus(0);
        updateById(salReturn);
    }

    @Override
    public List<SalReturnItem> getReturnItems(Long returnId) {
        LambdaQueryWrapper<SalReturnItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SalReturnItem::getReturnId, returnId);
        return returnItemMapper.selectList(wrapper);
    }

    private String generateReturnNo() {
        return "SR" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
                String.format("%04d", System.currentTimeMillis() % 10000);
    }
}
