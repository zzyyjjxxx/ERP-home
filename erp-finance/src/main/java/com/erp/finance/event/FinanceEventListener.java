package com.erp.finance.event;

import com.erp.finance.entity.FinPayable;
import com.erp.finance.entity.FinReceivable;
import com.erp.finance.mapper.FinPayableMapper;
import com.erp.finance.mapper.FinReceivableMapper;
import com.erp.purchase.event.GoodsReceivedEvent;
import com.erp.sales.event.GoodsDeliveredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinanceEventListener {

    private final FinPayableMapper payableMapper;
    private final FinReceivableMapper receivableMapper;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onGoodsReceived(GoodsReceivedEvent event) {
        log.info("生成应付: bizNo={}", event.getBizNo());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onGoodsDelivered(GoodsDeliveredEvent event) {
        log.info("生成应收: bizNo={}", event.getBizNo());
    }
}
