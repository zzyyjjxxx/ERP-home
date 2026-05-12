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
@TableName("pur_order")
public class PurOrder extends BaseEntity {
    private String orderNo;
    private Long supplierId;
    private LocalDate orderDate;
    private LocalDate deliveryDate;
    private Long warehouseId;
    private BigDecimal totalQty;
    private BigDecimal totalAmount;
    private Integer status;
    private Long auditorId;
    private LocalDateTime auditTime;
    private String remark;
}
