package com.erp.sales.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("sal_delivery_item")
public class SalDeliveryItem {
    private Long id;
    private Long deliveryId;
    private Long productId;
    private BigDecimal orderQty;
    private BigDecimal deliveryQty;
    private Long warehouseId;
    private BigDecimal unitPrice;
    private BigDecimal amount;
}
