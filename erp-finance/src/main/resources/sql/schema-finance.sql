USE erp;

CREATE TABLE fin_account_subject (
    id BIGINT PRIMARY KEY,
    code VARCHAR(20) NOT NULL,
    name VARCHAR(50) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    subject_type VARCHAR(10) COMMENT 'ASSET/LIABILITY/EQUITY/INCOME/EXPENSE',
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
    voucher_word VARCHAR(10) DEFAULT '记',
    voucher_type VARCHAR(10) COMMENT '收款/付款/转账',
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
    del_flag TINYINT DEFAULT 0,
    INDEX idx_voucher_date (voucher_date)
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

CREATE TABLE fin_payable (
    id BIGINT PRIMARY KEY,
    supplier_id BIGINT NOT NULL,
    biz_type VARCHAR(20) COMMENT 'PURCHASE_RECEIPT',
    biz_no VARCHAR(50),
    amount DECIMAL(18,2) DEFAULT 0,
    paid_amount DECIMAL(18,2) DEFAULT 0,
    balance DECIMAL(18,2) DEFAULT 0,
    due_date DATE,
    status TINYINT DEFAULT 0 COMMENT '0未付 1部分付 2已付清',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag TINYINT DEFAULT 0,
    INDEX idx_supplier (supplier_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应付账款';

CREATE TABLE fin_receivable (
    id BIGINT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    biz_type VARCHAR(20) COMMENT 'SALES_DELIVERY',
    biz_no VARCHAR(50),
    amount DECIMAL(18,2) DEFAULT 0,
    received_amount DECIMAL(18,2) DEFAULT 0,
    balance DECIMAL(18,2) DEFAULT 0,
    due_date DATE,
    status TINYINT DEFAULT 0 COMMENT '0未收 1部分收 2已收清',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag TINYINT DEFAULT 0,
    INDEX idx_customer (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应收账款';

CREATE TABLE fin_payment (
    id BIGINT PRIMARY KEY,
    payment_no VARCHAR(50) NOT NULL UNIQUE,
    payment_type VARCHAR(10) COMMENT 'RECEIPT收款/PAYMENT付款',
    biz_type VARCHAR(20),
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

-- Seed data for common accounting subjects
INSERT INTO fin_account_subject (id, code, name, parent_id, subject_type, level, sort, status) VALUES
(1, '1001', '库存现金', 0, 'ASSET', 1, 1, 1),
(2, '1002', '银行存款', 0, 'ASSET', 1, 2, 1),
(3, '1122', '应收账款', 0, 'ASSET', 1, 3, 1),
(4, '1403', '原材料', 0, 'ASSET', 1, 4, 1),
(5, '1405', '库存商品', 0, 'ASSET', 1, 5, 1),
(6, '2202', '应付账款', 0, 'LIABILITY', 1, 6, 1),
(7, '4001', '实收资本', 0, 'EQUITY', 1, 7, 1),
(8, '5001', '主营业务收入', 0, 'INCOME', 1, 8, 1),
(9, '5401', '主营业务成本', 0, 'EXPENSE', 1, 9, 1),
(10, '5602', '管理费用', 0, 'EXPENSE', 1, 10, 1);
