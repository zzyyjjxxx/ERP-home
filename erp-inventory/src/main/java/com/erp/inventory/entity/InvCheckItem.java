package com.erp.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("inv_check_item")
public class InvCheckItem {
    private Long id;
    private Long checkId;
    private Long productId;
    private BigDecimal bookQty;
    private BigDecimal actualQty;
    private BigDecimal diffQty;
    private String reason;
}
