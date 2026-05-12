package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;

@TableName("sys_dict_data")
public class SysDictData extends BaseEntity {
    private Long dictTypeId;
    private String dictLabel;
    private String dictValue;
    private String cssClass;
    private Integer sort;
    private Integer status;

    public Long getDictTypeId() { return dictTypeId; }
    public void setDictTypeId(Long dictTypeId) { this.dictTypeId = dictTypeId; }
    public String getDictLabel() { return dictLabel; }
    public void setDictLabel(String dictLabel) { this.dictLabel = dictLabel; }
    public String getDictValue() { return dictValue; }
    public void setDictValue(String dictValue) { this.dictValue = dictValue; }
    public String getCssClass() { return cssClass; }
    public void setCssClass(String cssClass) { this.cssClass = cssClass; }
    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
