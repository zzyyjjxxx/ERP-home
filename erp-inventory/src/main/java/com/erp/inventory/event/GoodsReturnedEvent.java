package com.erp.inventory.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class GoodsReturnedEvent {
    private Long productId;
    private Long warehouseId;
    private BigDecimal quantity;
    private String bizNo;
}
