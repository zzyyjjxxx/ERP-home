package com.erp.purchase.dto;

import com.erp.purchase.entity.PurOrder;
import com.erp.purchase.entity.PurOrderItem;
import lombok.Data;
import java.util.List;

@Data
public class CreateOrderRequest {
    private PurOrder order;
    private List<PurOrderItem> items;
}
