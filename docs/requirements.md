# Spring Cloud Alibaba vs Go 微服务对比项目 - 需求与功能规格

## 一、项目目标

### 1.1 核心目标

本项目是一个**对比性教学演示项目**，用于对比 Spring Cloud Alibaba 与 Go 语言系列架构在微服务开发中的语法差异和设计模式对比。当前阶段仅实现 Spring Cloud Alibaba 侧代码，Go 侧代码暂不实现，仅在文档中说明项目定位为对比项目。

### 1.2 次要目标

- 沉淀一套可复用的 Spring Cloud Alibaba 4.0 + Java 21 微服务项目模板
- 覆盖企业级微服务开发中的核心技术点
- 提供 K8s 部署配置，包括中间件主从分离部署
- 为后续 Go 侧实现提供清晰的接口契约和业务参考

### 1.3 成功标准

| 指标 | 目标 |
|---|---|
| 微服务数量 | ≥ 4 个业务服务 + 1 网关 + 1 认证服务 |
| Spring Cloud Alibaba 组件覆盖率 | ≥ 80% |
| K8s 部署文件完整性 | 基础设施可一键部署 |
| API 文档覆盖率 | 100%（SpringDoc） |

---

## 二、业务领域模型

### 2.1 角色定义

| 角色 | 说明 | 权限范围 |
|---|---|---|
| 买家 | 普通消费者，可浏览商品、收藏商品/店铺 | 仅读操作和收藏 |
| 卖家主账号 | 商家管理者，拥有店铺和商品管理权限 | 店铺、商品、子账号管理 |
| 卖家子账号 | 商家员工，由主账号分配有限权限 | 依赖主账号授权 |

### 2.2 核心业务实体

```
┌─────────┐     ┌─────────┐     ┌─────────┐
│  买家   │────▶│ 收藏商品 │────▶│  商品   │
└─────────┘     └─────────┘     └────┬────┘
     │                                  │
     │                                  ▼
     ▼                           ┌─────────┐
┌─────────┐     ┌─────────┐     │  店铺   │
│ 收藏店铺 │────▶│  店铺   │◀────│ 卖家主账号│
└─────────┘     └─────────┘     └────┬────┘
                                     │
                                     ▼
                              ┌───────────┐
                              │ 卖家子账号 │
                              └───────────┘
```

### 2.3 实体关系

| 实体 | 主键 | 外键 | 说明 |
|---|---|---|---|
| Buyer（买家） | id | - | 普通消费者账户 |
| Seller（卖家主账号） | id | - | 商家主账户 |
| SellerSubAccount（卖家子账号） | id | seller_id | 商家员工账户 |
| Shop（店铺） | id | seller_id | 店铺信息 |
| Product（商品） | id | shop_id, seller_id | 商品信息 |
| FavoriteProduct（收藏商品） | id | buyer_id, product_id | 用户收藏商品 |
| FavoriteShop（收藏店铺） | id | buyer_id, shop_id | 用户收藏店铺 |

---

## 三、功能规格

### 3.1 买家模块（user-service）

#### 功能列表

| 功能编号 | 功能名称 | 功能描述 | API 端点 |
|---|---|---|---|
| B-001 | 买家注册 | 买家用户注册账户 | POST /api/auth/register |
| B-002 | 买家登录 | 买家用户登录获取Token | POST /api/auth/login/buyer |
| B-003 | 买家信息查询 | 根据ID查询买家信息 | GET /api/buyer/{id} |
| B-004 | 买家信息更新 | 更新买家个人信息 | PUT /api/buyer/{id} |
| B-005 | 买家注销 | 注销买家账户 | DELETE /api/buyer/{id} |

#### 数据模型

```java
Buyer {
    Long id;           // 主键
    String username;    // 用户名（唯一）
    String password;    // 密码（BCrypt加密）
    String nickname;    // 昵称
    String phone;      // 手机号
    String email;       // 邮箱
    String avatar;      // 头像URL
    Short status;       // 状态：1-正常，0-禁用
}
```

### 3.2 卖家模块（user-service）

#### 功能列表

| 功能编号 | 功能名称 | 功能描述 | API 端点 |
|---|---|---|---|
| S-001 | 卖家注册 | 卖家企业用户注册 | POST /api/auth/register |
| S-002 | 卖家登录 | 卖家登录获取Token | POST /api/auth/login/seller |
| S-003 | 卖家信息查询 | 根据ID查询卖家信息 | GET /api/seller/{id} |
| S-004 | 卖家信息更新 | 更新卖家企业信息 | PUT /api/seller/{id} |
| S-005 | 卖家注销 | 注销卖家账户 | DELETE /api/seller/{id} |

#### 数据模型

```java
Seller {
    Long id;               // 主键
    String username;        // 用户名（唯一）
    String password;        // 密码（BCrypt加密）
    String companyName;     // 企业名称
    String contactName;     // 联系人姓名
    String phone;           // 联系电话
    String email;          // 企业邮箱
    String businessLicense; // 营业执照号
    Short status;          // 状态：1-正常，0-禁用
}
```

### 3.3 卖家子账号模块（user-service）

#### 功能列表

| 功能编号 | 功能名称 | 功能描述 | API 端点 |
|---|---|---|---|
| SA-001 | 子账号登录 | 子账号登录 | POST /api/auth/login/sub-account |
| SA-002 | 子账号列表 | 查询卖家下所有子账号 | GET /api/seller/sub-account/seller/{sellerId} |
| SA-003 | 创建子账号 | 创建新的子账号 | POST /api/seller/sub-account |
| SA-004 | 更新子账号 | 更新子账号信息和权限 | PUT /api/seller/sub-account/{id} |
| SA-005 | 删除子账号 | 删除子账号 | DELETE /api/seller/sub-account/{id} |

#### 数据模型

```java
SellerSubAccount {
    Long id;             // 主键
    Long sellerId;       // 所属卖家主账号ID
    String username;      // 用户名（唯一）
    String password;      // 密码（BCrypt加密）
    String nickname;      // 昵称
    String role;         // 角色：manager-管理员，operator-操作员
    String[] permissions; // 权限列表
    Short status;        // 状态：1-正常，0-禁用
}
```

### 3.4 店铺模块（shop-service）

#### 功能列表

| 功能编号 | 功能名称 | 功能描述 | API 端点 |
|---|---|---|---|
| SH-001 | 创建店铺 | 创建新店铺 | POST /api/shop |
| SH-002 | 店铺详情 | 根据ID查询店铺信息 | GET /api/shop/{id} |
| SH-003 | 店铺列表 | 查询卖家下所有店铺 | GET /api/shop/seller/{sellerId} |
| SH-004 | 更新店铺 | 更新店铺信息 | PUT /api/shop/{id} |
| SH-005 | 删除店铺 | 删除店铺 | DELETE /api/shop/{id} |

#### 数据模型

```java
Shop {
    Long id;           // 主键
    Long sellerId;     // 卖家ID
    String name;       // 店铺名称
    String description; // 店铺描述
    String logo;       // 店铺Logo URL
    String category;   // 店铺分类
    Short status;      // 状态：1-正常，0-禁用
}
```

### 3.5 商品模块（product-service）

#### 功能列表

| 功能编号 | 功能名称 | 功能描述 | API 端点 |
|---|---|---|---|
| P-001 | 创建商品 | 创建新商品 | POST /api/product |
| P-002 | 商品详情 | 根据ID查询商品信息 | GET /api/product/{id} |
| P-003 | 卖家商品列表 | 查询卖家下所有商品 | GET /api/product/seller/{sellerId} |
| P-004 | 店铺商品列表 | 查询店铺下所有商品 | GET /api/product/shop/{shopId} |
| P-005 | 分类商品列表 | 按分类查询商品 | GET /api/product/category/{category} |
| P-006 | 更新商品 | 更新商品信息 | PUT /api/product/{id} |
| P-007 | 删除商品 | 删除商品 | DELETE /api/product/{id} |
| P-008 | 更新库存 | 增减商品库存 | PUT /api/product/{id}/stock |

#### 数据模型

```java
Product {
    Long id;           // 主键
    Long shopId;       // 店铺ID
    Long sellerId;     // 卖家ID
    String name;       // 商品名称
    String description;// 商品描述
    BigDecimal price;  // 商品价格
    Integer stock;     // 库存数量
    String category;   // 商品分类
    String[] images;   // 商品图片URL列表
    Short status;      // 状态：1-上架，0-下架
}
```

### 3.6 收藏商品模块（favorite-service）

#### 功能列表

| 功能编号 | 功能名称 | 功能描述 | API 端点 |
|---|---|---|---|
| F-001 | 收藏商品 | 收藏指定商品 | POST /api/favorite/product/{productId} |
| F-002 | 收藏列表 | 获取当前用户收藏的商品列表 | GET /api/favorite/product/list |
| F-003 | 取消收藏 | 取消收藏指定商品 | DELETE /api/favorite/product/{productId} |

#### 数据模型

```java
FavoriteProduct {
    Long id;         // 主键
    Long buyerId;    // 买家ID
    Long productId;  // 商品ID
}
```

### 3.7 收藏店铺模块（favorite-service）

#### 功能列表

| 功能编号 | 功能名称 | 功能描述 | API 端点 |
|---|---|---|---|
| FS-001 | 收藏店铺 | 收藏指定店铺 | POST /api/favorite/shop/{shopId} |
| FS-002 | 收藏列表 | 获取当前用户收藏的店铺列表 | GET /api/favorite/shop/list |
| FS-003 | 取消收藏 | 取消收藏指定店铺 | DELETE /api/favorite/shop/{shopId} |

#### 数据模型

```java
FavoriteShop {
    Long id;       // 主键
    Long buyerId;   // 买家ID
    Long shopId;    // 店铺ID
}
```

---

## 四、技术需求

### 4.1 框架版本要求

| 组件 | 版本 | 备注 |
|---|---|---|
| Spring Boot | 4.0.2 | 需要 Java 21+ |
| Spring Cloud | 2025.1.0 | Loom 虚拟线程支持 |
| Spring Cloud Alibaba | 2025.1.0.0 | 核心依赖 |
| Nacos Client | 3.1.1 | 服务发现与配置中心 |
| Sentinel | 1.8.9 | 限流降级 |
| Seata | 2.5.0 | 分布式事务（AT模式） |
| RocketMQ | 5.3.1 | 消息队列 |
| Sa-Token | 1.40.0 | 权限认证 |

### 4.2 中间件依赖

| 中间件 | 用途 | 部署方式 |
|---|---|---|
| PostgreSQL | 业务数据库 | K8s 主从 + Pgpool-II |
| Redis | 缓存、分布式锁、会话 | K8s 主从 + Sentinel |
| Nacos | 服务注册发现、配置中心 | K8s 单节点 |
| RocketMQ | 异步消息、事件驱动 | K8s Namesrv + Broker |
| Seata | 分布式事务协调 | K8s 单节点 |
| Sentinel Dashboard | 限流监控大屏 | K8s 单节点 |
| Zipkin | 链路追踪 | K8s 单节点 |

### 4.3 K8s 部署要求

#### 命名空间规划

| 命名空间 | 用途 |
|---|---|
| middleware | Nacos、RocketMQ、Seata、Sentinel Dashboard、Zipkin |
| database | PostgreSQL 主从、Pgpool-II |
| cache | Redis 主从、Sentinel |

#### 高可用要求

| 组件 | 副本数 | 说明 |
|---|---|---|
| Nacos | 1 | 开发环境单节点 |
| PostgreSQL Primary | 1 | 主库可写 |
| PostgreSQL Replica | 1 | 从库只读 |
| Pgpool-II | 2 | 读写分离代理 |
| Redis Master | 1 | 主库可写 |
| Redis Replica | 1 | 从库只读 |
| Redis Sentinel | 3 | 哨兵仲裁 |
| RocketMQ Namesrv | 1 | 名称服务 |
| RocketMQ Broker | 1 | 消息代理 |
| Seata Server | 1 | 事务协调 |
| Sentinel Dashboard | 1 | 监控大屏 |
| Zipkin | 1 | 链路追踪 |

---

## 五、非功能性需求

### 5.1 性能需求

| 指标 | 目标值 |
|---|---|
| API 响应时间 P99 | < 200ms |
| 并发用户数 | ≥ 1000 |
| 缓存命中率 | ≥ 60% |

### 5.2 安全需求

| 需求项 | 描述 |
|---|---|
| 密码加密 | BCrypt 单向加密 |
| Token 安全 | Sa-Token UUID Token，24小时有效期 |
| 敏感数据 | 手机号、邮箱脱敏展示 |
| SQL 注入防护 | JPA 参数化查询 |
| 接口鉴权 | 所有业务接口需登录 |

### 5.3 可观测性需求

| 需求项 | 描述 |
|---|---|
| 日志规范 | 统一 JSON 格式，包含 traceId |
| 链路追踪 | Micrometer + Zipkin 全链路追踪 |
| 指标监控 | Prometheus 格式指标暴露 |
| 健康检查 | Spring Actuator 健康端点 |

### 5.4 可靠性需求

| 需求项 | 描述 |
|---|---|
| 服务熔断 | Sentinel 熔断降级，返回兜底数据 |
| 限流保护 | Sentinel QPS 限流，返回 429 |
| 分布式事务 | Seata AT 模式保证跨服务数据一致性 |
| 消息可靠性 | RocketMQ 消息持久化 + 消费确认 |

---

## 六、服务间调用约定

### 6.1 同步调用（OpenFeign）

| 调用方 | 被调用方 | 调用场景 |
|---|---|---|
| user-service | shop-service | 查询卖家店铺列表 |
| user-service | product-service | 查询卖家商品列表 |
| shop-service | user-service | 查询卖家基本信息 |
| product-service | user-service | 查询卖家基本信息 |
| favorite-service | user-service | 查询用户/卖家信息 |
| favorite-service | product-service | 查询商品详情 |
| favorite-service | shop-service | 查询店铺详情 |

### 6.2 异步事件（RocketMQ）

| Topic | 生产者 | 消费者 | 触发场景 |
|---|---|---|---|
| user-topic | user-service | 所有服务 | 用户注册/更新 |
| shop-topic | shop-service | user-service, favorite-service | 店铺创建/更新 |
| product-topic | product-service | favorite-service | 商品创建/更新/库存变更 |
| favorite-topic | favorite-service | user-service | 收藏/取消收藏事件 |

---

## 七、数据库隔离策略

### 7.1 每个服务独立数据库

| 服务 | 数据库 | 用途 |
|---|---|---|
| auth-service, user-service | user_db | 用户体系数据 |
| shop-service | shop_db | 店铺数据 |
| product-service | product_db | 商品数据 |
| favorite-service | favorite_db | 收藏数据 |

### 7.2 分布式事务场景

| 场景 | 涉及服务 | 事务范围 |
|---|---|---|
| 创建卖家同时创建默认店铺 | user-service → shop-service | Seata 全局事务 |
| 收藏商品同步更新缓存 | favorite-service → product-service | Seata AT 模式 |

---

## 八、未来扩展（Go 侧实现参考）

本项目设计时已预留 Go 侧实现接口契约，Go 侧实现时需遵循以下约定：

### 8.1 API 兼容性

- 所有 HTTP API 需与 Spring 侧保持相同路径和响应格式
- 使用 Protobuf 定义 gRPC 接口契约
- JWT/SaaS Token 需与 Sa-Token Token 互认

### 8.2 推荐 Go 框架

| 框架 | 适用场景 |
|---|---|
| Kratos | 与 Spring Cloud Alibaba 生态对齐，Nacos 集成完善 |
| go-zero | 高性能，内置熔断限流 |

### 8.3 中间件复用

- Go 侧服务可直接连接 K8s 中部署的 PostgreSQL、Redis、RocketMQ
- Nacos、RocketMQ 等支持多语言 SDK

---

## 九、变更历史

| 版本 | 日期 | 变更内容 | 作者 |
|---|---|---|---|
| 1.0.0 | 2026-05-07 | 初始版本，定义所有功能模块和技术需求 | - |
