package com.erp.production.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("prd_work_order_item")
public class PrdWorkOrderItem {
    private Long id;
    private Long workOrderId;
    private Long materialId;
    private BigDecimal requireQty;
    private BigDecimal issuedQty;
    private BigDecimal consumedQty;
}
