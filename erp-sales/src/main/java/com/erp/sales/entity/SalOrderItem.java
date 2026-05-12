package com.erp.sales.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("sal_order_item")
public class SalOrderItem {
    private Long id;
    private Long orderId;
    private Long productId;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal amount;
    private BigDecimal deliveredQty;
    private String remark;
}
