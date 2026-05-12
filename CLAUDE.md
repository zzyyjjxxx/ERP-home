# ERP HomeClaude - 全自动模式

你是全自动开发助手，负责这个 ERP 项目从零到完整实现的全部开发工作。

## 核心指令

1. **自主推进**：不需要等待用户确认，每完成一个任务立即开始下一个。
2. **遵循实施计划**：严格按 `docs/superpowers/plans/2026-05-12-erp-implementation.md` 中的 Phase 顺序执行。
3. **参考设计文档**：遇到设计问题时查阅 `docs/superpowers/specs/2026-05-12-erp-design.md`。
4. **每次提交**：每个 Task 完成后做一次 git commit，信息用中文。

## 项目路径

项目根目录：`C:\Users\Administrator\Desktop\项目\ERP-homeclaude`

## 实施计划位置

- 设计文档：`docs/superpowers/specs/2026-05-12-erp-design.md`
- 实施计划：`docs/superpowers/plans/2026-05-12-erp-implementation.md`

## 技术栈

- 后端：Java 17, Spring Boot 3.2.5, MyBatis-Plus 3.5.6, MySQL 8.0, Redis, Maven 多模块
- 前端：React 18, TypeScript, Vite, Ant Design 5, Zustand, React Query
- 部署：Docker Compose (Nginx + Spring Boot + MySQL + Redis)

## 工作流

1. 读取实施计划中的下一个未完成 Task
2. 分析需要创建/修改的文件
3. 编写代码
4. git add + git commit
5. 继续下一个 Task
6. 如果一个 Phase 的所有 Task 都完成，立即开始下一个 Phase

## 代码规范

- 包名：`com.erp.{module}`
- 统一响应体：`Result<T>`（code, message, data, traceId）
- 所有表都有公共字段：id, create_time, update_time, create_by, update_by, del_flag
- API URL 格式：`/api/{module}/{resource}/*`
- 模块间通过 Spring Events 异步解耦，不直接注入 Service
- 分页请求参数：pageNum, pageSize
