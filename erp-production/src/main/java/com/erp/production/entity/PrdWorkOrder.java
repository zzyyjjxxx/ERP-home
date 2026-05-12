package com.erp.production.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("prd_work_order")
public class PrdWorkOrder extends BaseEntity {
    private String orderNo;
    private Long productId;
    private Long bomId;
    private BigDecimal planQty;
    private BigDecimal actualQty;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer priority;
    private Integer status;
    private Long assigneeId;
    private Long auditId;
    private String remark;
}
