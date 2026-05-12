# ERP 系统设计文档

**日期**: 2026-05-12  
**版本**: v1.0  
**定位**: 中小型企业实际使用的 ERP 系统

---

## 1. 概述

### 1.1 项目背景

面向中小型企业的进销存+财务+生产一体化 ERP 系统。支持采购、销售、库存、财务总账/应收应付、生产管理（BOM、工单、计划）及系统管理（RBAC权限）。

### 1.2 一期范围

- 系统管理（用户、角色、菜单权限、部门、数据字典、操作日志）
- 采购管理（供应商、采购订单、收货、退货）
- 销售管理（客户、销售订单、发货、退货）
- 库存管理（仓库、商品/物料、库存、调拨、盘点）
- 财务管理（科目、凭证、应收、应付、收付款）
- 生产管理（BOM、工单、生产计划、质检）

### 1.3 模块依赖关系

```
erp-system ◄── (用户/权限) ──► 所有模块
    │
erp-purchase ──► erp-inventory ──► erp-production
    │                  │
erp-sales ──────► erp-finance
```

跨模块通过 Spring Events 异步解耦，不直接注入 Service。

---

## 2. 技术栈

### 2.1 后端

| 组件 | 选型 | 说明 |
|------|------|------|
| 语言 | Java 17+ LTS | 长期支持版本 |
| 框架 | Spring Boot 3.x | 主框架 |
| ORM | MyBatis-Plus | CRUD 效率高，国内主流 |
| 数据库 | MySQL 8.0 | 关系型数据库 |
| 缓存 | Redis | 权限缓存、会话、数据字典 |
| 模块通信 | Spring Events | 模块间异步解耦 |
| API文档 | SpringDoc + Knife4j | OpenAPI 3.0 |
| 构建 | Maven (multi-module) | 多模块项目管理 |
| 工具库 | Lombok, MapStruct, Hutool | 代码简化 |

### 2.2 前端

| 组件 | 选型 | 说明 |
|------|------|------|
| 框架 | React 18 + TypeScript | SPA |
| 构建 | Vite | 快速开发构建 |
| UI 库 | Ant Design 5.x | 企业级组件库 |
| 状态管理 | Zustand + React Query | 轻量状态 + 服务端缓存 |
| 路由 | React Router v6 | 前端路由 |
| 图表 | ECharts / AntV | 报表、仪表盘 |
| 表格 | Ant Design ProTable | 列表页标准组件 |

### 2.3 部署

- Docker Compose: Nginx + Spring Boot + MySQL + Redis
- Nginx: 静态资源 + 反向代理
- 前后端分离部署

---

## 3. 后端模块结构

```
erp-homeclaude/
├── erp-parent/           # 父 POM，统一依赖版本管理
├── erp-common/           # 公共模块
│   ├── base/             # BaseEntity, BaseController, BaseService
│   ├── exception/        # 全局异常处理, BusinessException
│   ├── response/         # 统一响应体 Result<T>
│   ├── annotation/       # @RequirePermission, @OperLog
│   └── util/             # 工具类
├── erp-system/           # 系统管理
├── erp-purchase/         # 采购管理
├── erp-sales/            # 销售管理
├── erp-inventory/        # 库存管理
├── erp-finance/          # 财务管理
├── erp-production/       # 生产管理
└── erp-server/           # 启动模块（打包入口）
```

### 3.1 模块通信规则

- 模块间通过 Spring Events 异步解耦
- 每个模块独立 Maven module，编译期隔离
- 公共能力（用户信息、字典）通过 erp-common 接口获取
- 事件失败通过补偿重试（定时任务扫异常表）

---

## 4. 前端页面结构

```
src/
├── pages/
│   ├── system/           # 系统管理
│   │   ├── user/         # 用户列表、用户表单
│   │   ├── role/         # 角色列表、角色表单、权限分配
│   │   ├── menu/         # 菜单管理（树形表格）
│   │   └── config/       # 数据字典管理
│   ├── purchase/         # 采购管理
│   │   ├── supplier/     # 供应商管理
│   │   ├── order/        # 采购订单列表/新增/详情
│   │   ├── receipt/      # 采购收货
│   │   └── return/       # 采购退货
│   ├── sales/            # 销售管理
│   │   ├── customer/     # 客户管理
│   │   ├── order/        # 销售订单
│   │   ├── delivery/     # 销售发货
│   │   └── return/       # 销售退货
│   ├── inventory/        # 库存管理
│   │   ├── warehouse/    # 仓库管理
│   │   ├── product/      # 商品/物料管理
│   │   ├── stock/        # 库存查询、库存流水
│   │   ├── transfer/     # 调拨管理
│   │   └── check/        # 盘点管理
│   ├── finance/          # 财务管理
│   │   ├── subject/      # 会计科目
│   │   ├── voucher/      # 凭证管理
│   │   ├── receivable/   # 应收账款
│   │   ├── payable/      # 应付账款
│   │   └── report/       # 财务报表
│   └── production/       # 生产管理
│       ├── bom/          # BOM 管理
│       ├── workorder/    # 工单管理
│       ├── planning/     # 生产计划
│       └── qc/           # 质检管理
├── components/           # 公共组件
│   ├── layout/           # 主布局（侧边栏、顶栏）
│   ├── PermissionBtn/    # 权限按钮组件
│   └── common/           # 通用组件
├── hooks/                # 自定义 Hooks
├── stores/               # Zustand stores (userStore, appStore)
├── services/             # API 请求层（按模块）
├── router/               # 路由配置 + 权限守卫
└── utils/                # 请求拦截器、工具函数
```

---

## 5. RBAC 权限模型

```
用户(User) ──N:M── 角色(Role) ──N:M── 权限(Menu)
                                            │
                             ┌──────────────┼──────────────┐
                             ▼              ▼              ▼
                          菜单权限        按钮权限        数据权限
                         (页面可见)     (新增/编辑/删除)  (部门数据范围)
```

- **菜单权限**: 控制左侧菜单显示/隐藏
- **按钮权限**: 控制页面内操作按钮（格式: `module:action:sub`，如 `purchase:order:add`）
- **数据权限**: 按部门范围隔离数据（全部数据 → 本部门 → 仅自己）
- 前端: 路由守卫 + `<PermissionBtn>` 组件
- 后端: `@RequirePermission` 注解 + AOP 拦截

---

## 6. 核心数据流

### 6.1 销售出库链路

```
销售员创建销售订单 → 审核通过 → 仓库发货 → 库存扣减 → 生成应收
          │              │          │          │
          └─ 锁定库存    └─ 审核    └─ Event → └─ Event → 财务
```

### 6.2 采购入库链路

```
采购员创建采购订单 → 审核通过 → 供应商发货 → 采购收货 → 库存增加 → 生成应付
          │              │          │          │
          └─ 预算检查    └─ 审核    └─ Event → └─ Event → 财务
```

### 6.3 生产工单链路

```
生产计划 → 生成工单 → 领料出库 → 生产完成 → 成品入库 → 质检
              │          │          │          │
              └─ BOM展开 └─ 库存扣减 └─ 入库+   └─ 质检单
```

### 6.4 最终一致性保证

- 所有跨模块操作通过 Spring Events 发送
- 事件处理失败则写入 `sys_event_record` 异常表
- 定时任务（每5分钟）扫描异常表自动重试
- 重试3次仍失败则告警人工处理

---

## 7. 数据库表设计

所有表包含公共字段: `id`(BIGINT PK), `create_time`, `update_time`, `create_by`, `update_by`, `del_flag`(逻辑删除)。

### 7.1 系统管理 (erp-system)

```
sys_user          用户表
  username, password, real_name, phone, email, dept_id, avatar, status(0禁用/1正常)

sys_role          角色表
  role_code, role_name, role_sort, data_scope(1全部/2本部门/3仅自己), status

sys_menu          菜单/权限表
  parent_id, menu_name, menu_type(M目录/C菜单/B按钮), path, component, icon, perms(权限标识), sort, status, visible

sys_user_role     用户-角色关联
  user_id, role_id

sys_role_menu     角色-菜单关联
  role_id, menu_id

sys_dept          部门表
  parent_id, dept_name, leader, phone, sort, status

sys_dict_type     字典类型表
  dict_code, dict_name, status

sys_dict_data     字典数据表
  dict_type_id, dict_label, dict_value, css_class, sort, status

sys_oper_log      操作日志表
  user_id, module, action, method, request_uri, params, result, ip, duration, status

sys_event_record  事件异常记录表
  event_name, event_data(JSON), retry_count, max_retry, last_error, status(0待处理/1成功/2失败)

sys_serial_number 流水号规则表
  biz_type, prefix, date_format, seq_length, current_seq
```

### 7.2 采购管理 (erp-purchase)

```
pur_supplier      供应商表
  code, name, contact_person, phone, email, address, bank_name, bank_account, tax_no, status, remark

pur_order         采购订单表
  order_no, supplier_id, order_date, delivery_date, warehouse_id, total_qty, total_amount, status(0草稿/1待审/2已审/3收货中/4完成/5取消), auditor_id, audit_time, remark

pur_order_item    采购订单明细
  order_id, product_id, quantity, unit_price, amount, received_qty, remark

pur_receipt       采购收货单
  receipt_no, order_id, supplier_id, warehouse_id, receipt_date, total_qty, status(0待审/1完成/2取消), auditor_id, audit_time, remark

pur_receipt_item  收货明细
  receipt_id, product_id, order_qty, receipt_qty, warehouse_id, unit_price, amount

pur_return        采购退货单
  return_no, supplier_id, order_id, receipt_id, warehouse_id, return_date, total_qty, total_amount, reason, status(0草稿/1完成)

pur_return_item   退货明细
  return_id, product_id, quantity, unit_price, amount
```

### 7.3 销售管理 (erp-sales)

```
sal_customer      客户表
  code, name, contact_person, phone, email, address, status, remark

sal_order         销售订单表
  order_no, customer_id, order_date, delivery_date, warehouse_id, total_qty, total_amount, discount_amount, actual_amount, status(0草稿/1待审/2已审/3发货中/4完成/5取消), auditor_id, audit_time, remark

sal_order_item    销售订单明细
  order_id, product_id, quantity, unit_price, amount, delivered_qty, remark

sal_delivery      销售发货单
  delivery_no, order_id, customer_id, warehouse_id, delivery_date, total_qty, status(0待审/1完成), auditor_id, audit_time, remark

sal_delivery_item 发货明细
  delivery_id, product_id, order_qty, delivery_qty, unit_price, amount

sal_return        销售退货单
  return_no, customer_id, order_id, delivery_id, warehouse_id, return_date, total_qty, total_amount, reason, status(0草稿/1完成)

sal_return_item   退货明细
  return_id, product_id, quantity, unit_price, amount
```

### 7.4 库存管理 (erp-inventory)

```
inv_category      商品分类表
  parent_id, name, code, sort, status

inv_unit          计量单位表
  name, code, status

inv_product       商品/物料表
  code, name, category_id, unit_id, spec, model, purchase_price, sale_price, min_stock, max_stock, status, remark

inv_warehouse     仓库表
  code, name, address, keeper, phone, status

inv_stock         库存表
  product_id, warehouse_id, quantity, locked_qty
  UNIQUE(product_id, warehouse_id)

inv_stock_flow    库存流水表
  product_id, warehouse_id, biz_type(PURCHASE_IN/SALES_OUT/TRANSFER_IN/TRANSFER_OUT/PRODUCTION_IN/PRODUCTION_OUT/CHECK_ADJUST), biz_no, before_qty, change_qty, after_qty, create_time

inv_transfer      调拨单
  transfer_no, from_warehouse_id, to_warehouse_id, total_qty, transfer_date, status(0草稿/1出库/2入库/3完成), auditor_id, remark

inv_transfer_item 调拨明细
  transfer_id, product_id, quantity

inv_check         盘点单
  check_no, warehouse_id, check_date, checker_id, status(0盘点中/1完成), remark

inv_check_item    盘点明细
  check_id, product_id, book_qty(账面数), actual_qty(实盘数), diff_qty(差异数), reason
```

### 7.5 财务管理 (erp-finance)

```
fin_account_subject  会计科目表
  code, name, parent_id, subject_type(ASSET/LIABILITY/EQUITY/INCOME/EXPENSE), level, sort, status, remark

fin_voucher          凭证表
  voucher_no, voucher_date, voucher_word(记), voucher_type(收款/付款/转账), total_debit, total_credit, maker_id, auditor_id, audit_time, status(0草稿/1已审/2过账/3作废), remark

fin_voucher_item     凭证分录
  voucher_id, subject_id, summary, debit_amount, credit_amount, sort

fin_payable          应付账款
  supplier_id, biz_type(PURCHASE_RECEIPT), biz_no, amount, paid_amount, balance, due_date, status(0未付/1部分付/2已付清)

fin_receivable       应收账款
  customer_id, biz_type(SALES_DELIVERY), biz_no, amount, received_amount, balance, due_date, status(0未收/1部分收/2已收清)

fin_payment          收付款记录
  payment_no, payment_type(RECEIPT收款/PAYMENT付款), biz_type, payer_id, payee_id, amount, payment_method(CASH/BANK/ALIPAY/WECHAT), payment_date, voucher_id, status(0待审/1完成), remark
```

### 7.6 生产管理 (erp-production)

```
prd_bom             BOM表
  product_id(成品), version, status(0草稿/1启用/2停用), creator_id, audit_id, create_time

prd_bom_item        BOM明细
  bom_id, material_id(原材料), quantity(标准用量), unit_id, seq

prd_work_order     工单表
  order_no, product_id, bom_id, plan_qty, actual_qty(实际完成数), start_date, end_date, priority, status(0待领料/1生产中/2完成/3关闭), assignee_id, audit_id, remark

prd_work_order_item 工单用料
  work_order_id, material_id, require_qty(需求数量), issued_qty(已领数量), consumed_qty(已消耗数量)

prd_planning       生产计划表
  plan_no, plan_date, product_id, quantity, start_date, end_date, source_type(SALES_ORDER/MANUAL/STOCK_LOW), source_no, status(0草稿/1已审/2执行中/3完成)

prd_qc             质检单
  qc_no, work_order_id, product_id, check_qty, pass_qty, fail_qty, checker_id, check_date, result(PASS/FAIL/PARTIAL), remark
```

---

## 8. API 设计规范

### 8.1 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "traceId": "uuid"
}
```

### 8.2 分页请求/响应

```json
// Request
{
  "pageNum": 1,
  "pageSize": 20,
  "keyword": "搜索关键词",
  "...filterParams": {}
}

// Response
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [],
    "total": 100,
    "pageNum": 1,
    "pageSize": 20
  }
}
```

### 8.3 RESTful URL 规范

```
GET    /api/{module}/{resource}/list      # 分页列表
GET    /api/{module}/{resource}/{id}      # 详情
POST   /api/{module}/{resource}           # 新增
PUT    /api/{module}/{resource}/{id}      # 修改
DELETE /api/{module}/{resource}/{id}      # 删除
POST   /api/{module}/{resource}/{id}/audit    # 审核
```

---

## 9. 异常处理

- 统一 `BusinessException` 携带业务错误码
- `@RestControllerAdvice` 全局捕获
- 数据库异常（唯一键冲突、外键约束）转换为友好业务提示
- 未知异常记录日志 + 返回"系统繁忙"

---

## 10. 安全策略

- JWT Token + Redis 缓存会话
- 密码 BCrypt 加密存储
- SQL 注入: MyBatis-Plus 参数化查询 + 前端输入校验
- XSS: 前端输出转义 + 后端 Jackson 配置转义
- CSRF: JWT 机制天然防御
- 接口限流: `@RateLimiter` 注解 + Redis 计数

---

## 11. 实施策略

分阶段实施，每个阶段可独立运行和验证：

| 阶段 | 内容 | 预计页码 |
|------|------|----------|
| Phase 1 | 项目脚手架 + erp-common + erp-system（登录/用户/角色/菜单） | 登录页、用户管理、角色权限 |
| Phase 2 | erp-purchase + erp-inventory（供应商、商品、仓库、采购订单、库存） | 采购流程、库存查询 |
| Phase 3 | erp-sales（客户、销售订单、发货、退货） | 销售流程 |
| Phase 4 | erp-finance（科目、凭证、应收应付、报表） | 财务模块 |
| Phase 5 | erp-production（BOM、工单、生产计划、质检） | 生产模块 |
| Phase 6 | 仪表盘首页 + 报表图表 + 完善优化 | 首页看板、图表 |
