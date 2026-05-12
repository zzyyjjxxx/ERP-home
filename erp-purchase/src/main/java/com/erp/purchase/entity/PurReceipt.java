package com.erp.purchase.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pur_receipt")
public class PurReceipt extends BaseEntity {
    private String receiptNo;
    private Long orderId;
    private Long supplierId;
    private Long warehouseId;
    private LocalDate receiptDate;
    private BigDecimal totalQty;
    private Integer status;
    private Long auditorId;
    private LocalDateTime auditTime;
    private String remark;
}
