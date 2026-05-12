package com.erp.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("inv_transfer_item")
public class InvTransferItem {
    private Long id;
    private Long transferId;
    private Long productId;
    private BigDecimal quantity;
}
