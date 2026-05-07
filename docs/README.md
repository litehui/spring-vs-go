# spring-vs-go

对比 Spring Cloud Alibaba 与 Go 语言系列架构的语法和设计模式。

> **当前阶段仅实现 Spring Cloud Alibaba 侧代码，Go 侧暂不编写代码。**

## 版本矩阵

| 组件 | 版本 |
|---|---|
| Spring Boot | 4.0.2 |
| Spring Cloud | 2025.1.0 |
| Spring Cloud Alibaba | 2025.1.0.0 |
| Java | 21+ |
| Nacos | 3.1.1 |
| Sentinel | 1.8.9 |
| Seata | 2.5.0 |
| RocketMQ | 5.3.1 |
| SchedulerX | 1.13.3 |

## 技术栈

| 类别 | 技术 |
|---|---|
| 权限鉴权 | Sa-Token |
| 持久层 | JPA (Hibernate) |
| API 网关 | Spring Cloud Gateway |
| 服务间调用 | OpenFeign |
| 数据库 | PostgreSQL |
| 缓存 | Redis |

## 微服务架构

| 服务名 | 端口 | 职责 | 数据库 |
|---|---|---|---|
| `gateway-service` | 8080 | Spring Cloud Gateway 网关 | 无 |
| `auth-service` | 8081 | Sa-Token 认证鉴权 | 共享 user-db |
| `user-service` | 8082 | 买家模块、卖家主账号、卖家子账号 | `user_db` |
| `shop-service` | 8083 | 店铺模块 | `shop_db` |
| `product-service` | 8084 | 商品模块 | `product_db` |
| `favorite-service` | 8085 | 收藏商品、收藏店铺 | `favorite_db` |

### 服务间调用关系

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

## K8s 基础设施

| 组件 | 部署方式 | 说明 |
|---|---|---|
| Nacos | Deployment（单节点） | 服务注册与发现、配置中心 |
| Higress | Helm + McpBridge | K8s 入口网关，对接 Nacos 服务发现 |
| PostgreSQL 主从 | Primary/Replica StatefulSet + Pgpool-II Deployment | 读写分离与自动故障转移 |
| Redis 主从哨兵 | Master/Replica StatefulSet + Sentinel StatefulSet（3 节点） | 自动故障转移 |

## 目录结构

```
/workspace/
├── docs/                                    # 项目文档
│   └── README.md                            # 项目说明
├── spring/                                  # Spring Cloud Alibaba 代码
│   ├── build.gradle                         # 根构建文件
│   ├── settings.gradle                      # 项目设置
│   ├── gradle/
│   │   └── wrapper/
│   │       ├── gradle-wrapper.jar
│   │       └── gradle-wrapper.properties
│   ├── gradlew
│   ├── common/                              # 公共模块
│   │   ├── common-core/                     # 核心工具（统一响应、异常、工具类）
│   │   ├── common-redis/                    # Redis 工具（分布式锁、缓存操作）
│   │   ├── common-log/                      # 日志工具（操作日志记录）
│   │   ├── common-security/                 # 安全工具（Sa-Token 集成）
│   │   ├── common-swagger/                  # API 文档（SpringDoc OpenAPI）
│   │   ├── common-feign/                    # Feign 工具（拦截器、降级工厂）
│   │   └── common-mq/                       # 消息队列工具（RocketMQ 封装）
│   ├── gateway/                             # Spring Cloud Gateway 服务
│   ├── auth/                                # 认证鉴权服务
│   ├── user/                                # 用户服务（买家+卖家主账号+卖家子账号）
│   ├── shop/                                # 店铺服务
│   ├── product/                             # 商品服务
│   └── favorite/                            # 收藏服务（收藏商品+收藏店铺）
├── go/                                      # Go 代码（占位，暂不实现）
│   └── .gitkeep
└── k8s/                                     # Kubernetes 部署
    ├── nacos/                               # Nacos 部署
    ├── higress/                             # Higress 网关
    ├── postgresql/                          # PostgreSQL 主从 + Pgpool-II
    │   ├── primary/
    │   ├── replica/
    │   └── pgpool/
    └── redis/                               # Redis 主从哨兵
        ├── master/
        ├── replica/
        └── sentinel/
```
