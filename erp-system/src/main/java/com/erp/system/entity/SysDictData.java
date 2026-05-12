package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_data")
public class SysDictData extends BaseEntity {
    private Long dictTypeId;
    private String dictLabel;
    private String dictValue;
    private String cssClass;
    private Integer sort;
    private Integer status;
}
