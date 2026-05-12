package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;

@TableName("sys_dict_type")
public class SysDictType extends BaseEntity {
    private String dictCode;
    private String dictName;
    private Integer status;

    public String getDictCode() { return dictCode; }
    public void setDictCode(String dictCode) { this.dictCode = dictCode; }
    public String getDictName() { return dictName; }
    public void setDictName(String dictName) { this.dictName = dictName; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
