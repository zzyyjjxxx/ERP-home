package com.erp.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("fin_voucher_item")
public class FinVoucherItem {
    private Long id;
    private Long voucherId;
    private Long subjectId;
    private String summary;
    private BigDecimal debitAmount;
    private BigDecimal creditAmount;
    private Integer sort;
}
