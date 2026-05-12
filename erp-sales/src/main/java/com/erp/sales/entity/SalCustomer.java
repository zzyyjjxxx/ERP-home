package com.erp.sales.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sal_customer")
public class SalCustomer extends BaseEntity {
    private String code;
    private String name;
    private String contactPerson;
    private String phone;
    private String email;
    private String address;
    private Integer status;
    private String remark;
}
