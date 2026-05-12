package com.erp.finance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_voucher")
public class FinVoucher extends BaseEntity {
    private String voucherNo;
    private LocalDate voucherDate;
    private String voucherWord;
    private String voucherType;
    private BigDecimal totalDebit;
    private BigDecimal totalCredit;
    private Long makerId;
    private Long auditorId;
    private LocalDateTime auditTime;
    private Integer status;
    private String remark;

    @TableField(exist = false)
    private List<FinVoucherItem> children;
}
