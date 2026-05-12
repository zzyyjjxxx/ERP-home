package com.erp.purchase.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("pur_receipt_item")
public class PurReceiptItem {
    private Long id;
    private Long receiptId;
    private Long productId;
    private BigDecimal orderQty;
    private BigDecimal receiptQty;
    private Long warehouseId;
    private BigDecimal unitPrice;
    private BigDecimal amount;
}
