package com.erp.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.exception.BusinessException;
import com.erp.purchase.entity.PurReturn;
import com.erp.purchase.entity.PurReturnItem;
import com.erp.purchase.event.GoodsReturnedEvent;
import com.erp.purchase.mapper.PurReturnItemMapper;
import com.erp.purchase.mapper.PurReturnMapper;
import com.erp.purchase.service.PurReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurReturnServiceImpl extends ServiceImpl<PurReturnMapper, PurReturn> implements PurReturnService {

    private final PurReturnItemMapper returnItemMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Page<PurReturn> pageReturns(int pageNum, int pageSize, Integer status) {
        LambdaQueryWrapper<PurReturn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null, PurReturn::getStatus, status)
                .orderByDesc(PurReturn::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void createReturn(PurReturn purReturn, List<PurReturnItem> items) {
        purReturn.setReturnNo(generateReturnNo());
        purReturn.setStatus(0);
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (PurReturnItem item : items) {
            totalQty = totalQty.add(item.getQuantity());
            totalAmount = totalAmount.add(item.getAmount());
        }
        purReturn.setTotalQty(totalQty);
        purReturn.setTotalAmount(totalAmount);
        save(purReturn);
        for (PurReturnItem item : items) {
            item.setReturnId(purReturn.getId());
            returnItemMapper.insert(item);
        }
    }

    @Override
    @Transactional
    public void completeReturn(Long id) {
        PurReturn purReturn = getById(id);
        if (purReturn == null) throw new BusinessException("退货单不存在");
        purReturn.setStatus(1);
        updateById(purReturn);
        List<PurReturnItem> items = getReturnItems(id);
        for (PurReturnItem item : items) {
            eventPublisher.publishEvent(new GoodsReturnedEvent(
                    item.getProductId(), purReturn.getWarehouseId(), item.getQuantity(), purReturn.getReturnNo()));
        }
    }

    @Override
    public List<PurReturnItem> getReturnItems(Long returnId) {
        LambdaQueryWrapper<PurReturnItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurReturnItem::getReturnId, returnId);
        return returnItemMapper.selectList(wrapper);
    }

    private String generateReturnNo() {
        return "PR" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
                String.format("%04d", System.currentTimeMillis() % 10000);
    }
}
