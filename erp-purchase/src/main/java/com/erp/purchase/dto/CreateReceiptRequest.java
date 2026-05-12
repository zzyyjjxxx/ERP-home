package com.erp.purchase.dto;

import com.erp.purchase.entity.PurReceipt;
import com.erp.purchase.entity.PurReceiptItem;
import lombok.Data;
import java.util.List;

@Data
public class CreateReceiptRequest {
    private PurReceipt receipt;
    private List<PurReceiptItem> items;
}
