# Spring Cloud Alibaba vs Go 微服务对比项目 - 实施计划

## 一、项目概述

本项目用于对比 Spring Cloud Alibaba 与 Go 语言系列架构的语法和设计模式。当前阶段仅实现 Spring Cloud Alibaba 侧代码，Go 侧暂不编写代码，仅在文档中说明项目定位为 Spring vs Go 对比项目。

### 版本矩阵

| 组件 | 版本 |
|---|---|
| Spring Boot | 4.0.0 |
| Spring Cloud | 2025.1.0 |
| Spring Cloud Alibaba | 2025.1.0.0 |
| Java | 21+ |
| Nacos Client | 3.1.1 |
| Sentinel | 1.8.9 |
| Seata | 2.5.0 |
| RocketMQ | 5.3.1 |
| SchedulerX | 1.13.3 |

### 技术选型决策

| 决策项 | 选择 | 理由 |
|---|---|---|
| Go 框架 | 暂不选择，暂不写 Go 代码 | 用户明确要求 |
| 持久层 | JPA (Hibernate) | Spring 生态标准方案 |
| 分布式定时任务 | SchedulerX | Spring Cloud Alibaba 官方组件 |
| 权限鉴权 | Sa-Token | 用户选择 |
| 微服务拆分 | 按业务域聚合为 4 个服务 | 平衡粒度与复杂度 |
| API 网关 | SCG + Higress 双层 | SCG 做微服务层路由，Higress 做 K8s 入口 |
| 数据库 | 每服务独立库 | 微服务最佳实践 |
| 消息队列 | RocketMQ | SCA 官方组件 |
| 服务间调用 | OpenFeign + RocketMQ | 每个接口都套 OpenFeign + 消息队列 |
| 构建工具 | Gradle | 用户明确要求 |

---

## 二、微服务架构设计

### 2.1 服务拆分

| 服务名 | 端口 | 包含模块 | 数据库 |
|---|---|---|---|
| `gateway-service` | 8080 | Spring Cloud Gateway 网关 | 无 |
| `auth-service` | 8081 | Sa-Token 认证鉴权 | 共享 user-db |
| `user-service` | 8082 | 买家模块、卖家主账号、卖家子账号 | `user_db` |
| `shop-service` | 8083 | 店铺模块 | `shop_db` |
| `product-service` | 8084 | 商品模块 | `product_db` |
| `favorite-service` | 8085 | 收藏商品、收藏店铺 | `favorite_db` |

### 2.2 服务间调用关系

```
客户端 → Higress(K8s入口) → Spring Cloud Gateway → 各微服务
                                                      ↓
                                              OpenFeign / RocketMQ
                                                      ↓
                                              其他微服务
```

- `user-service` → `shop-service`（查询卖家店铺信息）
- `user-service` → `product-service`（查询卖家商品信息）
- `favorite-service` → `product-service`（查询收藏的商品详情）
- `favorite-service` → `shop-service`（查询收藏的店铺详情）
- `favorite-service` → `user-service`（查询用户信息）
- `shop-service` → `user-service`（查询卖家信息）

### 2.3 Spring Cloud Alibaba 技术覆盖

| 技术点 | 实现方式 | 涉及服务 |
|---|---|---|
| 服务注册与发现 | Nacos Discovery | 所有服务 |
| 配置中心 | Nacos Config (spring.config.import) | 所有服务 |
| API 网关 | Spring Cloud Gateway + Higress | gateway-service |
| 服务间调用 | Spring Cloud OpenFeign | user/shop/product/favorite |
| 负载均衡 | Spring Cloud LoadBalancer | 所有消费端 |
| 限流降级 | Sentinel (流控+熔断+降级) | 所有服务 |
| Sentinel Dashboard | 大屏监控 | 独立部署 |
| 分布式事务 | Seata (AT 模式) | 跨服务写操作 |
| 分布式消息 | RocketMQ | 所有服务 |
| 异步服务 | @Async + RocketMQ | 所有服务 |
| 权限鉴权 | Sa-Token + Redis | auth-service + 所有服务 |
| 分布式定时任务 | SchedulerX | product-service / shop-service |
| 分布式缓存 | Redis (主从) | 所有服务 |
| 链路追踪 | Micrometer Tracing + Zipkin | 所有服务 |

---

## 三、项目目录结构

```
/workspace/
├── docs/                                    # 项目文档
│   └── README.md                            # 项目说明（Spring vs Go 对比项目定位）
├── spring/                                  # Spring Cloud Alibaba 代码
│   ├── build.gradle                         # 根构建文件
│   ├── settings.gradle                      # 项目设置
│   ├── gradle/
│   │   └── wrapper/
│   │       ├── gradle-wrapper.jar
│   │       └── gradle-wrapper.properties
│   ├── gradlew
│   ├── gradlew.bat
│   ├── common/                              # 公共模块
│   │   ├── common-core/                     # 核心工具（统一响应、异常、工具类）
│   │   ├── common-redis/                    # Redis 工具（分布式锁、缓存操作）
│   │   ├── common-log/                      # 日志工具（操作日志记录）
│   │   ├── common-security/                 # 安全工具（Sa-Token 集成）
│   │   ├── common-swagger/                  # API 文档（SpringDoc OpenAPI）
│   │   ├── common-feign/                    # Feign 工具（拦截器、降级工厂）
│   │   └── common-mq/                       # 消息队列工具（RocketMQ 封装）
│   ├── gateway/                             # Spring Cloud Gateway 服务
│   │   ├── build.gradle
│   │   └── src/
│   │       └── main/
│   │           ├── java/com/compare/gateway/
│   │           │   ├── GatewayApplication.java
│   │           │   ├── config/
│   │           │   │   ├── CorsConfig.java
│   │           │   │   └── SentinelConfig.java
│   │           │   ├── filter/
│   │           │   │   ├── AuthGlobalFilter.java
│   │           │   │   └── RequestLogFilter.java
│   │           │   └── handler/
│   │           │       └── GlobalExceptionHandler.java
│   │           └── resources/
│   │               └── application.yml
│   ├── auth/                                # 认证鉴权服务
│   │   ├── build.gradle
│   │   └── src/
│   │       └── main/
│   │           ├── java/com/compare/auth/
│   │           │   ├── AuthApplication.java
│   │           │   ├── controller/
│   │           │   │   ├── LoginController.java
│   │           │   │   └── TokenController.java
│   │           │   ├── service/
│   │           │   │   └── AuthService.java
│   │           │   └── config/
│   │           │       └── SaTokenConfig.java
│   │           └── resources/
│   │               └── application.yml
│   ├── user/                                # 用户服务（买家+卖家主账号+卖家子账号）
│   │   ├── build.gradle
│   │   └── src/
│   │       └── main/
│   │           ├── java/com/compare/user/
│   │           │   ├── UserApplication.java
│   │           │   ├── controller/
│   │           │   │   ├── BuyerController.java
│   │           │   │   ├── SellerController.java
│   │           │   │   └── SellerSubAccountController.java
│   │           │   ├── entity/
│   │           │   │   ├── Buyer.java
│   │           │   │   ├── Seller.java
│   │           │   │   └── SellerSubAccount.java
│   │           │   ├── repository/
│   │           │   │   ├── BuyerRepository.java
│   │           │   │   ├── SellerRepository.java
│   │           │   │   └── SellerSubAccountRepository.java
│   │           │   ├── service/
│   │           │   │   ├── BuyerService.java
│   │           │   │   ├── SellerService.java
│   │           │   │   └── SellerSubAccountService.java
│   │           │   ├── feign/
│   │           │   │   ├── ShopFeignClient.java
│   │           │   │   └── ProductFeignClient.java
│   │           │   ├── mq/
│   │           │   │   ├── producer/
│   │           │   │   │   └── UserEventProducer.java
│   │           │   │   └── consumer/
│   │           │   │       └── UserEventConsumer.java
│   │           │   ├── scheduler/
│   │           │   │   └── UserScheduledJobs.java
│   │           │   └── config/
│   │           │       └── SaTokenConfig.java
│   │           └── resources/
│   │               └── application.yml
│   ├── shop/                                # 店铺服务
│   │   ├── build.gradle
│   │   └── src/
│   │       └── main/
│   │           ├── java/com/compare/shop/
│   │           │   ├── ShopApplication.java
│   │           │   ├── controller/
│   │           │   │   └── ShopController.java
│   │           │   ├── entity/
│   │           │   │   └── Shop.java
│   │           │   ├── repository/
│   │           │   │   └── ShopRepository.java
│   │           │   ├── service/
│   │           │   │   └── ShopService.java
│   │           │   ├── feign/
│   │           │   │   └── UserFeignClient.java
│   │           │   ├── mq/
│   │           │   │   ├── producer/
│   │           │   │   │   └── ShopEventProducer.java
│   │           │   │   └── consumer/
│   │           │   │       └── ShopEventConsumer.java
│   │           │   └── scheduler/
│   │           │       └── ShopScheduledJobs.java
│   │           └── resources/
│   │               └── application.yml
│   ├── product/                             # 商品服务
│   │   ├── build.gradle
│   │   └── src/
│   │       └── main/
│   │           ├── java/com/compare/product/
│   │           │   ├── ProductApplication.java
│   │           │   ├── controller/
│   │           │   │   └── ProductController.java
│   │           │   ├── entity/
│   │           │   │   └── Product.java
│   │           │   ├── repository/
│   │           │   │   └── ProductRepository.java
│   │           │   ├── service/
│   │           │   │   └── ProductService.java
│   │           │   ├── feign/
│   │           │   │   └── UserFeignClient.java
│   │           │   ├── mq/
│   │           │   │   ├── producer/
│   │           │   │   │   └── ProductEventProducer.java
│   │           │   │   └── consumer/
│   │           │   │       └── ProductEventConsumer.java
│   │           │   └── scheduler/
│   │           │       └── ProductScheduledJobs.java
│   │           └── resources/
│   │               └── application.yml
│   └── favorite/                            # 收藏服务（收藏商品+收藏店铺）
│       ├── build.gradle
│       └── src/
│           └── main/
│               ├── java/com/compare/favorite/
│               │   ├── FavoriteApplication.java
│               │   ├── controller/
│               │   │   ├── FavoriteProductController.java
│               │   │   └── FavoriteShopController.java
│               │   ├── entity/
│               │   │   ├── FavoriteProduct.java
│               │   │   └── FavoriteShop.java
│               │   ├── repository/
│               │   │   ├── FavoriteProductRepository.java
│               │   │   └── FavoriteShopRepository.java
│               │   ├── service/
│               │   │   ├── FavoriteProductService.java
│               │   │   └── FavoriteShopService.java
│               │   ├── feign/
│               │   │   ├── UserFeignClient.java
│               │   │   ├── ProductFeignClient.java
│               │   │   └── ShopFeignClient.java
│               │   └── mq/
│               │       ├── producer/
│               │       │   └── FavoriteEventProducer.java
│               │       └── consumer/
│               │           └── FavoriteEventConsumer.java
│               └── resources/
│                   └── application.yml
├── go/                                      # Go 代码（占位，暂不实现）
│   └── .gitkeep
└── k8s/                                     # Kubernetes 部署
    ├── nacos/
    │   ├── deployment.yaml
    │   ├── service.yaml
    │   └── README.md
    ├── higress/
    │   ├── values.yaml
    │   ├── mcpbridge.yaml
    │   └── ingress.yaml
    ├── postgresql/
    │   ├── primary/
    │   │   ├── statefulset.yaml
    │   │   ├── service.yaml
    │   │   └── configmap.yaml
    │   ├── replica/
    │   │   ├── statefulset.yaml
    │   │   ├── service.yaml
    │   │   └── configmap.yaml
    │   └── pgpool/
    │       ├── deployment.yaml
    │       └── service.yaml
    └── redis/
        ├── master/
        │   ├── statefulset.yaml
        │   └── service.yaml
        ├── replica/
        │   ├── statefulset.yaml
        │   └── service.yaml
        └── sentinel/
            ├── statefulset.yaml
            └── service.yaml
```

---

## 四、数据库设计

### 4.1 user_db

```sql
-- 买家表
CREATE TABLE buyer (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    avatar VARCHAR(500),
    status SMALLINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 卖家主账号表
CREATE TABLE seller (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    company_name VARCHAR(200),
    contact_name VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    business_license VARCHAR(100),
    status SMALLINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 卖家子账号表
CREATE TABLE seller_sub_account (
    id BIGSERIAL PRIMARY KEY,
    seller_id BIGINT NOT NULL REFERENCES seller(id),
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(100),
    role VARCHAR(50) DEFAULT 'operator',
    permissions TEXT[],
    status SMALLINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 4.2 shop_db

```sql
-- 店铺表
CREATE TABLE shop (
    id BIGSERIAL PRIMARY KEY,
    seller_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    logo VARCHAR(500),
    category VARCHAR(100),
    status SMALLINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 4.3 product_db

```sql
-- 商品表
CREATE TABLE product (
    id BIGSERIAL PRIMARY KEY,
    shop_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    category VARCHAR(100),
    images TEXT[],
    status SMALLINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 4.4 favorite_db

```sql
-- 收藏商品表
CREATE TABLE favorite_product (
    id BIGSERIAL PRIMARY KEY,
    buyer_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(buyer_id, product_id)
);

-- 收藏店铺表
CREATE TABLE favorite_shop (
    id BIGSERIAL PRIMARY KEY,
    buyer_id BIGINT NOT NULL,
    shop_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(buyer_id, shop_id)
);
```

---

## 五、实施步骤

### 阶段一：项目骨架与基础设施 (K8s)

**步骤 1.1：初始化 Gradle 多项目结构**
- 创建根 `build.gradle` 和 `settings.gradle`
- 配置 Spring Boot 4.0.0 + Spring Cloud 2025.1.0 + SCA 2025.1.0.0 依赖管理
- 创建所有子模块的 `build.gradle`
- 配置 Gradle Wrapper

**步骤 1.2：创建 K8s 部署文件**
- Nacos：Deployment + Service（单节点，用于开发）
- Higress：Helm values + McpBridge + Ingress 路由
- PostgreSQL 主从：Primary StatefulSet + Replica StatefulSet + Pgpool-II Deployment
- Redis 主从哨兵：Master StatefulSet + Replica StatefulSet + Sentinel StatefulSet

**步骤 1.3：创建 docs/README.md**
- 说明项目定位为 Spring Cloud Alibaba vs Go 微服务架构对比
- 记录技术栈、版本矩阵、架构设计

### 阶段二：公共模块

**步骤 2.1：common-core**
- 统一响应体 `R<T>`（code, message, data）
- 全局异常处理器 `GlobalExceptionHandler`
- 自定义业务异常 `BusinessException`
- 分页请求/响应封装
- 工具类集合

**步骤 2.2：common-redis**
- RedisTemplate 配置
- 分布式锁工具 `RedisLockUtil`
- 缓存操作工具 `CacheUtil`

**步骤 2.3：common-security**
- Sa-Token 集成配置
- 权限校验注解封装
- 登录/鉴权拦截器
- 用户上下文工具 `SecurityContextUtil`

**步骤 2.4：common-feign**
- Feign 拦截器（传递 Sa-Token、TraceId）
- 通用降级工厂 `CommonFallbackFactory`
- Feign 日志配置

**步骤 2.5：common-mq**
- RocketMQ 消息生产者封装
- RocketMQ 消息消费者封装
- 消息轨迹配置
- 消息重试策略

**步骤 2.6：common-log**
- 操作日志注解 `@OperLog`
- 日志切面 `OperLogAspect`

**步骤 2.7：common-swagger**
- SpringDoc OpenAPI 配置
- 统一 API 文档分组

### 阶段三：网关与认证服务

**步骤 3.1：gateway-service**
- Spring Cloud Gateway 配置（路由规则）
- 跨域配置
- Sentinel 集成（网关限流规则）
- 全局认证过滤器（Sa-Token 网关鉴权）
- 请求日志过滤器

**步骤 3.2：auth-service**
- 买家登录/注册
- 卖家登录/注册
- 卖家子账号登录
- Token 管理（Sa-Token SSO 模式）
- 权限角色管理

### 阶段四：业务微服务

**步骤 4.1：user-service**
- 买家 CRUD + 个人信息管理
- 卖家主账号 CRUD + 企业信息管理
- 卖家子账号 CRUD + 权限分配
- Feign 调用：查询卖家店铺、查询卖家商品
- RocketMQ：用户注册事件、用户信息变更事件
- SchedulerX：定时清理过期子账号
- Seata：跨服务事务（创建卖家同时创建默认店铺）

**步骤 4.2：shop-service**
- 店铺 CRUD
- 店铺分类管理
- Feign 调用：查询卖家信息
- RocketMQ：店铺创建/更新事件
- SchedulerX：定时统计店铺数据
- Sentinel：店铺详情接口限流

**步骤 4.3：product-service**
- 商品 CRUD
- 商品库存管理
- 商品搜索（按分类/关键词）
- Feign 调用：查询卖家信息
- RocketMQ：商品创建/更新/库存变更事件
- SchedulerX：定时同步商品数据
- Sentinel：商品列表接口限流 + 熔断降级

**步骤 4.4：favorite-service**
- 收藏商品 CRUD
- 收藏店铺 CRUD
- 收藏列表（分页）
- Feign 调用：查询用户信息、商品详情、店铺详情
- RocketMQ：收藏/取消收藏事件
- Seata：收藏操作跨服务一致性
- Sentinel：收藏接口限流

### 阶段五：配置与治理

**步骤 5.1：Nacos 配置**
- 各服务 `application.yml` 配置
- Nacos 共享配置（`shared-configs`）
- `spring.config.import` 方式加载 Nacos 配置

**步骤 5.2：Sentinel 规则**
- 流控规则（QPS 限流）
- 熔断规则（慢调用比例、异常比例）
- 降级规则（返回默认数据）
- 热点参数限流
- 网关限流规则
- Sentinel Dashboard 部署配置

**步骤 5.3：Seata 配置**
- AT 模式配置
- 全局事务注解 `@GlobalTransactional`
- undo_log 表创建

**步骤 5.4：RocketMQ 配置**
- Topic 与 Consumer Group 规划
- 消息生产者/消费者配置
- 顺序消息、延迟消息、事务消息示例

**步骤 5.5：链路追踪**
- Micrometer Tracing + Zipkin 集成
- 采样率配置

---

## 六、K8s 部署详细设计

### 6.1 Nacos

```yaml
# 单节点部署，用于开发环境
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nacos
  namespace: middleware
spec:
  replicas: 1
  template:
    spec:
      containers:
      - name: nacos
        image: nacos/nacos-server:v3.1.1
        ports:
        - containerPort: 8848
        - containerPort: 9848
        env:
        - name: MODE
          value: standalone
        - name: SPRING_DATASOURCE_PLATFORM
          value: postgresql
        - name: NACOS_AUTH_ENABLE
          value: "true"
```

### 6.2 Higress

```yaml
# Helm values + McpBridge 对接 Nacos
# Ingress 路由到 Spring Cloud Gateway
apiVersion: networking.higress.io/v1
kind: McpBridge
metadata:
  name: default
  namespace: higress-system
spec:
  registries:
  - name: nacos
    type: nacos2
    domain: nacos.middleware.svc.cluster.local
    port: 8848
    nacosGroups:
    - DEFAULT_GROUP
```

### 6.3 PostgreSQL 主从

- Primary：1 个 StatefulSet，可写
- Replica：1 个 StatefulSet，流复制只读
- Pgpool-II：Deployment，自动路由读写请求
- 通过 Pgpool-II 实现读写分离和自动故障转移

### 6.4 Redis 主从哨兵

- Master：1 个 StatefulSet
- Replica：1 个 StatefulSet
- Sentinel：3 个 StatefulSet（仲裁用）
- 通过 Sentinel 实现自动故障转移

---

## 七、Gradle 依赖管理核心配置

```groovy
// 根 build.gradle 关键配置
plugins {
    id 'java'
    id 'org.springframework.boot' version '4.0.0' apply false
    id 'io.spring.dependency-management' version '1.1.7' apply false
}

ext {
    springCloudVersion = '2025.1.0'
    springCloudAlibabaVersion = '2025.1.0.0'
    saTokenVersion = '1.40.0'
    springdocVersion = '2.8.0'
}

subprojects {
    apply plugin: 'java'
    apply plugin: 'org.springframework.boot'
    apply plugin: 'io.spring.dependency-management'

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    dependencyManagement {
        imports {
            mavenBom "org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}"
            mavenBom "com.alibaba.cloud:spring-cloud-alibaba-dependencies:${springCloudAlibabaVersion}"
        }
    }
}
```

---

## 八、假设与约束

1. **Java 21+**：Spring Boot 4.0 最低要求 Java 21
2. **Jakarta EE 10**：所有 `javax.*` 已替换为 `jakarta.*`
3. **Nacos 配置加载**：使用 `spring.config.import` 而非 bootstrap（SCA 2025 移除了 bootstrap 支持）
4. **SchedulerX**：需要阿里云账号配置，在代码中预留配置入口
5. **K8s 环境**：假设使用 kind 或 minikube 本地集群进行开发测试
6. **Go 侧**：仅创建 `go/` 目录占位，暂不编写代码
7. **Sentinel Dashboard**：需要单独部署，用于大屏监控
8. **每个接口都套 OpenFeign + 消息队列**：服务间同步调用走 Feign，异步事件走 RocketMQ

---

## 九、验证步骤

1. **Gradle 构建**：`./gradlew build` 全项目编译通过
2. **K8s 基础设施**：`kubectl get pods -n middleware` 确认 Nacos/PG/Redis 就绪
3. **服务注册**：启动各服务后，Nacos 控制台确认服务注册成功
4. **网关路由**：通过 Higress → SCG 访问各服务 API
5. **认证鉴权**：未登录请求被拦截，登录后可正常访问
6. **Feign 调用**：服务间调用正常，TraceId 透传
7. **RocketMQ**：消息生产消费正常
8. **Sentinel**：限流规则生效，Dashboard 可监控
9. **Seata**：分布式事务回滚正常
10. **SchedulerX**：定时任务按计划执行
11. **PG 主从**：写入走 Primary，读取走 Replica
12. **Redis 哨兵**：Master 故障后自动切换
