package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_oper_log")
public class SysOperLog extends BaseEntity {
    private Long userId;
    private String module;
    private String action;
    private String method;
    private String requestUri;
    private String params;
    private String result;
    private String ip;
    private Long duration;
    private Integer status;
}
