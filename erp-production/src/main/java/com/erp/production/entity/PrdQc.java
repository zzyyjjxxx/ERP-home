package com.erp.production.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("prd_qc")
public class PrdQc extends BaseEntity {
    private String qcNo;
    private Long workOrderId;
    private Long productId;
    private BigDecimal checkQty;
    private BigDecimal passQty;
    private BigDecimal failQty;
    private Long checkerId;
    private LocalDate checkDate;
    private String result;
    private String remark;
}
