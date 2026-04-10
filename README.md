<div align="center">

# 🪐 Venus Framework

一个面向中后台业务系统的现代化后端基础框架，基于 Spring Boot + Maven 构建

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.9-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Maven](https://img.shields.io/badge/Maven-Multi--Module-C71A36.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

</div>

---

## 📖 项目简介

**Venus Framework** 是一个基于 **Spring Boot 3.5.9** 与 **Java 21** 的多模块后端开发框架，用于沉淀企业中后台项目中的通用能力与基础设施封装。
当前仓库聚合了通用工具、数据访问、Web 扩展、安全认证鉴权、工作流、对象存储、消息能力等核心模块，适合作为业务系统底层框架直接接入，也适合作为统一技术基座持续演进。

---

## ✨ 核心特性

- **现代技术栈**：Spring Boot 3.5.9 + Java 21 + Maven 多模块
- **模块化设计**：通用、核心、安全、工作流、Starter 解耦组织
- **安全认证体系**：基于 Spring Security，支持 JWT 与 Redis Token
- **数据访问扩展**：集成 MyBatis-Plus、统一 Query 模型与 Service 抽象
- **工作流能力**：内置流程定义、实例、任务、监听器与数据库脚本
- **简化数据翻译**：自动翻译实现基类接口BaseEnum的枚举字段，提供自定义注解TranslationField实现数据翻译
- **基础设施封装**：支持 Redis、MinIO、阿里云 OSS、RabbitMQ
- **自动配置友好**：简化大部分繁杂配置，结合SpringBoot自动配置提供默认配置

---

## 🧩 模块说明

| 模块 | 说明 |
|------|------|
| `venus-framework-common` | 通用基础能力，包含返回对象、实体基类、异常、JSON、日志、Redis、事务、AOP、工具类等 |
| `venus-framework-core` | 核心业务支撑，包含数据库、MyBatis-Plus、Query、Controller、Service、翻译、OSS、过滤器链、IM 等 |
| `venus-framework-security` | 安全模块，包含认证、授权、Token 管理、权限检查、Servlet 安全扩展 |
| `venus-framework-workflow` | 工作流模块，包含流程定义、流程实例、任务模型、查询服务、监听器与 SQL 初始化脚本 |
| `venus-framework-starter` | Starter 聚合模块，统一引入 common、core、security、workflow，并附带 Banner 监听能力 |

---

## 🛠 技术栈

### 🚀 核心框架

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.9 | 应用基础框架 |
| Java | 21 | 项目编译目标版本 |
| Maven | 3.6+ | 构建工具 |
| MyBatis-Plus | 3.5.7 | ORM 与数据访问增强 |
| Spring Security | - | 认证与授权体系 |

### ☁️ 基础设施

| 技术 | 说明 |
|------|------|
| PostgreSQL / MySQL | 业务数据存储 |
| Redis | Token、缓存、上下文能力支撑 |
| MinIO / 阿里云 OSS | 对象存储 |
| RabbitMQ | 即时消息能力 |
| Druid / HikariCP | 数据库连接池 |

---

## 📦 项目结构

```text
venus-framework/
├── pom.xml
├── LICENSE
├── README.md
├── venus-framework-common/
├── venus-framework-core/
├── venus-framework-security/
├── venus-framework-starter/
└── venus-framework-workflow/
```

---

## 🚀 快速开始

### 🧱 环境要求

在开始之前，请确保本地环境满足以下条件：

| 软件                      | 版本要求 | 说明                        |
|-------------------------|-----|---------------------------|
| JDK                     | 21+ | 项目使用 Java 21 编译           |
| Maven                   | 3.6+ | 推荐 3.9+                   |
| Redis                   | 可选  | 启用缓存 / Token / 会话能力时需要    |
| PostgreSQL / MySQL / 其他 | 必要  | 根据需求选择，使用mysql时默认为8.0以上版本 |
| MinIO / OSS             | 可选  | 启用文件存储能力时需要               |
| RabbitMQ                | 可选  | 启用 IM 能力时可选               |

### 📥 在业务项目中引入

推荐直接引入 Starter：

```xml
<dependency>
    <groupId>io.github.izzcj</groupId>
    <artifactId>venus-framework-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

如果只需要局部能力，也可以按模块单独引入，例如安全模块：

```xml
<dependency>
    <groupId>com.ale.venus</groupId>
    <artifactId>venus-framework-security</artifactId>
    <version>1.0.0</version>
</dependency>
```

### ⚙️ 启用基础能力

```java
import com.ale.venus.common.domain.entity.EntityScan;
import com.ale.venus.security.config.servlet.EnableVenusAuthentication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EntityScan
@EnableVenusAuthentication
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

说明：

- `@EntityScan` 用于扫描框架实体元数据
- `@EnableVenusAuthentication` 用于启用 Venus 认证体系

---

## ⚙️ 配置示例

Venus Framework 的主要配置以 `venus.*` 为前缀。

### 数据库配置

```yaml

venus:
  db:
    type: postgresql
    # 选填，默认为localhost
    host: 127.0.0.1
    # 选填，默认为对应数据库默认端口
    port: 5432
    database: demo
    schema: public
    username: postgres
    password: your-password
    pool:
      pool-type: Hikari
```

### 安全配置

```yaml
venus:
  security:
    # Token类型
    token-type: JWT
    # Token过期时间
    token-expiration: 7d
    # 是否每次请求都刷新Token过期时间
    refresh-token-expiration-per-request: true
    # 允许匿名访问的URI列表
    permitted-uris: []
    # 需要认证的URI列表
    authenticated-uris: []
```

支持的 Token 类型：

- `JWT`
- `REDIS`

### OSS 配置

```yaml
venus:
  oss:
    # 默认提供器
    default-provider: MINIO
    # OSS端点
    endpoint: endpoint
    # OSS域名（一般线上环境才配置）
    domain: domain
    # 访问key
    access-key-id: your-access-key-id
    # 访问密钥
    access-key-secret: your-access-key-secret
    # 文件桶
    bucket: your-bucket
```

### 日志配置
```yaml
venus:
  logging:
    # 是否启用日志
    enabled: true
    # 日志级别
    level:
      com.ale.starblog.admin: debug
    # 日志文件保存配置
    log-file:
      info:
        file-path: star-blog-admin/logs/info.log
      error:
        file-path: star-blog-admin/logs/error.log
```

### 共享数据上下文配置
```yaml
venus:
  shared-data-context:
    # 是否在生命周期结束时清理共享数据
    clear-on-lifecycle: true
```

### IM配置
```yaml
venus:
  im:
    # 是否启用
    enabled: true
    # 消息发送者，默认为websocket，可选为rabbitmq
    sender: websocket
```

### 通用配置
```yaml
venus:
  common:
    # 允许跨域
    enable-cors: true
```

### 工作流配置

```yaml
venus:
  workflow:
    # 是否开启
    enabled: true
    # 是否生成逻辑任务
    generate-logic-task: false
    # 是否生成数据库表结构
    generate-database-table: false
```

---

## 🧠 自动配置能力

项目已通过 Spring Boot 自动装配注册以下能力：

- `common`：Redis、JSON、异常、日志、事务、Spring 公共扩展
- `core`：数据库、MyBatis-Plus、Query、Controller、Service、翻译、OSS、过滤器链、IM
- `security`：安全自动配置、Servlet 安全扩展
- `workflow`：工作流自动配置
- `starter`：Banner 打印监听

---

## 📜 工作流脚本

工作流模块内置了数据库初始化脚本：

- `venus-framework-workflow/src/main/resources/db/flow_engine_init_mysql.sql`
- `venus-framework-workflow/src/main/resources/db/flow_engine_init_postgresql.sql`

使用工作流能力时，可根据目标数据库编写对应脚本，并实现SqlStatementsBuilder接口提供自定义处理逻辑。

---

## 🎯 适用场景

- 企业中后台系统基础框架
- 需要统一认证鉴权能力的业务平台
- 需要通用 CRUD / Query / Service 抽象的项目
- 需要工作流审批能力的系统
- 需要统一封装 Redis、OSS、消息等基础设施的项目

---

## 📝 开发说明

- 应用示例参考：https://github.com/izzcj/star-blog-server

---

## 🤝 贡献指南

欢迎提交 Issue 或 Pull Request 来帮助改进项目。

### 🐞 提交 Issue

- 提供详细的问题描述
- 包含复现步骤和环境信息
- 如有必要，附上关键日志或截图

### 🔧 提交 Pull Request

- Fork 仓库并创建功能分支
- 保持代码风格与现有模块一致
- 提供清晰的变更说明

---

## ⭐ Star

如果这个项目对你有帮助，欢迎 Star 支持。

---

## 📄 开源协议

本项目基于 [Apache License 2.0](LICENSE) 开源协议。

---
