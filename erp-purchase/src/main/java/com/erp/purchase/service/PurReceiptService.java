package com.erp.purchase.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.purchase.entity.PurReceipt;
import com.erp.purchase.entity.PurReceiptItem;
import java.util.List;

public interface PurReceiptService extends IService<PurReceipt> {
    Page<PurReceipt> pageReceipts(int pageNum, int pageSize, Integer status, String sortField, String sortOrder);
    void createReceipt(PurReceipt receipt, List<PurReceiptItem> items);
    void auditReceipt(Long id);
    void unauditReceipt(Long id);
    List<PurReceiptItem> getReceiptItems(Long receiptId);
}
