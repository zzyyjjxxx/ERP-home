package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;

@TableName("sys_serial_number")
public class SysSerialNumber extends BaseEntity {
    private String bizType;
    private String prefix;
    private String dateFormat;
    private Integer seqLength;
    private Long currentSeq;

    public String getBizType() { return bizType; }
    public void setBizType(String bizType) { this.bizType = bizType; }
    public String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }
    public String getDateFormat() { return dateFormat; }
    public void setDateFormat(String dateFormat) { this.dateFormat = dateFormat; }
    public Integer getSeqLength() { return seqLength; }
    public void setSeqLength(Integer seqLength) { this.seqLength = seqLength; }
    public Long getCurrentSeq() { return currentSeq; }
    public void setCurrentSeq(Long currentSeq) { this.currentSeq = currentSeq; }
}
