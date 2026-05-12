package com.erp.sales.dto;

import com.erp.sales.entity.SalOrder;
import com.erp.sales.entity.SalOrderItem;
import lombok.Data;
import java.util.List;

@Data
public class CreateOrderRequest {
    private SalOrder order;
    private List<SalOrderItem> items;
}
