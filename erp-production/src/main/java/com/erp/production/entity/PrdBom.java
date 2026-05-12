package com.erp.production.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("prd_bom")
public class PrdBom extends BaseEntity {
    private Long productId;
    private String version;
    private Integer status;
    private Long creatorId;
    private Long auditId;
}
