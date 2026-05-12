USE erp;

CREATE TABLE sal_customer (
    id BIGINT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    address VARCHAR(200),
    bank_name VARCHAR(100),
    bank_account VARCHAR(50),
    tax_no VARCHAR(50),
    status TINYINT DEFAULT 1,
    remark VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';

CREATE TABLE sal_order (
    id BIGINT PRIMARY KEY,
    order_no VARCHAR(50) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    order_date DATE NOT NULL,
    delivery_date DATE,
    warehouse_id BIGINT,
    total_qty DECIMAL(18,4) DEFAULT 0,
    total_amount DECIMAL(18,2) DEFAULT 0,
    status TINYINT DEFAULT 0 COMMENT '0草稿 1待审 2已审 3发货中 4完成 5取消',
    auditor_id BIGINT,
    audit_time DATETIME,
    remark VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag TINYINT DEFAULT 0,
    INDEX idx_customer (customer_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售订单表';

CREATE TABLE sal_order_item (
    id BIGINT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity DECIMAL(18,4) NOT NULL,
    unit_price DECIMAL(18,2) NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    delivered_qty DECIMAL(18,4) DEFAULT 0,
    remark VARCHAR(200),
    INDEX idx_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售订单明细';

CREATE TABLE sal_delivery (
    id BIGINT PRIMARY KEY,
    delivery_no VARCHAR(50) NOT NULL UNIQUE,
    order_id BIGINT,
    customer_id BIGINT NOT NULL,
    warehouse_id BIGINT,
    delivery_date DATE NOT NULL,
    total_qty DECIMAL(18,4) DEFAULT 0,
    status TINYINT DEFAULT 0 COMMENT '0待审 1完成 2取消',
    auditor_id BIGINT,
    audit_time DATETIME,
    remark VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售发货单';

CREATE TABLE sal_delivery_item (
    id BIGINT PRIMARY KEY,
    delivery_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    order_qty DECIMAL(18,4) DEFAULT 0,
    delivery_qty DECIMAL(18,4) NOT NULL,
    warehouse_id BIGINT,
    unit_price DECIMAL(18,2),
    amount DECIMAL(18,2),
    INDEX idx_delivery (delivery_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发货明细';

CREATE TABLE sal_return (
    id BIGINT PRIMARY KEY,
    return_no VARCHAR(50) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    order_id BIGINT,
    delivery_id BIGINT,
    warehouse_id BIGINT,
    return_date DATE NOT NULL,
    total_qty DECIMAL(18,4) DEFAULT 0,
    total_amount DECIMAL(18,2) DEFAULT 0,
    reason VARCHAR(500),
    status TINYINT DEFAULT 0 COMMENT '0草稿 1完成',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售退货单';

CREATE TABLE sal_return_item (
    id BIGINT PRIMARY KEY,
    return_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity DECIMAL(18,4) NOT NULL,
    unit_price DECIMAL(18,2) NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    INDEX idx_return (return_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售退货明细';
