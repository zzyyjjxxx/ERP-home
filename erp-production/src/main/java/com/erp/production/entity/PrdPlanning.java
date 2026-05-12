package com.erp.production.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("prd_planning")
public class PrdPlanning extends BaseEntity {
    private String planNo;
    private LocalDate planDate;
    private Long productId;
    private BigDecimal quantity;
    private LocalDate startDate;
    private LocalDate endDate;
    private String sourceType;
    private String sourceNo;
    private Integer status;
}
