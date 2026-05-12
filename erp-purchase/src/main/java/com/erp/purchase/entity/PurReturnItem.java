package com.erp.purchase.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("pur_return_item")
public class PurReturnItem {
    private Long id;
    private Long returnId;
    private Long productId;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal amount;
}
