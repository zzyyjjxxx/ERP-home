package com.erp.inventory.event;

import com.erp.inventory.service.InvStockService;
import com.erp.purchase.event.GoodsReceivedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventListener {

    private final InvStockService stockService;

    @Async
    @EventListener
    public void onGoodsReceived(GoodsReceivedEvent event) {
        log.info("采购收货入库: product={}, qty={}", event.getProductId(), event.getQuantity());
        stockService.increaseStock(event.getProductId(), event.getWarehouseId(),
                event.getQuantity(), "PURCHASE_IN", event.getBizNo());
    }

    @Async
    @EventListener
    public void onGoodsDelivered(GoodsDeliveredEvent event) {
        log.info("销售发货出库: product={}, qty={}", event.getProductId(), event.getQuantity());
        stockService.decreaseStock(event.getProductId(), event.getWarehouseId(),
                event.getQuantity(), "SALES_OUT", event.getBizNo());
    }

    @Async
    @EventListener
    public void onGoodsReturned(GoodsReturnedEvent event) {
        log.info("退货入库: product={}, qty={}", event.getProductId(), event.getQuantity());
        stockService.increaseStock(event.getProductId(), event.getWarehouseId(),
                event.getQuantity(), "PURCHASE_RETURN", event.getBizNo());
    }
}
