package com.erp.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("inv_product")
public class InvProduct extends BaseEntity {
    private String code;
    private String name;
    private Long categoryId;
    private Long unitId;
    private String spec;
    private String model;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;
    private BigDecimal minStock;
    private BigDecimal maxStock;
    private Integer status;
    private String remark;
}
