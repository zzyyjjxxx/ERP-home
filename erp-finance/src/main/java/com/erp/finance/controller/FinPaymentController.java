package com.erp.finance.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.base.BaseController;
import com.erp.common.exception.BusinessException;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.finance.entity.FinPayment;
import com.erp.finance.mapper.FinPaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/finance/payment")
@RequiredArgsConstructor
public class FinPaymentController extends BaseController {

    private final FinPaymentMapper paymentMapper;

    @GetMapping("/list")
    @RequirePermission("finance:payment:list")
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
    @RequirePermission("finance:payment:add")
    public Result<?> add(@RequestBody FinPayment payment) {
        payment.setPaymentNo(generatePaymentNo());
        payment.setStatus(0);
        paymentMapper.insert(payment);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @RequirePermission("finance:payment:edit")
    public Result<?> update(@PathVariable Long id, @RequestBody FinPayment payment) {
        payment.setId(id);
        paymentMapper.updateById(payment);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("finance:payment:delete")
    public Result<?> delete(@PathVariable Long id) {
        paymentMapper.deleteById(id);
        return Result.ok();
    }

    @PutMapping("/{id}/audit")
    @RequirePermission("finance:payment:audit")
    @Transactional
    public Result<?> audit(@PathVariable Long id) {
        FinPayment payment = paymentMapper.selectById(id);
        if (payment == null) throw new BusinessException("付款单不存在");
        if (payment.getStatus() != 0) throw new BusinessException("只能审核待审状态的付款单");
        payment.setStatus(1);
        paymentMapper.updateById(payment);
        return Result.ok();
    }

    @PutMapping("/{id}/unaudit")
    @RequirePermission("finance:payment:audit")
    @Transactional
    public Result<?> unaudit(@PathVariable Long id) {
        FinPayment payment = paymentMapper.selectById(id);
        if (payment == null) throw new BusinessException("付款单不存在");
        if (payment.getStatus() != 1) throw new BusinessException("只能反审核已审状态的付款单");
        payment.setStatus(0);
        paymentMapper.updateById(payment);
        return Result.ok();
    }

    private String generatePaymentNo() {
        return "PAY" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
                String.format("%04d", System.currentTimeMillis() % 10000);
    }
}
