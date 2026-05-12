package com.erp.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_account_subject")
public class FinAccountSubject extends BaseEntity {
    private String code;
    private String name;
    private Long parentId;
    private String subjectType;
    private Integer level;
    private Integer sort;
    private Integer status;
    private String remark;
}
