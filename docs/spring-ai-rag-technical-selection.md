# Venus Framework Spring AI 与 RAG 技术选型方案

## 1. 文档目标

本文档用于明确 Venus Framework 引入 `Spring AI` 模块并落地 `RAG` 能力时的技术选型、模块边界和实施路线。

目标不是一次性建设完整 AI 平台，而是在不破坏现有框架风格的前提下，为业务系统提供一套可扩展、可自动装配、可逐步演进的 AI 基础能力。

本文档适用于：

- 在 Venus Framework 中新增 `venus-framework-ai` 模块
- 基于 Spring Boot 3.x 和 Java 21 构建 AI 能力
- 统一封装聊天模型、Embedding、向量库、文档导入与 RAG 调用链
- 为后续业务系统提供标准化接入方式

## 2. 当前框架现状

结合当前仓库代码，已有基础如下：

- 根项目基于 Spring Boot `3.5.9`
- JDK 版本为 `21`
- 已存在 `venus-framework-ai` 模块
- 父 `pom.xml` 已引入 `spring-ai-bom`
- `venus-framework-ai` 当前已接入 `spring-ai-starter-model-deepseek`
- 框架已具备 PostgreSQL、MySQL、Redis、OSS、RabbitMQ 等基础设施封装
- 自动配置风格为 `@AutoConfiguration + @ConfigurationProperties + META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- 统一配置前缀采用 `venus.*`

这意味着 AI 模块不需要从零开始，更适合延续现有框架约定，建设一个独立但风格一致的自动配置模块。

## 3. 选型原则

本次选型遵循以下原则：

1. 尽量复用框架现有基础设施，降低新增中间件数量
2. 默认方案优先考虑中小规模业务系统的落地成本
3. 模型层与向量层解耦，避免将某一家模型供应商绑定为唯一选择
4. 模块设计优先考虑 Starter 化、自动配置化、配置项统一化
5. 先建设可用的 RAG MVP，再逐步增强召回、排序、评测和治理能力

## 4. 总体结论

### 4.1 默认推荐方案

默认推荐采用以下技术组合：

- Chat Model: `DeepSeek`
- Embedding Model: `OpenAI Embedding` 或 `Ollama Embedding`
- Vector Store: `PGVector`
- Cache / Memory: `Redis`
- RAG 编排: `Spring AI Advisor`

对应的一句话结论是：

`DeepSeek Chat + OpenAI/Ollama Embedding + PGVector + Redis`

### 4.2 推荐理由

#### 4.2.1 继续使用 DeepSeek 作为默认聊天模型

原因：

- 当前 `venus-framework-ai` 已经接入 `spring-ai-starter-model-deepseek`
- DeepSeek 适合问答、总结、改写、推理等通用生成场景
- 对中文业务场景友好
- 作为聊天模型接入成本低，便于尽快形成 MVP

但需要明确：

- 聊天模型和 Embedding 模型不应强绑定
- 不建议把 DeepSeek 作为唯一模型供应商设计到框架内部

#### 4.2.2 Embedding 与 Chat 解耦

Embedding 默认推荐两档方案：

- 质量优先：`OpenAI Embedding`
- 私有化优先：`Ollama Embedding`

原因：

- RAG 的召回质量高度依赖 Embedding 模型
- 框架层更适合提供统一接口和自动配置，而不是限定为单一厂商
- 业务项目可能允许公网模型，也可能要求本地私有部署

因此框架层建议提供统一的 `EmbeddingModel` 接入能力，通过配置切换实现不同供应商适配。

#### 4.2.3 默认向量库采用 PGVector

默认向量库推荐使用 `PGVector`。

原因：

- Venus Framework 已经支持 PostgreSQL
- 在已有 PostgreSQL 基础上启用 `pgvector` 扩展，运维成本最低
- 适合大多数中小规模业务知识库场景
- 与业务数据同源时更容易做租户隔离、元数据过滤与事务管理
- 作为框架默认方案，复杂度明显低于引入独立向量数据库

适用范围：

- 单库或少量知识库
- 文档规模在十万级以内
- 以后台管理、企业知识问答、制度问答、工单辅助、客服辅助为主的场景

#### 4.2.4 Redis 不作为默认主向量库

虽然框架已有 Redis 支撑能力，但不建议默认将 Redis 作为 RAG 主向量库。

原因：

- 普通 Redis 不等于可用的向量检索能力
- Spring AI 的 Redis Vector Store 依赖 Redis Stack / RediSearch 能力
- 现有框架对 Redis 的封装主要面向缓存与通用 KV，而不是向量检索

Redis 更适合承担：

- 会话上下文缓存
- 对话记忆
- 热门问题缓存
- 语义缓存
- 检索结果短时缓存

#### 4.2.5 暂不将 Milvus 作为默认方案

Milvus 适合更大规模的向量检索场景，但不适合作为框架初始默认方案。

原因：

- 独立组件更重，运维复杂度更高
- 早期业务规模通常不足以发挥其优势
- 对框架默认落地来说，收益不如 PGVector 明显

Milvus 适合以下场景再引入：

- 百万到千万级文档切片
- 对召回性能有专项要求
- 具备独立 AI 平台或专门运维能力

## 5. 版本选型建议

### 5.1 Spring AI 版本

- 目前版本：`Spring AI 1.1.4`，暂时不升级至2.x版本
- 
### 5.2 Spring Boot 版本

当前 `3.5.9` 可以继续保留，不需要因为引入 Spring AI 而切换主框架版本。

### 5.3 Java 版本

当前 `21` 可以继续保留，满足 Spring AI 与现代 Java 开发要求。

## 6. 模块设计建议

### 6.1 模块定位

建议将 `venus-framework-ai` 设计为框架级 AI 能力模块，而不是某个具体业务项目的 AI 实现。

模块职责包括：

- 模型客户端自动配置
- Embedding 自动配置
- Vector Store 自动配置
- RAG 调用链装配
- 文档切片与导入支持
- 统一配置属性定义
- 对外暴露简化服务接口

### 6.2 建议的包结构

建议包结构如下：

```text
com.ale.venus.ai
├─ config
├─ properties
├─ chat
├─ embedding
├─ vectorstore
├─ rag
├─ ingestion
├─ memory
├─ domain
├─ service
└─ support
```

建议说明：

- `config`: 自动配置类
- `properties`: 配置属性类
- `chat`: 聊天模型封装
- `embedding`: Embedding 模型封装
- `vectorstore`: 向量库适配
- `rag`: 检索增强与顾问链路
- `ingestion`: 文档读取、切片、入库
- `memory`: 对话记忆、缓存
- `service`: 对外服务门面

### 6.3 自动配置风格

建议完全沿用当前框架风格：

- `@AutoConfiguration`
- `@EnableConfigurationProperties`
- `@ConditionalOnClass`
- `@ConditionalOnProperty`
- `@ConditionalOnMissingBean`
- `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

这样可以保证 `venus-framework-ai` 与其他模块的接入方式一致。

## 7. 配置设计建议

建议新增统一配置前缀：

```yaml
venus:
  ai:
    enabled: true
```

在此基础上细分子配置。

### 7.1 总配置

```yaml
venus:
  ai:
    enabled: true
    provider: deepseek
    embedding-provider: openai
    vector-store: pgvector
    rag:
      enabled: true
```

### 7.2 Chat 配置

```yaml
venus:
  ai:
    chat:
      provider: deepseek
      temperature: 0.2
      max-tokens: 4096
```

### 7.3 Embedding 配置

```yaml
venus:
  ai:
    embedding:
      provider: openai
      batch-size: 16
```

### 7.4 PGVector 配置

```yaml
venus:
  ai:
    vector-store:
      provider: pgvector
      initialize-schema: false
      dimensions: 1536
      table-name: ai_document_vector
      distance-type: cosine
```

### 7.5 RAG 配置

```yaml
venus:
  ai:
    rag:
      enabled: true
      top-k: 5
      similarity-threshold: 0.75
      chunk-size: 800
      chunk-overlap: 200
      return-source-documents: true
```

### 7.6 Memory / Cache 配置

```yaml
venus:
  ai:
    memory:
      enabled: true
      provider: redis
      ttl: 30m
```

## 8. 组件选型细化

### 8.1 Chat Model 选型

建议：

- 默认：`DeepSeek`
- 兼容扩展：`OpenAI`、`Ollama`

设计要求：

- 对外暴露统一聊天服务接口
- 允许通过配置切换默认模型提供方
- 不将厂商特定参数泄露到框架统一接口中

### 8.2 Embedding Model 选型

建议：

- 默认优先：`OpenAI Embedding`
- 私有化可选：`Ollama Embedding`

设计要求：

- 和 Chat Model 独立配置
- 允许后续增加 `DashScope`、`Qwen`、`Azure OpenAI` 等实现
- 明确向量维度与模型配置之间的对应关系

### 8.3 Vector Store 选型

建议采用多实现策略：

- 默认实现：`PGVector`
- 备选实现：`Redis Vector Store`
- 备选实现：`Elasticsearch`
- 高阶实现：`Milvus`

建议的默认优先级：

1. `PGVector`
2. `Elasticsearch`
3. `Redis Vector Store`
4. `Milvus`

说明：

- 如果业务系统已经重度使用 ES，可直接走 `Elasticsearch`
- 如果需要纯内存型快速试验，可以考虑 Redis Stack
- 如果需要大规模高性能召回，再考虑 Milvus

## 9. RAG 实现方案

### 9.1 第一阶段采用 Naive RAG

建议 MVP 使用如下链路：

1. 用户问题输入
2. 进行向量检索
3. 将召回文档拼接到 Prompt
4. 调用 Chat Model 生成答案
5. 返回答案与来源片段

在 Spring AI 中，这一阶段建议优先采用：

- `ChatClient`
- `VectorStore`
- `QuestionAnswerAdvisor`

原因：

- 简单直接
- 易于调试
- 更适合作为框架第一版能力

### 9.2 第二阶段升级到增强型 RAG

当第一阶段稳定后，再增加以下能力：

- Query Rewrite
- Multi Query Retrieval
- Metadata Filter
- Hybrid Search
- Rerank
- Answer Grounding
- 引用来源格式化

这一阶段可基于更完整的 Advisor 编排能力扩展。

### 9.3 文档导入链路

建议标准导入链路为：

1. 读取原始文档
2. 抽取文本
3. 文本清洗
4. 按规则切片
5. 附加元数据
6. 生成 Embedding
7. 写入 Vector Store

建议使用的 Spring AI 组件方向：

- `DocumentReader`
- `Document`
- `TextSplitter` 或 `TokenTextSplitter`
- `VectorStore`

建议至少支持的文档类型：

- `txt`
- `md`
- `html`
- `pdf`

后续可扩展：

- `docx`
- `xlsx`
- 业务数据库表记录
- OSS 文件

## 10. 框架内的数据模型建议

建议框架至少定义以下核心概念：

### 10.1 KnowledgeBase

表示知识库本身，建议包含：

- `id`
- `code`
- `name`
- `description`
- `status`
- `tenantId`

### 10.2 KnowledgeDocument

表示原始文档，建议包含：

- `id`
- `knowledgeBaseId`
- `title`
- `sourceType`
- `sourceUri`
- `contentType`
- `status`
- `metadata`

### 10.3 KnowledgeChunk

表示切片后的文档片段，建议包含：

- `id`
- `knowledgeBaseId`
- `documentId`
- `chunkIndex`
- `content`
- `metadata`
- `tokenCount`

说明：

- 如果向量直接存入 PGVector 专用表，则业务表中只保留元数据和原文信息
- 向量表建议保留业务主键映射关系，便于权限过滤和删除重建

## 11. 与现有 Venus Framework 的集成建议

### 11.1 与 PostgreSQL 集成

优先复用现有 PostgreSQL 能力，并通过 `pgvector` 扩展支持向量检索。

建议：

- 框架文档中明确 PostgreSQL 版本要求
- 提供 `pgvector` 初始化 SQL
- 为不同 Embedding 模型说明对应维度要求

### 11.2 与 Redis 集成

Redis 建议承担辅助角色：

- Conversation Memory
- Prompt Cache
- Retrieval Cache
- 用户会话上下文

不建议在框架第一版中将普通 Redis 封装为默认向量检索能力。

### 11.3 与 OSS 集成

框架已有 MinIO / OSS 支撑能力，因此知识库原始文件可优先落 OSS。

建议链路：

- 上传文件到 OSS
- 保存文档元数据到业务表
- 异步触发解析和切片
- 将切片向量写入 PGVector

### 11.4 与安全模块集成

RAG 场景必须考虑数据权限。

建议在检索时支持：

- 按租户过滤
- 按知识库过滤
- 按业务归属过滤
- 按用户权限过滤

这部分应通过 `metadata filter` 实现，而不是完全依赖应用层二次筛选。

## 12. 不同场景下的推荐组合

### 12.1 标准业务项目

推荐组合：

- Chat: `DeepSeek`
- Embedding: `OpenAI`
- Vector Store: `PGVector`
- Cache: `Redis`

适用于：

- 企业后台
- 管理系统
- 客服问答
- 内部知识库问答

### 12.2 私有化部署项目

推荐组合：

- Chat: `DeepSeek` 或 `Ollama`
- Embedding: `Ollama`
- Vector Store: `PGVector`
- Cache: `Redis`

适用于：

- 政企内网
- 对数据不出域有要求的系统

### 12.3 已有搜索平台项目

推荐组合：

- Chat: `DeepSeek`
- Embedding: `OpenAI` 或 `Ollama`
- Vector Store: `Elasticsearch`
- Cache: `Redis`

适用于：

- 已经具备 ES 搜索基础设施
- 希望结合全文检索和向量检索的项目

### 12.4 大规模 AI 平台项目

推荐组合：

- Chat: `DeepSeek`
- Embedding: `OpenAI`
- Vector Store: `Milvus`
- Cache: `Redis`

适用于：

- 大规模知识库
- 高并发召回
- 专门建设 AI 中台的场景

## 13. 实施路线建议

### 13.1 第一阶段：完成 MVP

目标：

- 跑通聊天模型调用
- 跑通 Embedding
- 跑通 PGVector 存储与检索
- 跑通基础问答 RAG

建设内容：

- 升级 `spring-ai-bom`
- 完善 `venus-framework-ai` 依赖
- 增加统一属性类
- 增加自动配置类
- 增加基础聊天服务
- 增加基础 RAG 服务

### 13.2 第二阶段：完成知识库导入能力

目标：

- 支持文档上传
- 支持解析与切片
- 支持入库与重建

建设内容：

- 文档读取器
- 文本切片器
- 向量入库服务
- 文档状态管理
- 异步处理机制

### 13.3 第三阶段：完成治理与增强能力

目标：

- 提高召回质量与可控性
- 满足生产级使用要求

建设内容：

- 多租户隔离
- 权限过滤
- 召回参数调优
- 评测指标
- 引用来源展示
- 监控与日志追踪
- Prompt 模板治理

## 14. 风险与注意事项

### 14.1 模型和 Embedding 维度不一致风险

不同 Embedding 模型输出维度不同，必须确保：

- 向量表维度与 Embedding 模型一致
- 切换模型时支持重建索引

### 14.2 知识库权限风险

RAG 不只是检索正确性问题，也有数据越权问题。

必须在设计早期考虑：

- 多租户隔离
- 用户级访问控制
- 知识库范围控制

### 14.3 成本与延迟风险

公网 Embedding 和大模型调用会带来额外成本与延迟。

因此建议：

- 高价值场景优先启用
- 对热点问题做缓存
- 将 Embedding 和重建过程异步化

### 14.4 框架职责边界风险

`venus-framework-ai` 应提供的是基础能力，而不是完整业务知识库系统。

框架层应避免承载过多业务细节，例如：

- 复杂审核流
- 知识库运营后台
- 专门的评测平台

这些能力更适合在业务项目或上层平台中实现。

## 15. 最终推荐方案

综合当前框架现状、接入成本和后续扩展性，最终建议如下：

### 15.1 默认落地方案

- Spring AI 版本：`1.1.4`
- Chat Model：`DeepSeek`
- Embedding Model：`OpenAI Embedding`
- Vector Store：`PGVector`
- Cache：`Redis`
- RAG 方案：`ChatClient + VectorStore + QuestionAnswerAdvisor`

### 15.2 私有化备选方案

- Spring AI 版本：`1.1.4`
- Chat Model：`DeepSeek` 或本地模型
- Embedding Model：`Ollama Embedding`
- Vector Store：`PGVector`
- Cache：`Redis`

### 15.3 框架设计原则总结

- 默认简单可用
- 模型与向量能力解耦
- 自动配置优先
- 配置前缀统一
- 多实现可切换
- 先 MVP，后增强

## 16. 下一步实施建议

基于本文档，建议按以下顺序继续推进：

1. 升级 `spring-ai-bom` 版本
2. 完善 `venus-framework-ai` 的依赖结构
3. 设计 `venus.ai.*` 配置项
4. 实现自动配置与基础服务门面
5. 补充 `PGVector` 初始化脚本与接入文档
6. 提供一个最小可运行的 RAG 示例

## 17. 参考资料

- Spring AI Reference: https://docs.spring.io/spring-ai/reference/
- DeepSeek Chat: https://docs.spring.io/spring-ai/reference/api/chat/deepseek-chat.html
- Retrieval Augmented Generation: https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html
- Advisors: https://docs.spring.io/spring-ai/reference/api/advisors.html
- PGVector Vector Store: https://docs.spring.io/spring-ai/reference/api/vectordbs/pgvector.html
- Redis Vector Store: https://docs.spring.io/spring-ai/reference/api/vectordbs/redis.html
- Elasticsearch Vector Store: https://docs.spring.io/spring-ai/reference/api/vectordbs/elasticsearch.html
- Milvus Vector Store: https://docs.spring.io/spring-ai/reference/api/vectordbs/milvus.html
- ETL Pipeline: https://docs.spring.io/spring-ai/reference/api/etl-pipeline.html
