package com.erp.finance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.base.BaseController;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.finance.entity.FinVoucher;
import com.erp.finance.entity.FinVoucherItem;
import com.erp.finance.mapper.FinVoucherItemMapper;
import com.erp.finance.mapper.FinVoucherMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/finance/voucher")
@RequiredArgsConstructor
public class FinVoucherController extends BaseController {

    private final FinVoucherMapper voucherMapper;
    private final FinVoucherItemMapper itemMapper;

    @GetMapping("/list")
    public Result<PageResult<FinVoucher>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<FinVoucher> page = voucherMapper.selectPage(new Page<>(pageNum, pageSize),
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<FinVoucher>()
                        .orderByDesc(FinVoucher::getCreateTime));
        return pageResult(page);
    }

    @GetMapping("/{id}/items")
    public Result<List<FinVoucherItem>> getItems(@PathVariable Long id) {
        List<FinVoucherItem> items = itemMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<FinVoucherItem>()
                        .eq(FinVoucherItem::getVoucherId, id));
        return Result.ok(items);
    }

    @PostMapping
    public Result<?> add(@RequestBody FinVoucher voucher) {
        voucher.setVoucherNo(generateNo());
        voucher.setStatus(0);
        voucher.setTotalDebit(BigDecimal.ZERO);
        voucher.setTotalCredit(BigDecimal.ZERO);
        voucherMapper.insert(voucher);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        voucherMapper.deleteById(id);
        return Result.ok();
    }

    private String generateNo() {
        return "PZ" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
                String.format("%04d", System.currentTimeMillis() % 10000);
    }
}
