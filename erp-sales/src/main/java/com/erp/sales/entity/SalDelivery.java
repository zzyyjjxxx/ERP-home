package com.erp.sales.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sal_delivery")
public class SalDelivery extends BaseEntity {
    private String deliveryNo;
    private Long orderId;
    private Long customerId;
    private Long warehouseId;
    private LocalDate deliveryDate;
    private BigDecimal totalQty;
    private Integer status;
    private Long auditorId;
    private LocalDateTime auditTime;
    private String remark;
}
