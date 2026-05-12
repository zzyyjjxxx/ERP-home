package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_event_record")
public class SysEventRecord extends BaseEntity {
    private String eventName;
    private String eventData;
    private Integer retryCount;
    private Integer maxRetry;
    private String lastError;
    private Integer status;
}
