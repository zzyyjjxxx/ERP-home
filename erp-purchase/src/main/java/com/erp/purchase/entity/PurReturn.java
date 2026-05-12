package com.erp.purchase.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pur_return")
public class PurReturn extends BaseEntity {
    private String returnNo;
    private Long supplierId;
    private Long orderId;
    private Long receiptId;
    private Long warehouseId;
    private LocalDate returnDate;
    private BigDecimal totalQty;
    private BigDecimal totalAmount;
    private String reason;
    private Integer status;
}
