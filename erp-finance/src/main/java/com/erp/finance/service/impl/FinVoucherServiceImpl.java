package com.erp.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.exception.BusinessException;
import com.erp.finance.entity.FinVoucher;
import com.erp.finance.entity.FinVoucherItem;
import com.erp.finance.mapper.FinVoucherItemMapper;
import com.erp.finance.mapper.FinVoucherMapper;
import com.erp.finance.service.FinVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinVoucherServiceImpl extends ServiceImpl<FinVoucherMapper, FinVoucher> implements FinVoucherService {

    private final FinVoucherItemMapper voucherItemMapper;

    @Override
    public Page<FinVoucher> pageVouchers(int pageNum, int pageSize, Integer status) {
        LambdaQueryWrapper<FinVoucher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null, FinVoucher::getStatus, status)
                .orderByDesc(FinVoucher::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void createVoucher(FinVoucher voucher, List<FinVoucherItem> items) {
        voucher.setVoucherNo(generateVoucherNo());
        voucher.setVoucherDate(LocalDate.now());
        voucher.setStatus(0);

        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;

        for (FinVoucherItem item : items) {
            totalDebit = totalDebit.add(item.getDebitAmount() != null ? item.getDebitAmount() : BigDecimal.ZERO);
            totalCredit = totalCredit.add(item.getCreditAmount() != null ? item.getCreditAmount() : BigDecimal.ZERO);
        }

        if (totalDebit.compareTo(totalCredit) != 0) {
            throw new BusinessException("借贷不平衡");
        }

        voucher.setTotalDebit(totalDebit);
        voucher.setTotalCredit(totalCredit);
        save(voucher);

        int sort = 0;
        for (FinVoucherItem item : items) {
            item.setVoucherId(voucher.getId());
            item.setSort(sort++);
            voucherItemMapper.insert(item);
        }
    }

    @Override
    @Transactional
    public void auditVoucher(Long id) {
        FinVoucher voucher = getById(id);
        if (voucher == null) throw new BusinessException("凭证不存在");
        if (voucher.getStatus() != 0) throw new BusinessException("只能审核草稿状态的凭证");
        voucher.setStatus(1);
        voucher.setAuditTime(LocalDateTime.now());
        updateById(voucher);
    }

    @Override
    public List<FinVoucherItem> getVoucherItems(Long voucherId) {
        LambdaQueryWrapper<FinVoucherItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinVoucherItem::getVoucherId, voucherId)
                .orderByAsc(FinVoucherItem::getSort);
        return voucherItemMapper.selectList(wrapper);
    }

    private String generateVoucherNo() {
        return "PZ" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
                String.format("%04d", System.currentTimeMillis() % 10000);
    }
}
