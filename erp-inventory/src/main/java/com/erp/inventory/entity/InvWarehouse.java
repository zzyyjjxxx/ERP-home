package com.erp.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("inv_warehouse")
public class InvWarehouse extends BaseEntity {
    private String code;
    private String name;
    private String address;
    private String keeper;
    private String phone;
    private Integer status;
}
