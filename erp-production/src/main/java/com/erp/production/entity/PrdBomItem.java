package com.erp.production.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("prd_bom_item")
public class PrdBomItem {
    private Long id;
    private Long bomId;
    private Long materialId;
    private BigDecimal quantity;
    private Long unitId;
    private Integer seq;
}
