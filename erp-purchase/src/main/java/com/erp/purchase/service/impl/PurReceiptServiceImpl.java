package com.erp.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.base.SortHelper;
import com.erp.common.exception.BusinessException;
import com.erp.purchase.entity.PurReceipt;
import com.erp.purchase.entity.PurReceiptItem;
import com.erp.purchase.event.GoodsReceivedEvent;
import com.erp.purchase.mapper.PurReceiptItemMapper;
import com.erp.purchase.mapper.PurReceiptMapper;
import com.erp.purchase.service.PurReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PurReceiptServiceImpl extends ServiceImpl<PurReceiptMapper, PurReceipt> implements PurReceiptService {

    private final PurReceiptItemMapper receiptItemMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Page<PurReceipt> pageReceipts(int pageNum, int pageSize, Integer status, String sortField, String sortOrder) {
        Map<String, SFunction<PurReceipt, ?>> fieldMap = Map.of(
            "receiptNo", PurReceipt::getReceiptNo,
            "createTime", PurReceipt::getCreateTime
        );
        LambdaQueryWrapper<PurReceipt> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null, PurReceipt::getStatus, status);
        SortHelper.applySort(wrapper, sortField, sortOrder, PurReceipt::getCreateTime, fieldMap);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void createReceipt(PurReceipt receipt, List<PurReceiptItem> items) {
        receipt.setReceiptNo(generateReceiptNo());
        receipt.setStatus(0);
        BigDecimal totalQty = BigDecimal.ZERO;
        for (PurReceiptItem item : items) {
            totalQty = totalQty.add(item.getReceiptQty());
        }
        receipt.setTotalQty(totalQty);
        save(receipt);
        for (PurReceiptItem item : items) {
            item.setReceiptId(receipt.getId());
            receiptItemMapper.insert(item);
        }
    }

    @Override
    @Transactional
    public void auditReceipt(Long id) {
        PurReceipt receipt = getById(id);
        if (receipt == null) throw new BusinessException("收货单不存在");
        receipt.setStatus(1);
        receipt.setAuditTime(LocalDateTime.now());
        updateById(receipt);
        List<PurReceiptItem> items = getReceiptItems(id);
        for (PurReceiptItem item : items) {
            eventPublisher.publishEvent(new GoodsReceivedEvent(
                    item.getProductId(), item.getWarehouseId(), item.getReceiptQty(), receipt.getReceiptNo()));
        }
    }

    @Override
    @Transactional
    public void unauditReceipt(Long id) {
        PurReceipt receipt = getById(id);
        if (receipt == null) throw new BusinessException("收货单不存在");
        if (receipt.getStatus() != 1) throw new BusinessException("只能反审核已完成状态的收货单");
        receipt.setStatus(0);
        receipt.setAuditTime(null);
        receipt.setAuditorId(null);
        updateById(receipt);
    }

    @Override
    public List<PurReceiptItem> getReceiptItems(Long receiptId) {
        LambdaQueryWrapper<PurReceiptItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurReceiptItem::getReceiptId, receiptId);
        return receiptItemMapper.selectList(wrapper);
    }

    private String generateReceiptNo() {
        return "RC" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
                String.format("%04d", System.currentTimeMillis() % 10000);
    }
}
