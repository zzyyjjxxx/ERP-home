package com.erp.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("inv_stock_flow")
public class InvStockFlow {
    private Long id;
    private Long productId;
    private Long warehouseId;
    private String bizType;
    private String bizNo;
    private BigDecimal beforeQty;
    private BigDecimal changeQty;
    private BigDecimal afterQty;
    private LocalDateTime createTime;
}
