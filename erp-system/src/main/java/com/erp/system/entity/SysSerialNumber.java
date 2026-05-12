package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_serial_number")
public class SysSerialNumber extends BaseEntity {
    private String bizType;
    private String prefix;
    private String dateFormat;
    private Integer seqLength;
    private Long currentSeq;
}
