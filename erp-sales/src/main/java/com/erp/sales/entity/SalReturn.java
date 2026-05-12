package com.erp.sales.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sal_return")
public class SalReturn extends BaseEntity {
    private String returnNo;
    private Long customerId;
    private Long orderId;
    private Long deliveryId;
    private Long warehouseId;
    private LocalDate returnDate;
    private BigDecimal totalQty;
    private BigDecimal totalAmount;
    private String reason;
    private Integer status;
}
