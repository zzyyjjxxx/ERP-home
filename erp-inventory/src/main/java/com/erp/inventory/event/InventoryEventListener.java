package com.erp.inventory.event;

import com.erp.inventory.service.InvStockService;
import com.erp.purchase.event.GoodsReceivedEvent;
import com.erp.purchase.event.GoodsReturnedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventListener {

    private final InvStockService stockService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onGoodsReceived(GoodsReceivedEvent event) {
        log.info("库存增加: productId={}, warehouseId={}, qty={}, bizNo={}",
                event.getProductId(), event.getWarehouseId(), event.getQuantity(), event.getBizNo());
        stockService.increaseStock(event.getProductId(), event.getWarehouseId(),
                event.getQuantity(), "PURCHASE_IN", event.getBizNo());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onGoodsReturned(GoodsReturnedEvent event) {
        log.info("退货入库: productId={}, warehouseId={}, qty={}, bizNo={}",
                event.getProductId(), event.getWarehouseId(), event.getQuantity(), event.getBizNo());
        stockService.increaseStock(event.getProductId(), event.getWarehouseId(),
                event.getQuantity(), "PURCHASE_RETURN", event.getBizNo());
    }
}
