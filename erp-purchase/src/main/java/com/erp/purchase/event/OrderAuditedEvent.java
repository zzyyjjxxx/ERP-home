package com.erp.purchase.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderAuditedEvent {
    private Long orderId;
    private String orderNo;
}
