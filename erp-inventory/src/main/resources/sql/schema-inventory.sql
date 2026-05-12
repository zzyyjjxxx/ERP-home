USE erp;

CREATE TABLE inv_category (
    id BIGINT PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(50) NOT NULL,
    sort INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

CREATE TABLE inv_unit (
    id BIGINT PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    code VARCHAR(20) NOT NULL UNIQUE,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计量单位表';

CREATE TABLE inv_product (
    id BIGINT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    category_id BIGINT,
    unit_id BIGINT,
    spec VARCHAR(100),
    model VARCHAR(100),
    purchase_price DECIMAL(18,2),
    sale_price DECIMAL(18,2),
    min_stock DECIMAL(18,4) DEFAULT 0,
    max_stock DECIMAL(18,4) DEFAULT 0,
    status TINYINT DEFAULT 1,
    remark VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag TINYINT DEFAULT 0,
    INDEX idx_category (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品/物料表';

CREATE TABLE inv_warehouse (
    id BIGINT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(200),
    keeper VARCHAR(50),
    phone VARCHAR(20),
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库表';

CREATE TABLE inv_stock (
    id BIGINT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    quantity DECIMAL(18,4) DEFAULT 0,
    locked_qty DECIMAL(18,4) DEFAULT 0,
    UNIQUE KEY uk_product_warehouse (product_id, warehouse_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

CREATE TABLE inv_stock_flow (
    id BIGINT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    biz_type VARCHAR(50) NOT NULL COMMENT 'PURCHASE_IN/SALES_OUT/TRANSFER_IN/TRANSFER_OUT/PRODUCTION_IN/PRODUCTION_OUT/CHECK_ADJUST',
    biz_no VARCHAR(50),
    before_qty DECIMAL(18,4),
    change_qty DECIMAL(18,4),
    after_qty DECIMAL(18,4),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_product (product_id),
    INDEX idx_warehouse (warehouse_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存流水表';

CREATE TABLE inv_transfer (
    id BIGINT PRIMARY KEY,
    transfer_no VARCHAR(50) NOT NULL UNIQUE,
    from_warehouse_id BIGINT NOT NULL,
    to_warehouse_id BIGINT NOT NULL,
    total_qty DECIMAL(18,4) DEFAULT 0,
    transfer_date DATE,
    status TINYINT DEFAULT 0 COMMENT '0草稿 1出库 2入库 3完成',
    auditor_id BIGINT,
    remark VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='调拨单';

CREATE TABLE inv_transfer_item (
    id BIGINT PRIMARY KEY,
    transfer_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity DECIMAL(18,4) NOT NULL,
    INDEX idx_transfer (transfer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='调拨明细';

CREATE TABLE inv_check (
    id BIGINT PRIMARY KEY,
    check_no VARCHAR(50) NOT NULL UNIQUE,
    warehouse_id BIGINT NOT NULL,
    check_date DATE,
    checker_id BIGINT,
    status TINYINT DEFAULT 0 COMMENT '0盘点中 1完成',
    remark VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盘点单';

CREATE TABLE inv_check_item (
    id BIGINT PRIMARY KEY,
    check_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    book_qty DECIMAL(18,4) DEFAULT 0,
    actual_qty DECIMAL(18,4) DEFAULT 0,
    diff_qty DECIMAL(18,4) DEFAULT 0,
    reason VARCHAR(500),
    INDEX idx_check (check_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盘点明细';
