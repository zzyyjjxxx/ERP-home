package com.erp.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("inv_check")
public class InvCheck extends BaseEntity {
    private String checkNo;
    private Long warehouseId;
    private LocalDate checkDate;
    private Long checkerId;
    private Integer status;
    private String remark;
}
