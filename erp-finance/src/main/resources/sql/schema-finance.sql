USE erp;

CREATE TABLE fin_account_subject (
    id BIGINT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    subject_type VARCHAR(20) COMMENT 'ASSET/LIABILITY/EQUITY/INCOME/EXPENSE',
    level INT DEFAULT 1,
    sort INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    remark VARCHAR(200),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会计科目表';

CREATE TABLE fin_voucher (
    id BIGINT PRIMARY KEY,
    voucher_no VARCHAR(50) NOT NULL UNIQUE,
    voucher_date DATE NOT NULL,
    voucher_word VARCHAR(20) DEFAULT '记',
    voucher_type VARCHAR(20) COMMENT '收款/付款/转账',
    total_debit DECIMAL(18,2) DEFAULT 0,
    total_credit DECIMAL(18,2) DEFAULT 0,
    maker_id BIGINT,
    auditor_id BIGINT,
    audit_time DATETIME,
    status TINYINT DEFAULT 0 COMMENT '0草稿 1已审 2过账 3作废',
    remark VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='凭证表';

CREATE TABLE fin_voucher_item (
    id BIGINT PRIMARY KEY,
    voucher_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    summary VARCHAR(200),
    debit_amount DECIMAL(18,2) DEFAULT 0,
    credit_amount DECIMAL(18,2) DEFAULT 0,
    sort INT DEFAULT 0,
    INDEX idx_voucher (voucher_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='凭证分录';

CREATE TABLE fin_receivable (
    id BIGINT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    biz_type VARCHAR(50) COMMENT 'SALES_DELIVERY',
    biz_no VARCHAR(50),
    amount DECIMAL(18,2) NOT NULL,
    received_amount DECIMAL(18,2) DEFAULT 0,
    balance DECIMAL(18,2),
    due_date DATE,
    status TINYINT DEFAULT 0 COMMENT '0未收 1部分收 2已收清',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应收账款';

CREATE TABLE fin_payable (
    id BIGINT PRIMARY KEY,
    supplier_id BIGINT NOT NULL,
    biz_type VARCHAR(50) COMMENT 'PURCHASE_RECEIPT',
    biz_no VARCHAR(50),
    amount DECIMAL(18,2) NOT NULL,
    paid_amount DECIMAL(18,2) DEFAULT 0,
    balance DECIMAL(18,2),
    due_date DATE,
    status TINYINT DEFAULT 0 COMMENT '0未付 1部分付 2已付清',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应付账款';

CREATE TABLE fin_payment (
    id BIGINT PRIMARY KEY,
    payment_no VARCHAR(50) NOT NULL UNIQUE,
    payment_type VARCHAR(20) COMMENT 'RECEIPT收款/PAYMENT付款',
    biz_type VARCHAR(50),
    payer_id BIGINT,
    payee_id BIGINT,
    amount DECIMAL(18,2) NOT NULL,
    payment_method VARCHAR(20) COMMENT 'CASH/BANK/ALIPAY/WECHAT',
    payment_date DATE,
    voucher_id BIGINT,
    status TINYINT DEFAULT 0 COMMENT '0待审 1完成',
    remark VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收付款记录';
