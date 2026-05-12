package com.erp.sales.dto;

import com.erp.sales.entity.SalDelivery;
import com.erp.sales.entity.SalDeliveryItem;
import lombok.Data;
import java.util.List;

@Data
public class CreateDeliveryRequest {
    private SalDelivery delivery;
    private List<SalDeliveryItem> items;
}
