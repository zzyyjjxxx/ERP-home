package com.erp.finance.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.base.BaseController;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.finance.entity.FinPayment;
import com.erp.finance.mapper.FinPaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/finance/payment")
@RequiredArgsConstructor
public class FinPaymentController extends BaseController {

    private final FinPaymentMapper paymentMapper;

    @GetMapping("/list")
    public Result<PageResult<FinPayment>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String paymentType) {
        Page<FinPayment> page = paymentMapper.selectPage(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<FinPayment>()
                        .eq(status != null, FinPayment::getStatus, status)
                        .eq(paymentType != null && !paymentType.isEmpty(), FinPayment::getPaymentType, paymentType)
                        .orderByDesc(FinPayment::getCreateTime));
        return pageResult(page);
    }

    @GetMapping("/{id}")
    public Result<FinPayment> getById(@PathVariable Long id) {
        return Result.ok(paymentMapper.selectById(id));
    }

    @PostMapping
    public Result<?> add(@RequestBody FinPayment payment) {
        payment.setPaymentNo(generatePaymentNo());
        payment.setStatus(0);
        paymentMapper.insert(payment);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody FinPayment payment) {
        payment.setId(id);
        paymentMapper.updateById(payment);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        paymentMapper.deleteById(id);
        return Result.ok();
    }

    private String generatePaymentNo() {
        return "PAY" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
                String.format("%04d", System.currentTimeMillis() % 10000);
    }
}
