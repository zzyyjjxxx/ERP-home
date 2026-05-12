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
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinanceEventListener {

    private final FinPayableMapper payableMapper;
    private final FinReceivableMapper receivableMapper;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onGoodsReceived(GoodsReceivedEvent event) {
        log.info("生成应付: bizNo={}, qty={}", event.getBizNo(), event.getQuantity());
        FinPayable payable = new FinPayable();
        payable.setSupplierId(0L);
        payable.setBizType("PURCHASE_RECEIPT");
        payable.setBizNo(event.getBizNo());
        payable.setAmount(event.getQuantity());
        payable.setPaidAmount(BigDecimal.ZERO);
        payable.setBalance(event.getQuantity());
        payable.setDueDate(LocalDate.now().plusDays(30));
        payable.setStatus(0);
        payableMapper.insert(payable);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onGoodsDelivered(GoodsDeliveredEvent event) {
        log.info("生成应收: bizNo={}, qty={}", event.getBizNo(), event.getQuantity());
        FinReceivable receivable = new FinReceivable();
        receivable.setCustomerId(0L);
        receivable.setBizType("SALES_DELIVERY");
        receivable.setBizNo(event.getBizNo());
        receivable.setAmount(event.getQuantity());
        receivable.setReceivedAmount(BigDecimal.ZERO);
        receivable.setBalance(event.getQuantity());
        receivable.setDueDate(LocalDate.now().plusDays(30));
        receivable.setStatus(0);
        receivableMapper.insert(receivable);
    }
}
