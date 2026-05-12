package com.erp.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("inv_transfer")
public class InvTransfer extends BaseEntity {
    private String transferNo;
    private Long fromWarehouseId;
    private Long toWarehouseId;
    private BigDecimal totalQty;
    private LocalDate transferDate;
    private Integer status;
    private Long auditorId;
    private String remark;
}
