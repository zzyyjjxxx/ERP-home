package com.erp.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("inv_category")
public class InvCategory extends BaseEntity {
    private Long parentId;
    private String name;
    private String code;
    private Integer sort;
    private Integer status;
}
