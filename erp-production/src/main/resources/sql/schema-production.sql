USE erp;

CREATE TABLE prd_bom (
    id BIGINT PRIMARY KEY,
    product_id BIGINT NOT NULL COMMENT '成品ID',
    version VARCHAR(20) DEFAULT '1.0',
    status TINYINT DEFAULT 0 COMMENT '0草稿 1启用 2停用',
    creator_id BIGINT,
    audit_id BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag TINYINT DEFAULT 0,
    INDEX idx_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='BOM表';

CREATE TABLE prd_bom_item (
    id BIGINT PRIMARY KEY,
    bom_id BIGINT NOT NULL,
    material_id BIGINT NOT NULL COMMENT '原材料ID',
    quantity DECIMAL(18,4) NOT NULL COMMENT '标准用量',
    unit_id BIGINT,
    seq INT DEFAULT 0,
    INDEX idx_bom (bom_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='BOM明细';

CREATE TABLE prd_work_order (
    id BIGINT PRIMARY KEY,
    order_no VARCHAR(50) NOT NULL UNIQUE,
    product_id BIGINT NOT NULL,
    bom_id BIGINT,
    plan_qty DECIMAL(18,4) NOT NULL,
    actual_qty DECIMAL(18,4) DEFAULT 0,
    start_date DATE,
    end_date DATE,
    priority INT DEFAULT 0,
    status TINYINT DEFAULT 0 COMMENT '0待领料 1生产中 2完成 3关闭',
    assignee_id BIGINT,
    audit_id BIGINT,
    remark VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag TINYINT DEFAULT 0,
    INDEX idx_product (product_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单表';

CREATE TABLE prd_work_order_item (
    id BIGINT PRIMARY KEY,
    work_order_id BIGINT NOT NULL,
    material_id BIGINT NOT NULL,
    require_qty DECIMAL(18,4) NOT NULL COMMENT '需求数量',
    issued_qty DECIMAL(18,4) DEFAULT 0,
    consumed_qty DECIMAL(18,4) DEFAULT 0,
    INDEX idx_wo (work_order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单用料';

CREATE TABLE prd_planning (
    id BIGINT PRIMARY KEY,
    plan_no VARCHAR(50) NOT NULL UNIQUE,
    plan_date DATE,
    product_id BIGINT NOT NULL,
    quantity DECIMAL(18,4) NOT NULL,
    start_date DATE,
    end_date DATE,
    source_type VARCHAR(20) COMMENT 'SALES_ORDER/MANUAL/STOCK_LOW',
    source_no VARCHAR(50),
    status TINYINT DEFAULT 0 COMMENT '0草稿 1已审 2执行中 3完成',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产计划表';

CREATE TABLE prd_qc (
    id BIGINT PRIMARY KEY,
    qc_no VARCHAR(50) NOT NULL UNIQUE,
    work_order_id BIGINT,
    product_id BIGINT NOT NULL,
    check_qty DECIMAL(18,4) DEFAULT 0,
    pass_qty DECIMAL(18,4) DEFAULT 0,
    fail_qty DECIMAL(18,4) DEFAULT 0,
    checker_id BIGINT,
    check_date DATE,
    result VARCHAR(10) COMMENT 'PASS/FAIL/PARTIAL',
    remark VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质检单';
