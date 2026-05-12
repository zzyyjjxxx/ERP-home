package com.erp.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_payable")
public class FinPayable extends BaseEntity {
    private Long supplierId;
    private String bizType;
    private String bizNo;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private BigDecimal balance;
    private LocalDate dueDate;
    private Integer status;
}
