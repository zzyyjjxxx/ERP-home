package com.erp.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_payment")
public class FinPayment extends BaseEntity {
    private String paymentNo;
    private String paymentType;
    private String bizType;
    private Long payerId;
    private Long payeeId;
    private BigDecimal amount;
    private String paymentMethod;
    private LocalDate paymentDate;
    private Long voucherId;
    private Integer status;
    private String remark;
}
