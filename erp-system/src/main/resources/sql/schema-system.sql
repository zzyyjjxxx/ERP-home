CREATE DATABASE IF NOT EXISTS erp DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE erp;

CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    dept_id BIGINT,
    avatar VARCHAR(255),
    status TINYINT DEFAULT 1 COMMENT '0禁用 1正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag TINYINT DEFAULT 0,
    INDEX idx_username (username),
    INDEX idx_dept_id (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    role_name VARCHAR(50) NOT NULL,
    role_sort INT DEFAULT 0,
    data_scope TINYINT DEFAULT 1 COMMENT '1全部 2本部门 3仅自己',
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

CREATE TABLE sys_menu (
    id BIGINT PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    menu_name VARCHAR(50) NOT NULL,
    menu_type CHAR(1) NOT NULL COMMENT 'M目录 C菜单 B按钮',
    path VARCHAR(200),
    component VARCHAR(200),
    icon VARCHAR(50),
    perms VARCHAR(100) COMMENT '权限标识如 purchase:order:add',
    sort INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    visible TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag TINYINT DEFAULT 0,
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

CREATE TABLE sys_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联';

CREATE TABLE sys_role_menu (
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联';

CREATE TABLE sys_dept (
    id BIGINT PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    dept_name VARCHAR(50) NOT NULL,
    leader VARCHAR(50),
    phone VARCHAR(20),
    sort INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

CREATE TABLE sys_dict_type (
    id BIGINT PRIMARY KEY,
    dict_code VARCHAR(50) NOT NULL UNIQUE,
    dict_name VARCHAR(50) NOT NULL,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

CREATE TABLE sys_dict_data (
    id BIGINT PRIMARY KEY,
    dict_type_id BIGINT NOT NULL,
    dict_label VARCHAR(50) NOT NULL,
    dict_value VARCHAR(50) NOT NULL,
    css_class VARCHAR(50),
    sort INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag TINYINT DEFAULT 0,
    INDEX idx_dict_type (dict_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

CREATE TABLE sys_oper_log (
    id BIGINT PRIMARY KEY,
    user_id BIGINT,
    module VARCHAR(50),
    action VARCHAR(50),
    method VARCHAR(100),
    request_uri VARCHAR(200),
    params TEXT,
    result TEXT,
    ip VARCHAR(50),
    duration BIGINT,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    del_flag TINYINT DEFAULT 0,
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

CREATE TABLE sys_event_record (
    id BIGINT PRIMARY KEY,
    event_name VARCHAR(100) NOT NULL,
    event_data JSON,
    retry_count INT DEFAULT 0,
    max_retry INT DEFAULT 3,
    last_error TEXT,
    status TINYINT DEFAULT 0 COMMENT '0待处理 1成功 2失败',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='事件异常记录表';

CREATE TABLE sys_serial_number (
    id BIGINT PRIMARY KEY,
    biz_type VARCHAR(50) NOT NULL UNIQUE,
    prefix VARCHAR(20),
    date_format VARCHAR(20) DEFAULT 'yyyyMMdd',
    seq_length INT DEFAULT 4,
    current_seq BIGINT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流水号表';

-- Init data
INSERT INTO sys_user (id, username, password, real_name, status) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '系统管理员', 1);

INSERT INTO sys_role (id, role_code, role_name, role_sort, data_scope, status) VALUES
(1, 'admin', '超级管理员', 1, 1, 1);

INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, perms, sort, status) VALUES
(1, 0, '系统管理', 'M', '/system', NULL, 'SettingOutlined', NULL, 1, 1),
(2, 1, '用户管理', 'C', '/system/user', 'system/user/index', NULL, 'system:user:list', 1, 1),
(3, 2, '新增用户', 'B', NULL, NULL, NULL, 'system:user:add', 1, 1),
(4, 2, '编辑用户', 'B', NULL, NULL, NULL, 'system:user:edit', 2, 1),
(5, 2, '删除用户', 'B', NULL, NULL, NULL, 'system:user:delete', 3, 1),
(6, 1, '角色管理', 'C', '/system/role', 'system/role/index', NULL, 'system:role:list', 2, 1),
(7, 6, '新增角色', 'B', NULL, NULL, NULL, 'system:role:add', 1, 1),
(8, 6, '编辑角色', 'B', NULL, NULL, NULL, 'system:role:edit', 2, 1),
(9, 6, '删除角色', 'B', NULL, NULL, NULL, 'system:role:delete', 3, 1),
(10, 1, '菜单管理', 'C', '/system/menu', 'system/menu/index', NULL, 'system:menu:list', 3, 1),
(11, 1, '数据字典', 'C', '/system/dict', 'system/dict/index', NULL, 'system:dict:list', 4, 1);

INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 1),(1, 2),(1, 3),(1, 4),(1, 5),(1, 6),(1, 7),(1, 8),(1, 9),(1, 10),(1, 11);

INSERT INTO sys_dept (id, parent_id, dept_name, sort, status) VALUES
(1, 0, '总公司', 1, 1),
(2, 1, '技术部', 1, 1),
(3, 1, '财务部', 2, 1);

INSERT INTO sys_dict_type (id, dict_code, dict_name, status) VALUES
(1, 'sys_user_status', '用户状态', 1);

INSERT INTO sys_dict_data (id, dict_type_id, dict_label, dict_value, sort, status) VALUES
(1, 1, '正常', '1', 1, 1),
(2, 1, '禁用', '0', 2, 1);
