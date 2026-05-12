package com.erp.finance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.finance.entity.FinVoucher;
import com.erp.finance.entity.FinVoucherItem;
import java.util.List;

public interface FinVoucherService extends IService<FinVoucher> {
    Page<FinVoucher> pageVouchers(int pageNum, int pageSize, Integer status);
    void createVoucher(FinVoucher voucher, List<FinVoucherItem> items);
    void auditVoucher(Long id);
    List<FinVoucherItem> getVoucherItems(Long voucherId);
}
