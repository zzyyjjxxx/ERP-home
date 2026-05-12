# ERP 系统实施计划

> **For agentic workers:** 使用 superpowers:subagent-driven-development (推荐) 或 superpowers:executing-plans 逐任务实施。任务使用 `- [ ]` 格式跟踪。

**目标:** 从零构建面向中小型企业的进销存+财务+生产一体化 ERP 系统

**架构:** Spring Boot 3.x 模块化单体（Modulith），React 18 + Ant Design 5.x 前端，MySQL + Redis，Docker Compose 部署

**技术栈:** Java 17, Spring Boot 3, MyBatis-Plus, MySQL 8.0, Redis, React 18, TypeScript, Vite, Ant Design 5, Zustand, React Query

---

## Phase 1: 项目脚手架 + 系统管理

### Task 1: 创建 Maven 多模块父 POM

**文件:**
- Create: `erp-homeclaude/pom.xml`

- [ ] **Step 1: 编写父 POM**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.erp</groupId>
    <artifactId>erp-parent</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>
    <name>ERP Parent</name>
    <description>中小企业 ERP 系统 - 父模块</description>

    <modules>
        <module>erp-common</module>
        <module>erp-system</module>
        <module>erp-purchase</module>
        <module>erp-sales</module>
        <module>erp-inventory</module>
        <module>erp-finance</module>
        <module>erp-production</module>
        <module>erp-server</module>
    </modules>

    <properties>
        <java.version>17</java.version>
        <spring-boot.version>3.2.5</spring-boot.version>
        <mybatis-plus.version>3.5.6</mybatis-plus.version>
        <mysql.version>8.3.0</mysql.version>
        <hutool.version>5.8.27</hutool.version>
        <lombok.version>1.18.32</lombok.version>
        <mapstruct.version>1.5.5.Final</mapstruct.version>
        <knife4j.version>4.4.0</knife4j.version>
        <jjwt.version>0.12.5</jjwt.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>com.baomidou</groupId>
                <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
                <version>${mybatis-plus.version}</version>
            </dependency>
            <dependency>
                <groupId>com.mysql</groupId>
                <artifactId>mysql-connector-j</artifactId>
                <version>${mysql.version}</version>
            </dependency>
            <dependency>
                <groupId>cn.hutool</groupId>
                <artifactId>hutool-all</artifactId>
                <version>${hutool.version}</version>
            </dependency>
            <dependency>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${lombok.version}</version>
                <scope>provided</scope>
            </dependency>
            <dependency>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct</artifactId>
                <version>${mapstruct.version}</version>
            </dependency>
            <dependency>
                <groupId>com.github.xiaoymin</groupId>
                <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
                <version>${knife4j.version}</version>
            </dependency>
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-api</artifactId>
                <version>${jjwt.version}</version>
            </dependency>
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-impl</artifactId>
                <version>${jjwt.version}</version>
                <scope>runtime</scope>
            </dependency>
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-jackson</artifactId>
                <version>${jjwt.version}</version>
                <scope>runtime</scope>
            </dependency>
            <dependency>
                <groupId>com.erp</groupId>
                <artifactId>erp-common</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.erp</groupId>
                <artifactId>erp-system</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.erp</groupId>
                <artifactId>erp-purchase</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.erp</groupId>
                <artifactId>erp-sales</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.erp</groupId>
                <artifactId>erp-inventory</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.erp</groupId>
                <artifactId>erp-finance</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.erp</groupId>
                <artifactId>erp-production</artifactId>
                <version>${project.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <version>${spring-boot.version}</version>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>
</project>
```

- [ ] **Step 2: 提交**

```bash
cd /c/Users/Administrator/Desktop/项目/ERP-homeclaude
git init && git add pom.xml && git commit -m "chore: init Maven parent POM"
```

---

### Task 2: 创建 erp-common 公共模块

**文件:**
- Create: `erp-common/pom.xml`
- Create: `erp-common/src/main/java/com/erp/common/base/BaseEntity.java`
- Create: `erp-common/src/main/java/com/erp/common/base/BaseController.java`
- Create: `erp-common/src/main/java/com/erp/common/response/Result.java`
- Create: `erp-common/src/main/java/com/erp/common/response/PageResult.java`
- Create: `erp-common/src/main/java/com/erp/common/exception/BusinessException.java`
- Create: `erp-common/src/main/java/com/erp/common/exception/GlobalExceptionHandler.java`
- Create: `erp-common/src/main/java/com/erp/common/annotation/RequirePermission.java`
- Create: `erp-common/src/main/java/com/erp/common/annotation/OperLog.java`

- [ ] **Step 1: 编写 erp-common/pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.erp</groupId>
        <artifactId>erp-parent</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    <artifactId>erp-common</artifactId>
    <name>ERP Common</name>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>com.github.xiaoymin</groupId>
            <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 2: 编写 BaseEntity.java**

```java
package com.erp.common.base;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class BaseEntity implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    @TableLogic
    private Integer delFlag;
}
```

- [ ] **Step 3: 编写 Result.java**

```java
package com.erp.common.response;

import lombok.Data;
import java.util.UUID;

@Data
public class Result<T> {
    private int code;
    private String message;
    private T data;
    private String traceId;

    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();
        r.code = 200;
        r.message = "操作成功";
        r.data = data;
        r.traceId = UUID.randomUUID().toString().replace("-", "");
        return r;
    }

    public static <T> Result<T> ok() {
        return ok(null);
    }

    public static <T> Result<T> fail(int code, String message) {
        Result<T> r = new Result<>();
        r.code = code;
        r.message = message;
        r.traceId = UUID.randomUUID().toString().replace("-", "");
        return r;
    }

    public static <T> Result<T> fail(String message) {
        return fail(500, message);
    }
}
```

- [ ] **Step 4: 编写 PageResult.java**

```java
package com.erp.common.response;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private List<T> records;
    private long total;
    private long pageNum;
    private long pageSize;

    public static <T> PageResult<T> of(List<T> records, long total, long pageNum, long pageSize) {
        PageResult<T> r = new PageResult<>();
        r.records = records;
        r.total = total;
        r.pageNum = pageNum;
        r.pageSize = pageSize;
        return r;
    }
}
```

- [ ] **Step 5: 编写 BusinessException.java**

```java
package com.erp.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        this(500, message);
    }
}
```

- [ ] **Step 6: 编写 GlobalExceptionHandler.java**

```java
package com.erp.common.exception;

import com.erp.common.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusiness(BusinessException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(BindException.class)
    public Result<?> handleBind(BindException e) {
        String msg = e.getBindingResult().getAllErrors().stream()
                .map(oe -> oe.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b).orElse("参数校验失败");
        return Result.fail(400, msg);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Result<?> handleDuplicateKey(DuplicateKeyException e) {
        return Result.fail(400, "数据已存在，请检查唯一字段");
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.fail("系统繁忙，请稍后重试");
    }
}
```

- [ ] **Step 7: 编写 RequirePermission.java 注解**

```java
package com.erp.common.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    String value();
}
```

- [ ] **Step 8: 编写 OperLog.java 注解**

```java
package com.erp.common.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperLog {
    String module() default "";
    String action() default "";
}
```

- [ ] **Step 9: 编写 BaseController.java**

```java
package com.erp.common.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import org.springframework.web.bind.annotation.*;

public abstract class BaseController {

    protected <T> Result<PageResult<T>> pageResult(Page<T> page) {
        return Result.ok(PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize()));
    }
}
```

- [ ] **Step 10: 提交**

```bash
cd /c/Users/Administrator/Desktop/项目/ERP-homeclaude
git add erp-common/ && git commit -m "feat: add erp-common module with base classes"
```

---

### Task 3: 创建 erp-system 模块 - 实体和数据库

**文件:**
- Create: `erp-system/pom.xml`
- Create: `erp-system/src/main/java/com/erp/system/entity/SysUser.java`
- Create: `erp-system/src/main/java/com/erp/system/entity/SysRole.java`
- Create: `erp-system/src/main/java/com/erp/system/entity/SysMenu.java`
- Create: `erp-system/src/main/java/com/erp/system/entity/SysDept.java`
- Create: `erp-system/src/main/java/com/erp/system/entity/SysUserRole.java`
- Create: `erp-system/src/main/java/com/erp/system/entity/SysRoleMenu.java`
- Create: `erp-system/src/main/java/com/erp/system/entity/SysDictType.java`
- Create: `erp-system/src/main/java/com/erp/system/entity/SysDictData.java`
- Create: `erp-system/src/main/java/com/erp/system/entity/SysOperLog.java`
- Create: `erp-system/src/main/java/com/erp/system/entity/SysEventRecord.java`
- Create: `erp-system/src/main/java/com/erp/system/entity/SysSerialNumber.java`
- Create: `erp-system/src/main/resources/sql/schema-system.sql`

- [ ] **Step 1: 编写 erp-system/pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.erp</groupId>
        <artifactId>erp-parent</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    <artifactId>erp-system</artifactId>
    <name>ERP System</name>

    <dependencies>
        <dependency>
            <groupId>com.erp</groupId>
            <artifactId>erp-common</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 2: 编写 SysUser.java 实体**

```java
package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String email;
    private Long deptId;
    private String avatar;
    private Integer status; // 0禁用 1正常
}
```

- [ ] **Step 3: 编写 SysRole.java 实体**

```java
package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {
    private String roleCode;
    private String roleName;
    private Integer roleSort;
    private Integer dataScope; // 1全部 2本部门 3仅自己
    private Integer status;
}
```

- [ ] **Step 4: 编写 SysMenu.java 实体**

```java
package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends BaseEntity {
    private Long parentId;
    private String menuName;
    private String menuType; // M目录 C菜单 B按钮
    private String path;
    private String component;
    private String icon;
    private String perms;
    private Integer sort;
    private Integer status;
    private Integer visible;
}
```

- [ ] **Step 5: 编写 SysDept.java**

```java
package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
public class SysDept extends BaseEntity {
    private Long parentId;
    private String deptName;
    private String leader;
    private String phone;
    private Integer sort;
    private Integer status;
}
```

- [ ] **Step 6: 编写关联表和其余实体**

```java
// SysUserRole.java
package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_user_role")
public class SysUserRole {
    private Long userId;
    private Long roleId;
}
```

```java
// SysRoleMenu.java
package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_role_menu")
public class SysRoleMenu {
    private Long roleId;
    private Long menuId;
}
```

```java
// SysDictType.java
package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_type")
public class SysDictType extends BaseEntity {
    private String dictCode;
    private String dictName;
    private Integer status;
}
```

```java
// SysDictData.java
package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_data")
public class SysDictData extends BaseEntity {
    private Long dictTypeId;
    private String dictLabel;
    private String dictValue;
    private String cssClass;
    private Integer sort;
    private Integer status;
}
```

```java
// SysOperLog.java
package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_oper_log")
public class SysOperLog extends BaseEntity {
    private Long userId;
    private String module;
    private String action;
    private String method;
    private String requestUri;
    private String params;
    private String result;
    private String ip;
    private Long duration;
    private Integer status;
}
```

```java
// SysEventRecord.java
package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_event_record")
public class SysEventRecord extends BaseEntity {
    private String eventName;
    private String eventData;
    private Integer retryCount;
    private Integer maxRetry;
    private String lastError;
    private Integer status; // 0待处理 1成功 2失败
}
```

```java
// SysSerialNumber.java
package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_serial_number")
public class SysSerialNumber extends BaseEntity {
    private String bizType;
    private String prefix;
    private String dateFormat;
    private Integer seqLength;
    private Long currentSeq;
}
```

- [ ] **Step 7: 编写建表 SQL**

```sql
-- schema-system.sql
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

-- 初始化数据
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
```

- [ ] **Step 8: 提交**

```bash
cd /c/Users/Administrator/Desktop/项目/ERP-homeclaude
git add erp-system/ && git commit -m "feat: add erp-system entities and schema SQL"
```

---

### Task 4: 创建 erp-system Mapper 和 Service 层

**文件:**
- Create: `erp-system/src/main/java/com/erp/system/config/MyBatisPlusConfig.java`
- Create: `erp-system/src/main/java/com/erp/system/config/MetaObjectHandler.java`
- Create: `erp-system/src/main/java/com/erp/system/mapper/SysUserMapper.java`
- Create: `erp-system/src/main/java/com/erp/system/mapper/SysRoleMapper.java`
- Create: `erp-system/src/main/java/com/erp/system/mapper/SysMenuMapper.java`
- Create: `erp-system/src/main/java/com/erp/system/mapper/SysUserRoleMapper.java`
- Create: `erp-system/src/main/java/com/erp/system/mapper/SysRoleMenuMapper.java`
- Create: `erp-system/src/main/java/com/erp/system/service/SysUserService.java`
- Create: `erp-system/src/main/java/com/erp/system/service/impl/SysUserServiceImpl.java`
- Create: `erp-system/src/main/java/com/erp/system/service/SysRoleService.java`
- Create: `erp-system/src/main/java/com/erp/system/service/impl/SysRoleServiceImpl.java`
- Create: `erp-system/src/main/java/com/erp/system/service/SysMenuService.java`
- Create: `erp-system/src/main/java/com/erp/system/service/impl/SysMenuServiceImpl.java`

- [ ] **Step 1: 编写 MyBatisPlusConfig.java**

```java
package com.erp.system.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.erp.**.mapper")
public class MyBatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

- [ ] **Step 2: 编写 MetaObjectHandler.java**

```java
package com.erp.system.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class MetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
```

- [ ] **Step 3: 编写 SysUserMapper.java**

```java
package com.erp.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
```

同样的模式创建 SysRoleMapper, SysMenuMapper, SysUserRoleMapper, SysRoleMenuMapper（均继承 BaseMapper）。

- [ ] **Step 4: 编写 SysUserService.java**

```java
package com.erp.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.system.entity.SysUser;

public interface SysUserService extends IService<SysUser> {
    Page<SysUser> pageUsers(int pageNum, int pageSize, String keyword, Long deptId, Integer status);
    SysUser getByUsername(String username);
    void addUser(SysUser user);
    void updateUser(SysUser user);
    void deleteUsers(List<Long> ids);
    void assignRoles(Long userId, List<Long> roleIds);
}
```

- [ ] **Step 5: 编写 SysUserServiceImpl.java**

```java
package com.erp.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.exception.BusinessException;
import com.erp.system.entity.SysUser;
import com.erp.system.entity.SysUserRole;
import com.erp.system.mapper.SysUserMapper;
import com.erp.system.mapper.SysUserRoleMapper;
import com.erp.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserRoleMapper userRoleMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Page<SysUser> pageUsers(int pageNum, int pageSize, String keyword, Long deptId, Integer status) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(keyword), SysUser::getUsername, keyword)
                .or().like(StrUtil.isNotBlank(keyword), SysUser::getRealName, keyword)
                .eq(deptId != null, SysUser::getDeptId, deptId)
                .eq(status != null, SysUser::getStatus, status)
                .orderByDesc(SysUser::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public SysUser getByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        return getOne(wrapper);
    }

    @Override
    public void addUser(SysUser user) {
        if (getByUsername(user.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        save(user);
    }

    @Override
    public void updateUser(SysUser user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(null);
        }
        updateById(user);
    }

    @Override
    public void deleteUsers(List<Long> ids) {
        removeByIds(ids);
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        userRoleMapper.delete(wrapper);
        for (Long roleId : roleIds) {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            userRoleMapper.insert(ur);
        }
    }
}
```

- [ ] **Step 6: 编写 SysRoleService 和 SysMenuService**

SysRoleService 同上模式，增加 `getRoleMenus(Long roleId)` 和 `assignMenus(Long roleId, List<Long> menuIds)` 方法。  
SysMenuService 提供 `getMenuTree()` 返回树形结构，`getMenusByUserId(Long userId)` 查用户拥有的菜单。

- [ ] **Step 7: 提交**

```bash
cd /c/Users/Administrator/Desktop/项目/ERP-homeclaude
git add erp-system/ && git commit -m "feat: add erp-system mapper and service layers"
```

---

### Task 5: 创建 erp-system Controller 层 + JWT 认证

**文件:**
- Create: `erp-common/src/main/java/com/erp/common/util/JwtUtil.java`
- Create: `erp-system/src/main/java/com/erp/system/config/SecurityConfig.java`
- Create: `erp-system/src/main/java/com/erp/system/dto/LoginDTO.java`
- Create: `erp-system/src/main/java/com/erp/system/dto/UserVO.java`
- Create: `erp-system/src/main/java/com/erp/system/controller/AuthController.java`
- Create: `erp-system/src/main/java/com/erp/system/controller/SysUserController.java`
- Create: `erp-system/src/main/java/com/erp/system/controller/SysRoleController.java`
- Create: `erp-system/src/main/java/com/erp/system/controller/SysMenuController.java`
- Create: `erp-system/src/main/java/com/erp/system/interceptor/AuthInterceptor.java`
- Create: `erp-system/src/main/java/com/erp/system/config/WebMvcConfig.java`
- Create: `erp-system/src/main/java/com/erp/system/annotation/PermissionAspect.java`

- [ ] **Step 1: 编写 JwtUtil.java**

```java
package com.erp.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret:erp-homeclaude-secret-key-2026-min-length-32}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expiration;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId, String username) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey())
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserId(String token) {
        return Long.valueOf(parseToken(token).getSubject());
    }
}
```

- [ ] **Step 2: 编写 AuthInterceptor.java**

```java
package com.erp.system.interceptor;

import cn.hutool.core.util.StrUtil;
import com.erp.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equals(request.getMethod())) return true;

        String token = request.getHeader("Authorization");
        if (StrUtil.isBlank(token) || !token.startsWith("Bearer ")) {
            response.setStatus(401);
            return false;
        }

        try {
            token = token.substring(7);
            Long userId = jwtUtil.getUserId(token);
            request.setAttribute("userId", userId);
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }
    }
}
```

- [ ] **Step 3: 编写 WebMvcConfig.java**

```java
package com.erp.system.config;

import com.erp.system.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/login", "/api/auth/captcha", "/doc.html", "/v3/api-docs/**", "/webjars/**");
    }
}
```

- [ ] **Step 4: 编写 LoginDTO 和 UserVO**

```java
// LoginDTO.java
package com.erp.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
}
```

```java
// UserVO.java
package com.erp.system.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String avatar;
    private Long deptId;
    private String deptName;
    private List<String> roles;
    private List<String> permissions;
}
```

- [ ] **Step 5: 编写 AuthController.java**

```java
package com.erp.system.controller;

import com.erp.common.response.Result;
import com.erp.common.util.JwtUtil;
import com.erp.system.dto.LoginDTO;
import com.erp.system.dto.UserVO;
import com.erp.system.entity.SysUser;
import com.erp.system.service.SysMenuService;
import com.erp.system.service.SysUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService userService;
    private final SysMenuService menuService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO dto) {
        SysUser user = userService.getByUsername(dto.getUsername());
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return Result.fail(401, "用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            return Result.fail(403, "账号已被禁用");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setPermissions(menuService.getPermsByUserId(user.getId()));

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", vo);
        return Result.ok(result);
    }

    @GetMapping("/info")
    public Result<UserVO> info(@RequestAttribute("userId") Long userId) {
        SysUser user = userService.getById(userId);
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setAvatar(user.getAvatar());
        vo.setPermissions(menuService.getPermsByUserId(userId));
        return Result.ok(vo);
    }
}
```

- [ ] **Step 6: 编写 SysUserController.java**

```java
package com.erp.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.annotation.OperLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.base.BaseController;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;
import com.erp.system.entity.SysUser;
import com.erp.system.service.SysUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
public class SysUserController extends BaseController {

    private final SysUserService userService;

    @GetMapping("/list")
    @RequirePermission("system:user:list")
    public Result<PageResult<SysUser>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Integer status) {
        Page<SysUser> page = userService.pageUsers(pageNum, pageSize, keyword, deptId, status);
        return pageResult(page);
    }

    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Long id) {
        return Result.ok(userService.getById(id));
    }

    @PostMapping
    @RequirePermission("system:user:add")
    @OperLog(module = "用户管理", action = "新增用户")
    public Result<?> add(@Valid @RequestBody SysUser user) {
        userService.addUser(user);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @RequirePermission("system:user:edit")
    @OperLog(module = "用户管理", action = "编辑用户")
    public Result<?> update(@PathVariable Long id, @RequestBody SysUser user) {
        user.setId(id);
        userService.updateUser(user);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("system:user:delete")
    @OperLog(module = "用户管理", action = "删除用户")
    public Result<?> delete(@PathVariable Long id) {
        userService.deleteUsers(List.of(id));
        return Result.ok();
    }

    @PutMapping("/{id}/roles")
    @RequirePermission("system:user:edit")
    public Result<?> assignRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        userService.assignRoles(id, roleIds);
        return Result.ok();
    }
}
```

- [ ] **Step 7: 编写 PermissionAspect.java**

```java
package com.erp.system.annotation;

import com.erp.common.annotation.RequirePermission;
import com.erp.common.exception.BusinessException;
import com.erp.system.service.SysMenuService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final SysMenuService menuService;

    @Before("@annotation(rp)")
    public void checkPermission(RequirePermission rp) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return;
        HttpServletRequest request = attrs.getRequest();
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) throw new BusinessException(401, "未登录");

        List<String> perms = menuService.getPermsByUserId(userId);
        if (!perms.contains(rp.value())) {
            throw new BusinessException(403, "无操作权限");
        }
    }
}
```

- [ ] **Step 8: 编写 SysRoleController 和 SysMenuController**

```java
// SysRoleController.java
package com.erp.system.controller;

import com.erp.common.annotation.RequirePermission;
import com.erp.common.response.Result;
import com.erp.system.entity.SysRole;
import com.erp.system.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService roleService;

    @GetMapping("/list")
    @RequirePermission("system:role:list")
    public Result<List<SysRole>> list() {
        return Result.ok(roleService.list());
    }

    @PostMapping
    @RequirePermission("system:role:add")
    public Result<?> add(@RequestBody SysRole role) {
        roleService.save(role);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @RequirePermission("system:role:edit")
    public Result<?> update(@PathVariable Long id, @RequestBody SysRole role) {
        role.setId(id);
        roleService.updateById(role);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("system:role:delete")
    public Result<?> delete(@PathVariable Long id) {
        roleService.removeById(id);
        return Result.ok();
    }

    @GetMapping("/{id}/menus")
    public Result<List<Long>> getRoleMenus(@PathVariable Long id) {
        return Result.ok(roleService.getRoleMenus(id));
    }

    @PutMapping("/{id}/menus")
    @RequirePermission("system:role:edit")
    public Result<?> assignMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        roleService.assignMenus(id, menuIds);
        return Result.ok();
    }
}
```

```java
// SysMenuController.java
package com.erp.system.controller;

import com.erp.common.response.Result;
import com.erp.system.entity.SysMenu;
import com.erp.system.service.SysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuService menuService;

    @GetMapping("/tree")
    public Result<List<SysMenu>> tree() {
        return Result.ok(menuService.getMenuTree());
    }

    @GetMapping("/user/{userId}")
    public Result<List<SysMenu>> userMenus(@PathVariable Long userId) {
        return Result.ok(menuService.getMenusByUserId(userId));
    }

    @PostMapping
    public Result<?> add(@RequestBody SysMenu menu) {
        menuService.save(menu);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody SysMenu menu) {
        menu.setId(id);
        menuService.updateById(menu);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        menuService.removeById(id);
        return Result.ok();
    }
}
```

- [ ] **Step 9: 提交**

```bash
cd /c/Users/Administrator/Desktop/项目/ERP-homeclaude
git add erp-common/ erp-system/ && git commit -m "feat: add controllers, JWT auth, and permission AOP"
```

---

### Task 6: 创建 erp-server 启动模块

**文件:**
- Create: `erp-server/pom.xml`
- Create: `erp-server/src/main/java/com/erp/ErpApplication.java`
- Create: `erp-server/src/main/resources/application.yml`

- [ ] **Step 1: 编写 erp-server/pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.erp</groupId>
        <artifactId>erp-parent</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    <artifactId>erp-server</artifactId>
    <name>ERP Server</name>

    <dependencies>
        <dependency>
            <groupId>com.erp</groupId>
            <artifactId>erp-system</artifactId>
        </dependency>
        <dependency>
            <groupId>com.erp</groupId>
            <artifactId>erp-purchase</artifactId>
        </dependency>
        <dependency>
            <groupId>com.erp</groupId>
            <artifactId>erp-sales</artifactId>
        </dependency>
        <dependency>
            <groupId>com.erp</groupId>
            <artifactId>erp-inventory</artifactId>
        </dependency>
        <dependency>
            <groupId>com.erp</groupId>
            <artifactId>erp-finance</artifactId>
        </dependency>
        <dependency>
            <groupId>com.erp</groupId>
            <artifactId>erp-production</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 2: 编写 ErpApplication.java**

```java
package com.erp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ErpApplication {
    public static void main(String[] args) {
        SpringApplication.run(ErpApplication.class, args);
    }
}
```

- [ ] **Step 3: 编写 application.yml**

```yaml
server:
  port: 8080

spring:
  application:
    name: erp-server
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/erp?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=Asia/Shanghai
    username: root
    password: root123
  data:
    redis:
      host: localhost
      port: 6379
      password:

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      logic-delete-field: delFlag
      logic-delete-value: 1
      logic-not-delete-value: 0

jwt:
  secret: erp-homeclaude-secret-key-2026-min-length-32
  expiration: 86400000

springdoc:
  swagger-ui:
    path: /swagger-ui.html
```

- [ ] **Step 4: 提交**

```bash
cd /c/Users/Administrator/Desktop/项目/ERP-homeclaude
git add erp-server/ && git commit -m "feat: add erp-server startup module"
```

---

### Task 7: 前端项目初始化

**文件:**
- Create: `frontend/package.json`
- Create: `frontend/vite.config.ts`
- Create: `frontend/tsconfig.json`
- Create: `frontend/index.html`
- Create: `frontend/src/main.tsx`
- Create: `frontend/src/App.tsx`
- Create: `frontend/src/utils/request.ts`
- Create: `frontend/src/stores/userStore.ts`
- Create: `frontend/src/services/auth.ts`
- Create: `frontend/src/router/index.tsx`

- [ ] **Step 1: 初始化 Vite + React 项目**

```bash
cd /c/Users/Administrator/Desktop/项目/ERP-homeclaude
npm create vite@latest frontend -- --template react-ts
cd frontend && npm install
npm install antd @ant-design/pro-components @ant-design/icons react-router-dom zustand @tanstack/react-query axios echarts echarts-for-react dayjs
npm install -D @types/echarts
```

- [ ] **Step 2: 编写 vite.config.ts**

```typescript
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: { '@': path.resolve(__dirname, 'src') },
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
});
```

- [ ] **Step 3: 编写 src/utils/request.ts**

```typescript
import axios, { AxiosResponse } from 'axios';
import { message } from 'antd';

const request = axios.create({
  baseURL: '/api',
  timeout: 30000,
});

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

request.interceptors.response.use(
  (response: AxiosResponse) => {
    const { code, message: msg, data } = response.data;
    if (code === 200) return data;
    if (code === 401) {
      localStorage.clear();
      window.location.href = '/login';
      return Promise.reject(new Error(msg));
    }
    message.error(msg || '请求失败');
    return Promise.reject(new Error(msg));
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.clear();
      window.location.href = '/login';
    }
    message.error('网络异常');
    return Promise.reject(error);
  }
);

export default request;
```

- [ ] **Step 4: 编写 src/stores/userStore.ts**

```typescript
import { create } from 'zustand';

interface UserInfo {
  id: number;
  username: string;
  realName: string;
  avatar?: string;
  permissions: string[];
}

interface UserStore {
  token: string;
  user: UserInfo | null;
  setToken: (token: string) => void;
  setUser: (user: UserInfo) => void;
  logout: () => void;
  hasPermission: (perm: string) => boolean;
}

export const useUserStore = create<UserStore>((set, get) => ({
  token: localStorage.getItem('token') || '',
  user: null,
  setToken: (token) => {
    localStorage.setItem('token', token);
    set({ token });
  },
  setUser: (user) => set({ user }),
  logout: () => {
    localStorage.clear();
    set({ token: '', user: null });
  },
  hasPermission: (perm) => {
    const { user } = get();
    return user?.permissions?.includes(perm) ?? false;
  },
}));
```

- [ ] **Step 5: 编写 src/services/auth.ts**

```typescript
import request from '@/utils/request';

export function login(username: string, password: string) {
  return request.post('/auth/login', { username, password });
}

export function getUserInfo() {
  return request.get('/auth/info');
}
```

- [ ] **Step 6: 编写路由 src/router/index.tsx**

```typescript
import { createBrowserRouter, Navigate } from 'react-router-dom';
import Login from '@/pages/login';
import Layout from '@/components/layout';
import UserList from '@/pages/system/user';
import RoleList from '@/pages/system/role';
import MenuList from '@/pages/system/menu';
import DictManage from '@/pages/system/dict';

const router = createBrowserRouter([
  { path: '/login', element: <Login /> },
  {
    path: '/',
    element: <Layout />,
    children: [
      { index: true, element: <Navigate to="/system/user" replace /> },
      { path: 'system/user', element: <UserList /> },
      { path: 'system/role', element: <RoleList /> },
      { path: 'system/menu', element: <MenuList /> },
      { path: 'system/dict', element: <DictManage /> },
    ],
  },
]);

export default router;
```

- [ ] **Step 7: 提交**

```bash
cd /c/Users/Administrator/Desktop/项目/ERP-homeclaude
git add frontend/ && git commit -m "feat: init React frontend with Vite, router, auth"
```

---

### Task 8: 登录页和主布局

**文件:**
- Create: `frontend/src/pages/login/index.tsx`
- Create: `frontend/src/components/layout/index.tsx`
- Create: `frontend/src/components/PermissionBtn.tsx`

- [ ] **Step 1: 编写登录页 login/index.tsx**

```tsx
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Form, Input, Button, Card, message } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { login } from '@/services/auth';
import { useUserStore } from '@/stores/userStore';

export default function Login() {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { setToken, setUser } = useUserStore();

  const onFinish = async (values: { username: string; password: string }) => {
    setLoading(true);
    try {
      const data = await login(values.username, values.password);
      setToken(data.token);
      setUser(data.user);
      message.success('登录成功');
      navigate('/', { replace: true });
    } catch {
      // error handled by interceptor
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      height: '100vh', display: 'flex', justifyContent: 'center',
      alignItems: 'center', background: '#f0f2f5'
    }}>
      <Card title="ERP 管理系统" style={{ width: 400 }}>
        <Form onFinish={onFinish} size="large">
          <Form.Item name="username" rules={[{ required: true, message: '请输入用户名' }]}>
            <Input prefix={<UserOutlined />} placeholder="用户名" />
          </Form.Item>
          <Form.Item name="password" rules={[{ required: true, message: '请输入密码' }]}>
            <Input.Password prefix={<LockOutlined />} placeholder="密码" />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading} block>登录</Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
}
```

- [ ] **Step 2: 编写主布局 layout/index.tsx**

```tsx
import { useEffect, useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { Layout, Menu, Button, theme, Dropdown, Avatar } from 'antd';
import {
  MenuFoldOutlined, MenuUnfoldOutlined, UserOutlined, LogoutOutlined,
  SettingOutlined, ShoppingOutlined, ShopOutlined, InboxOutlined,
  DollarOutlined, ToolOutlined,
} from '@ant-design/icons';
import { useUserStore } from '@/stores/userStore';
import { getUserInfo } from '@/services/auth';

const { Header, Sider, Content } = Layout;

const menuItems = [
  { key: '/system', icon: <SettingOutlined />, label: '系统管理', children: [
    { key: '/system/user', label: '用户管理' },
    { key: '/system/role', label: '角色管理' },
    { key: '/system/menu', label: '菜单管理' },
    { key: '/system/dict', label: '数据字典' },
  ]},
];

export default function MainLayout() {
  const [collapsed, setCollapsed] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const { user, setUser, logout } = useUserStore();

  useEffect(() => {
    if (!user) {
      getUserInfo().then(setUser).catch(() => navigate('/login'));
    }
  }, []);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider trigger={null} collapsible collapsed={collapsed}>
        <div style={{ height: 48, margin: 16, color: '#fff', textAlign: 'center', lineHeight: '48px', fontWeight: 'bold' }}>
          {collapsed ? 'ERP' : 'ERP 管理系统'}
        </div>
        <Menu
          theme="dark" mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={({ key }) => navigate(key)}
        />
      </Sider>
      <Layout>
        <Header style={{ padding: '0 24px', background: '#fff', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Button type="text" icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />} onClick={() => setCollapsed(!collapsed)} />
          <Dropdown menu={{ items: [{ key: 'logout', icon: <LogoutOutlined />, label: '退出登录', onClick: handleLogout }] }}>
            <span style={{ cursor: 'pointer' }}>
              <Avatar size="small" icon={<UserOutlined />} style={{ marginRight: 8 }} />
              {user?.realName || user?.username}
            </span>
          </Dropdown>
        </Header>
        <Content style={{ margin: 24, padding: 24, background: '#fff', borderRadius: 8, minHeight: 280 }}>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
}
```

- [ ] **Step 3: 编写权限按钮组件 PermissionBtn.tsx**

```tsx
import { useUserStore } from '@/stores/userStore';
import { Button, ButtonProps } from 'antd';

interface Props extends ButtonProps {
  permission: string;
}

export default function PermissionBtn({ permission, children, ...props }: Props) {
  const hasPermission = useUserStore((s) => s.hasPermission);
  if (!hasPermission(permission)) return null;
  return <Button {...props}>{children}</Button>;
}
```

- [ ] **Step 4: 提交**

```bash
cd /c/Users/Administrator/Desktop/项目/ERP-homeclaude
git add frontend/ && git commit -m "feat: add login page, layout, and permission button"
```

---

### Task 9: 用户管理页面

**文件:**
- Create: `frontend/src/pages/system/user/index.tsx`
- Create: `frontend/src/services/system.ts`

- [ ] **Step 1: 编写 services/system.ts**

```typescript
import request from '@/utils/request';

export function getUserList(params: any) {
  return request.get('/system/user/list', { params });
}

export function addUser(data: any) {
  return request.post('/system/user', data);
}

export function updateUser(id: number, data: any) {
  return request.put(`/system/user/${id}`, data);
}

export function deleteUser(id: number) {
  return request.delete(`/system/user/${id}`);
}

export function assignUserRoles(id: number, roleIds: number[]) {
  return request.put(`/system/user/${id}/roles`, roleIds);
}

export function getRoleList() {
  return request.get('/system/role/list');
}

export function addRole(data: any) {
  return request.post('/system/role', data);
}

export function updateRole(id: number, data: any) {
  return request.put(`/system/role/${id}`, data);
}

export function deleteRole(id: number) {
  return request.delete(`/system/role/${id}`);
}

export function getRoleMenus(id: number) {
  return request.get(`/system/role/${id}/menus`);
}

export function assignRoleMenus(id: number, menuIds: number[]) {
  return request.put(`/system/role/${id}/menus`, menuIds);
}

export function getMenuTree() {
  return request.get('/system/menu/tree');
}

export function addMenu(data: any) {
  return request.post('/system/menu', data);
}

export function updateMenu(id: number, data: any) {
  return request.put(`/system/menu/${id}`, data);
}

export function deleteMenu(id: number) {
  return request.delete(`/system/menu/${id}`);
}
```

- [ ] **Step 2: 编写用户管理页 system/user/index.tsx**

```tsx
import { useRef } from 'react';
import { ProTable, ModalForm, ProFormText, ProFormSelect } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Button, Tag, message, Popconfirm } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getUserList, addUser, updateUser, deleteUser } from '@/services/system';
import PermissionBtn from '@/components/PermissionBtn';

export default function UserList() {
  const actionRef = useRef<ActionType>();

  const columns: ProColumns[] = [
    { title: '用户名', dataIndex: 'username', key: 'username' },
    { title: '姓名', dataIndex: 'realName', key: 'realName' },
    { title: '手机号', dataIndex: 'phone', key: 'phone', search: false },
    { title: '邮箱', dataIndex: 'email', key: 'email', search: false },
    {
      title: '状态', dataIndex: 'status', key: 'status',
      valueEnum: { 0: { text: '禁用', status: 'Error' }, 1: { text: '正常', status: 'Success' } },
      render: (_, record) => (
        <Tag color={record.status === 1 ? 'green' : 'red'}>
          {record.status === 1 ? '正常' : '禁用'}
        </Tag>
      ),
    },
    { title: '创建时间', dataIndex: 'createTime', key: 'createTime', search: false, valueType: 'dateTime' },
    {
      title: '操作', key: 'action', search: false, fixed: 'right',
      render: (_, record) => (
        <>
          <PermissionBtn permission="system:user:edit" type="link" onClick={() => {
            // open edit modal
          }}>编辑</PermissionBtn>
          <PermissionBtn permission="system:user:delete" type="link" danger>
            <Popconfirm title="确定删除?" onConfirm={async () => {
              await deleteUser(record.id);
              message.success('删除成功');
              actionRef.current?.reload();
            }}>
              删除
            </Popconfirm>
          </PermissionBtn>
        </>
      ),
    },
  ];

  return (
    <ProTable
      columns={columns}
      request={async (params) => {
        const data = await getUserList(params);
        return { data: data.records, total: data.total, success: true };
      }}
      actionRef={actionRef}
      rowKey="id"
      search={{ labelWidth: 'auto' }}
      toolBarRender={() => [
        <PermissionBtn permission="system:user:add" type="primary" icon={<PlusOutlined />}>
          新增用户
        </PermissionBtn>,
      ]}
    />
  );
}
```

- [ ] **Step 3: 提交**

```bash
cd /c/Users/Administrator/Desktop/项目/ERP-homeclaude
git add frontend/ && git commit -m "feat: add user management page with ProTable"
```

---

### Task 10: 角色管理、菜单管理、字典管理页面

这些页面和用户管理页面模式相同，均使用 ProTable + ModalForm。

**文件:**
- Create: `frontend/src/pages/system/role/index.tsx`
- Create: `frontend/src/pages/system/menu/index.tsx`
- Create: `frontend/src/pages/system/dict/index.tsx`

每个页面遵循同样的 ProTable 模式：
- `request={fetch}` 从 API 获取数据
- `toolBarRender` 提供新增按钮（带权限控制）
- 操作列提供编辑/删除（带权限控制）
- 角色页面额外包含"分配权限"按钮打开 Tree 选择弹窗
- 菜单页面使用 Ant Design Tree 组件展示

- [ ] **Step 1: 编写角色管理页 role/index.tsx**

```tsx
import { useRef, useState } from 'react';
import { ProTable, ModalForm, ProFormText, ProFormSelect } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Tree, Modal, message, Popconfirm } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getRoleList, addRole, updateRole, deleteRole, getRoleMenus, assignRoleMenus, getMenuTree } from '@/services/system';
import PermissionBtn from '@/components/PermissionBtn';

export default function RoleList() {
  const actionRef = useRef<ActionType>();
  const [menuVisible, setMenuVisible] = useState(false);
  const [currentRoleId, setCurrentRoleId] = useState<number>();
  const [checkedKeys, setCheckedKeys] = useState<number[]>([]);
  const [menuTree, setMenuTree] = useState<any[]>([]);

  const openMenuModal = async (roleId: number) => {
    setCurrentRoleId(roleId);
    const [menus, tree] = await Promise.all([getRoleMenus(roleId), getMenuTree()]);
    setCheckedKeys(menus || []);
    setMenuTree(tree || []);
    setMenuVisible(true);
  };

  const columns: ProColumns[] = [
    { title: '角色编码', dataIndex: 'roleCode', key: 'roleCode' },
    { title: '角色名称', dataIndex: 'roleName', key: 'roleName' },
    { title: '排序', dataIndex: 'roleSort', key: 'roleSort', search: false },
    {
      title: '操作', key: 'action', search: false,
      render: (_, record) => (
        <>
          <PermissionBtn permission="system:role:edit" type="link" onClick={() => {
            // trigger edit modal
          }}>编辑</PermissionBtn>
          <PermissionBtn permission="system:role:edit" type="link" onClick={() => openMenuModal(record.id)}>分配权限</PermissionBtn>
          <PermissionBtn permission="system:role:delete" type="link" danger>
            <Popconfirm title="确定删除?" onConfirm={async () => {
              await deleteRole(record.id);
              message.success('删除成功');
              actionRef.current?.reload();
            }}>删除</Popconfirm>
          </PermissionBtn>
        </>
      ),
    },
  ];

  return (
    <>
      <ProTable
        columns={columns}
        request={async () => {
          const data = await getRoleList();
          return { data, total: data?.length || 0, success: true };
        }}
        actionRef={actionRef}
        rowKey="id"
        search={false}
        toolBarRender={() => [
          <PermissionBtn permission="system:role:add" type="primary" icon={<PlusOutlined />}>新增角色</PermissionBtn>,
        ]}
      />
      <Modal
        title="分配权限"
        open={menuVisible}
        onCancel={() => setMenuVisible(false)}
        onOk={async () => {
          await assignRoleMenus(currentRoleId!, checkedKeys);
          message.success('权限分配成功');
          setMenuVisible(false);
        }}
      >
        <Tree
          checkable
          defaultExpandAll
          fieldNames={{ title: 'menuName', key: 'id' }}
          treeData={menuTree}
          checkedKeys={checkedKeys}
          onCheck={(keys: any) => setCheckedKeys(keys.checked)}
        />
      </Modal>
    </>
  );
}
```

- [ ] **Step 2: 编写菜单管理页 menu/index.tsx**

```tsx
import { useRef } from 'react';
import { ProTable, ModalForm, ProFormText, ProFormSelect, ProFormDigit } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { message, Popconfirm } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getMenuTree, addMenu, updateMenu, deleteMenu } from '@/services/system';

export default function MenuList() {
  const actionRef = useRef<ActionType>();

  const columns: ProColumns[] = [
    { title: '名称', dataIndex: 'menuName', key: 'menuName' },
    {
      title: '类型', dataIndex: 'menuType', key: 'menuType',
      valueEnum: { M: '目录', C: '菜单', B: '按钮' },
    },
    { title: '路径', dataIndex: 'path', key: 'path', search: false },
    { title: '权限标识', dataIndex: 'perms', key: 'perms', search: false },
    { title: '排序', dataIndex: 'sort', key: 'sort', search: false },
    {
      title: '操作', key: 'action', search: false,
      render: (_, record) => (
        <>
          <a onClick={() => {/* open edit */}}>编辑</a>
          <Popconfirm title="确定删除?" onConfirm={async () => {
            await deleteMenu(record.id);
            message.success('删除成功');
            actionRef.current?.reload();
          }}><a style={{ marginLeft: 8, color: 'red' }}>删除</a></Popconfirm>
        </>
      ),
    },
  ];

  return (
    <ProTable
      columns={columns}
      request={async () => {
        const data = await getMenuTree();
        return { data, total: data?.length || 0, success: true };
      }}
      actionRef={actionRef}
      rowKey="id"
      search={false}
      pagination={false}
      toolBarRender={() => [
        <Button type="primary" icon={<PlusOutlined />}>新增</Button>,
      ]}
    />
  );
}
```

- [ ] **Step 3: 提交**

```bash
cd /c/Users/Administrator/Desktop/项目/ERP-homeclaude
git add frontend/ && git commit -m "feat: add role, menu, dict management pages"
```

---

### Task 11: Docker Compose 部署配置

**文件:**
- Create: `docker-compose.yml`
- Create: `nginx/nginx.conf`
- Create: `frontend/Dockerfile`
- Create: `Dockerfile` (后端)

- [ ] **Step 1: 编写 docker-compose.yml**

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    container_name: erp-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: erp
      TZ: Asia/Shanghai
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      - ./erp-system/src/main/resources/sql:/docker-entrypoint-initdb.d
    command: --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

  redis:
    image: redis:7-alpine
    container_name: erp-redis
    ports:
      - "6379:6379"

  backend:
    build: .
    container_name: erp-backend
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - redis
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/erp?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=Asia/Shanghai
      SPRING_DATA_REDIS_HOST: redis
    restart: always

  nginx:
    image: nginx:alpine
    container_name: erp-nginx
    ports:
      - "80:80"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf
      - ./frontend/dist:/usr/share/nginx/html
    depends_on:
      - backend

volumes:
  mysql-data:
```

- [ ] **Step 2: 编写 nginx/nginx.conf**

```nginx
events { worker_connections 1024; }

http {
    include /etc/nginx/mime.types;

    server {
        listen 80;
        server_name localhost;

        location /api/ {
            proxy_pass http://backend:8080/api/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
        }

        location / {
            root /usr/share/nginx/html;
            try_files $uri $uri/ /index.html;
        }
    }
}
```

- [ ] **Step 3: 编写 Dockerfile**

```dockerfile
# 后端 Dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY erp-server/target/erp-server-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```dockerfile
# frontend/Dockerfile
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
```

- [ ] **Step 4: 提交**

```bash
cd /c/Users/Administrator/Desktop/项目/ERP-homeclaude
git add docker-compose.yml nginx/ Dockerfile frontend/Dockerfile
git commit -m "chore: add Docker Compose and deployment configs"
```

---

## Phase 2: 采购管理 + 库存管理

### Task 12: 采购管理模块后端

**文件:**
- Create: `erp-purchase/pom.xml`
- Create: `erp-purchase/src/main/java/com/erp/purchase/entity/PurSupplier.java`
- Create: `erp-purchase/src/main/java/com/erp/purchase/entity/PurOrder.java`
- Create: `erp-purchase/src/main/java/com/erp/purchase/entity/PurOrderItem.java`
- Create: `erp-purchase/src/main/java/com/erp/purchase/entity/PurReceipt.java`
- Create: `erp-purchase/src/main/java/com/erp/purchase/entity/PurReceiptItem.java`
- Create: `erp-purchase/src/main/java/com/erp/purchase/entity/PurReturn.java`
- Create: `erp-purchase/src/main/java/com/erp/purchase/entity/PurReturnItem.java`
- Create: 对应 Mapper、Service、Controller 文件
- Create: `erp-purchase/src/main/resources/sql/schema-purchase.sql`

- [ ] **Step 1: 编写采购管理 SQL**

```sql
-- schema-purchase.sql
USE erp;

CREATE TABLE pur_supplier (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';

CREATE TABLE pur_order (
    id BIGINT PRIMARY KEY,
    order_no VARCHAR(50) NOT NULL UNIQUE,
    supplier_id BIGINT NOT NULL,
    order_date DATE NOT NULL,
    delivery_date DATE,
    warehouse_id BIGINT,
    total_qty DECIMAL(18,4) DEFAULT 0,
    total_amount DECIMAL(18,2) DEFAULT 0,
    status TINYINT DEFAULT 0 COMMENT '0草稿 1待审 2已审 3收货中 4完成 5取消',
    auditor_id BIGINT,
    audit_time DATETIME,
    remark VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag TINYINT DEFAULT 0,
    INDEX idx_supplier (supplier_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购订单表';

CREATE TABLE pur_order_item (
    id BIGINT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity DECIMAL(18,4) NOT NULL,
    unit_price DECIMAL(18,2) NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    received_qty DECIMAL(18,4) DEFAULT 0,
    remark VARCHAR(200),
    INDEX idx_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购订单明细';

CREATE TABLE pur_receipt (
    id BIGINT PRIMARY KEY,
    receipt_no VARCHAR(50) NOT NULL UNIQUE,
    order_id BIGINT,
    supplier_id BIGINT NOT NULL,
    warehouse_id BIGINT,
    receipt_date DATE NOT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购收货单';

CREATE TABLE pur_receipt_item (
    id BIGINT PRIMARY KEY,
    receipt_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    order_qty DECIMAL(18,4) DEFAULT 0,
    receipt_qty DECIMAL(18,4) NOT NULL,
    warehouse_id BIGINT,
    unit_price DECIMAL(18,2),
    amount DECIMAL(18,2),
    INDEX idx_receipt (receipt_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货明细';

CREATE TABLE pur_return (
    id BIGINT PRIMARY KEY,
    return_no VARCHAR(50) NOT NULL UNIQUE,
    supplier_id BIGINT NOT NULL,
    order_id BIGINT,
    receipt_id BIGINT,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购退货单';

CREATE TABLE pur_return_item (
    id BIGINT PRIMARY KEY,
    return_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity DECIMAL(18,4) NOT NULL,
    unit_price DECIMAL(18,2) NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    INDEX idx_return (return_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退货明细';
```

- [ ] **Step 2: 编写实体、Mapper、Service、Controller**

实体继承 BaseEntity，使用 `@TableName` 映射。Mapper 继承 `BaseMapper<T>`。Service 继承 `ServiceImpl<M, T>`，实现业务逻辑。Controller 使用 `@RestController` + `@RequestMapping("/api/purchase/...")`。所有遵循 Phase 1 建立的模式。

关键业务方法：
- `PurOrderService.createOrder()` — 创建订单，生成流水号
- `PurOrderService.auditOrder(Long id)` — 审核订单，发布 `OrderAuditedEvent`
- `PurReceiptService.createReceipt()` — 创建收货单，发布 `GoodsReceivedEvent`
- 事件监听器 `InventoryEventListener` 在 erp-inventory 中处理库存变更

- [ ] **Step 3: 提交**

```bash
cd /c/Users/Administrator/Desktop/项目/ERP-homeclaude
git add erp-purchase/ && git commit -m "feat: add purchase management module backend"
```

---

### Task 13: 库存管理模块后端

**文件:**
- Create: `erp-inventory/pom.xml`
- Create: `erp-inventory/src/main/java/com/erp/inventory/entity/InvCategory.java`
- Create: `erp-inventory/src/main/java/com/erp/inventory/entity/InvUnit.java`
- Create: `erp-inventory/src/main/java/com/erp/inventory/entity/InvProduct.java`
- Create: `erp-inventory/src/main/java/com/erp/inventory/entity/InvWarehouse.java`
- Create: `erp-inventory/src/main/java/com/erp/inventory/entity/InvStock.java`
- Create: `erp-inventory/src/main/java/com/erp/inventory/entity/InvStockFlow.java`
- Create: `erp-inventory/src/main/java/com/erp/inventory/entity/InvTransfer.java`
- Create: `erp-inventory/src/main/java/com/erp/inventory/entity/InvTransferItem.java`
- Create: `erp-inventory/src/main/java/com/erp/inventory/entity/InvCheck.java`
- Create: `erp-inventory/src/main/java/com/erp/inventory/entity/InvCheckItem.java`
- Create: 对应 Mapper、Service、Controller
- Create: `erp-inventory/src/main/resources/sql/schema-inventory.sql`
- Create: `erp-inventory/src/main/java/com/erp/inventory/event/InventoryEventListener.java`

- [ ] **Step 1: 编写库存管理 SQL（同上模式，约10张表）**

- [ ] **Step 2: 编写库存事件监听器**

```java
// InventoryEventListener.java
package com.erp.inventory.event;

import com.erp.inventory.service.InvStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventListener {

    private final InvStockService stockService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onGoodsReceived(GoodsReceivedEvent event) {
        stockService.increaseStock(event.getProductId(), event.getWarehouseId(),
                event.getQuantity(), "PURCHASE_IN", event.getBizNo());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onGoodsDelivered(GoodsDeliveredEvent event) {
        stockService.decreaseStock(event.getProductId(), event.getWarehouseId(),
                event.getQuantity(), "SALES_OUT", event.getBizNo());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onGoodsReturned(GoodsReturnedEvent event) {
        stockService.increaseStock(event.getProductId(), event.getWarehouseId(),
                event.getQuantity(), "PURCHASE_RETURN", event.getBizNo());
    }
}
```

- [ ] **Step 3: 库存核心方法 InvStockService**

```java
@Service
public class InvStockServiceImpl implements InvStockService {

    @Transactional
    public void increaseStock(Long productId, Long warehouseId, BigDecimal qty, String bizType, String bizNo) {
        InvStock stock = getOrCreateStock(productId, warehouseId);
        BigDecimal before = stock.getQuantity();
        stock.setQuantity(before.add(qty));
        updateById(stock);
        saveFlow(productId, warehouseId, bizType, bizNo, before, qty, stock.getQuantity());
    }

    @Transactional
    public void decreaseStock(Long productId, Long warehouseId, BigDecimal qty, String bizType, String bizNo) {
        InvStock stock = getStock(productId, warehouseId);
        if (stock.getQuantity().compareTo(qty) < 0) {
            throw new BusinessException("库存不足");
        }
        BigDecimal before = stock.getQuantity();
        stock.setQuantity(before.subtract(qty));
        updateById(stock);
        saveFlow(productId, warehouseId, bizType, bizNo, before, qty.negate(), stock.getQuantity());
    }
}
```

- [ ] **Step 4: 提交**

```bash
cd /c/Users/Administrator/Desktop/项目/ERP-homeclaude
git add erp-inventory/ && git commit -m "feat: add inventory management module backend"
```

---

### Task 14: 采购与库存前端页面

**文件:**
- Create: `frontend/src/pages/purchase/supplier/index.tsx`
- Create: `frontend/src/pages/purchase/order/index.tsx`
- Create: `frontend/src/pages/purchase/receipt/index.tsx`
- Create: `frontend/src/pages/purchase/return/index.tsx`
- Create: `frontend/src/pages/inventory/warehouse/index.tsx`
- Create: `frontend/src/pages/inventory/product/index.tsx`
- Create: `frontend/src/pages/inventory/stock/index.tsx`
- Create: `frontend/src/pages/inventory/transfer/index.tsx`
- Create: `frontend/src/pages/inventory/check/index.tsx`
- Create: `frontend/src/services/purchase.ts`
- Create: `frontend/src/services/inventory.ts`
- Update: `frontend/src/router/index.tsx` (添加路由)
- Update: `frontend/src/components/layout/index.tsx` (添加菜单)

所有页面遵循 ProTable + ModalForm 模式，和 Phase 1 的用户管理页面结构一致。

采购订单页面额外包含：
- 主子表结构（订单头 + 明细行），明细行使用 Ant Design Table 内联编辑
- 审核按钮，调用审核接口后刷新
- 收货/退货联动，点击后跳转到对应收货单/退货单页面

库存查询页面：
- 按仓库、分类筛选
- 显示当前库存量、锁定量
- 支持导出 Excel

- [ ] **Step: 提交**

```bash
cd /c/Users/Administrator/Desktop/项目/ERP-homeclaude
git add frontend/ && git commit -m "feat: add purchase and inventory frontend pages"
```

---

## Phase 3: 销售管理

### Task 15: 销售管理模块后端

**文件:**
- Create: `erp-sales/pom.xml`
- Create: `erp-sales/src/main/java/com/erp/sales/entity/SalCustomer.java`
- Create: `erp-sales/src/main/java/com/erp/sales/entity/SalOrder.java`
- Create: `erp-sales/src/main/java/com/erp/sales/entity/SalOrderItem.java`
- Create: `erp-sales/src/main/java/com/erp/sales/entity/SalDelivery.java`
- Create: `erp-sales/src/main/java/com/erp/sales/entity/SalDeliveryItem.java`
- Create: `erp-sales/src/main/java/com/erp/sales/entity/SalReturn.java`
- Create: `erp-sales/src/main/java/com/erp/sales/entity/SalReturnItem.java`
- Create: 对应 Mapper、Service、Controller
- Create: `erp-sales/src/main/resources/sql/schema-sales.sql`

和采购管理完全对称，实体映射到 `sal_*` 表。关键业务：
- `SalOrderService.auditOrder()` → 审核后发布 `OrderApprovedEvent`
- `SalDeliveryService.createDelivery()` → 发货后发布 `GoodsDeliveredEvent`
- `SalReturnService.createReturn()` → 退货入库

### Task 16: 销售管理前端页面

**文件:**
- Create: `frontend/src/pages/sales/customer/index.tsx`
- Create: `frontend/src/pages/sales/order/index.tsx`
- Create: `frontend/src/pages/sales/delivery/index.tsx`
- Create: `frontend/src/pages/sales/return/index.tsx`
- Create: `frontend/src/services/sales.ts`
- Update: router 和 layout 菜单

---

## Phase 4: 财务管理

### Task 17: 财务管理模块后端

**文件:**
- Create: `erp-finance/pom.xml`
- Create: `erp-finance/src/main/java/com/erp/finance/entity/FinAccountSubject.java`
- Create: `erp-finance/src/main/java/com/erp/finance/entity/FinVoucher.java`
- Create: `erp-finance/src/main/java/com/erp/finance/entity/FinVoucherItem.java`
- Create: `erp-finance/src/main/java/com/erp/finance/entity/FinPayable.java`
- Create: `erp-finance/src/main/java/com/erp/finance/entity/FinReceivable.java`
- Create: `erp-finance/src/main/java/com/erp/finance/entity/FinPayment.java`
- Create: 对应 Mapper、Service、Controller
- Create: `erp-finance/src/main/resources/sql/schema-finance.sql`
- Create: `erp-finance/src/main/java/com/erp/finance/event/FinanceEventListener.java`

关键业务：
- 凭证录入：借贷平衡校验
- 应收应付：监听销售发货/采购收货事件，自动生成应收/应付
- 收付款：核销应收/应付
- 财务报表：利润表、负债表 SQL 查询

### Task 18: 财务管理前端页面

**文件:**
- Create: `frontend/src/pages/finance/subject/index.tsx`
- Create: `frontend/src/pages/finance/voucher/index.tsx`
- Create: `frontend/src/pages/finance/receivable/index.tsx`
- Create: `frontend/src/pages/finance/payable/index.tsx`
- Create: `frontend/src/pages/finance/report/index.tsx`
- Create: `frontend/src/services/finance.ts`

---

## Phase 5: 生产管理

### Task 19: 生产管理模块后端

**文件:**
- Create: `erp-production/pom.xml`
- Create: `erp-production/src/main/java/com/erp/production/entity/PrdBom.java`
- Create: `erp-production/src/main/java/com/erp/production/entity/PrdBomItem.java`
- Create: `erp-production/src/main/java/com/erp/production/entity/PrdWorkOrder.java`
- Create: `erp-production/src/main/java/com/erp/production/entity/PrdWorkOrderItem.java`
- Create: `erp-production/src/main/java/com/erp/production/entity/PrdPlanning.java`
- Create: `erp-production/src/main/java/com/erp/production/entity/PrdQc.java`
- Create: 对应 Mapper、Service、Controller
- Create: `erp-production/src/main/resources/sql/schema-production.sql`

关键业务：
- BOM管理：树形结构，多版本
- 工单创建：选择BOM版本，自动展开BOM生成用料明细
- 生产领料：从库存扣减原材料
- 完工入库：成品数量增加，实际消耗回写工单
- MRP简单计算：净需求 = 需求量 - 现有库存 - 在途 + 安全库存

### Task 20: 生产管理前端页面

**文件:**
- Create: `frontend/src/pages/production/bom/index.tsx`
- Create: `frontend/src/pages/production/workorder/index.tsx`
- Create: `frontend/src/pages/production/planning/index.tsx`
- Create: `frontend/src/pages/production/qc/index.tsx`
- Create: `frontend/src/services/production.ts`

---

## Phase 6: 首页仪表盘 + 报表 + 优化

### Task 21: 首页仪表盘

**文件:**
- Create: `frontend/src/pages/dashboard/index.tsx`
- Create: `erp-server/src/main/java/com/erp/controller/DashboardController.java`
- Update: router 添加首页路由

仪表盘内容包括：
- 今日销售额、今日采购额、本月销售额、本月采购额（StatisticCard）
- 库存预警列表（低于安全库存的商品）
- 待处理事项（待审订单数、待收货数）
- 近30天销售趋势图（ECharts 折线图）
- 库存分布饼图

### Task 22: 补充前端菜单和路由

更新 `layout/index.tsx` 添加完整的菜单树：
- 首页 (Dashboard)
- 系统管理（用户、角色、菜单、字典）
- 采购管理（供应商、采购订单、收货、退货）
- 销售管理（客户、销售订单、发货、退货）
- 库存管理（仓库、商品、库存查询、调拨、盘点）
- 财务管理（科目、凭证、应收、应付、报表）
- 生产管理（BOM、工单、计划、质检）

更新 `router/index.tsx` 添加所有页面的路由映射。

### Task 23: 事件重试定时任务

**文件:**
- Create: `erp-system/src/main/java/com/erp/system/task/EventRetryTask.java`

```java
package com.erp.system.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.system.entity.SysEventRecord;
import com.erp.system.mapper.SysEventRecordMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventRetryTask {

    private final SysEventRecordMapper eventRecordMapper;
    private final ApplicationEventPublisher publisher;
    private final ObjectMapper objectMapper;

    @Scheduled(cron = "0 */5 * * * ?")
    public void retryFailedEvents() {
        LambdaQueryWrapper<SysEventRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysEventRecord::getStatus, 0)
                .lt(SysEventRecord::getRetryCount, 3);
        List<SysEventRecord> events = eventRecordMapper.selectList(wrapper);

        for (SysEventRecord record : events) {
            try {
                Object event = objectMapper.readValue(record.getEventData(), Class.forName(record.getEventName()));
                publisher.publishEvent(event);
                record.setStatus(1); // success
            } catch (Exception e) {
                record.setRetryCount(record.getRetryCount() + 1);
                record.setLastError(e.getMessage());
                if (record.getRetryCount() >= 3) {
                    record.setStatus(2); // failed
                    log.error("事件重试失败，eventId={}", record.getId(), e);
                }
            }
            eventRecordMapper.updateById(record);
        }
    }
}
```

### Task 24: 最终验证与优化

- 运行 `mvn clean package` 确保所有模块编译通过
- 运行 `npm run build` 确保前端打包成功
- 运行 `docker-compose up -d` 验证完整部署
- 检查所有 API 是否有对应的 Knife4j 文档
- SQL 索引检查：确保查询字段有合适索引
- 前端路由守卫：未登录跳转登录页

---

## 总结

| 阶段 | 任务数 | 核心产出 |
|------|--------|----------|
| Phase 1 | 11 tasks | 脚手架、RBAC、登录、用户/角色/菜单管理 |
| Phase 2 | 3 tasks | 采购管理、库存管理（含跨模块事件） |
| Phase 3 | 2 tasks | 销售管理 |
| Phase 4 | 2 tasks | 财务管理（总账、应收应付） |
| Phase 5 | 2 tasks | 生产管理（BOM、工单、计划） |
| Phase 6 | 4 tasks | 仪表盘、定时任务、部署验证 |

**代码模式**：所有 CRUD 页面遵循 ProTable + ModalForm 模式。所有后端模块遵循 Entity → Mapper → Service → Controller 分层模式。
