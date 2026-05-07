# Spring Cloud Alibaba vs Go 微服务对比项目

本项目用于对比 **Spring Cloud Alibaba** 与 **Go 语言系列架构**的语法和设计模式。

## 项目定位

当前阶段仅实现 Spring Cloud Alibaba 侧代码，Go 侧代码暂不实现。

## 技术栈版本

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

## 微服务架构

| 服务 | 端口 | 职责 |
|---|---|---|
| gateway-service | 8080 | Spring Cloud Gateway 网关 |
| auth-service | 8081 | 认证鉴权服务 |
| user-service | 8082 | 买家、卖家、卖家子账号 |
| shop-service | 8083 | 店铺管理 |
| product-service | 8084 | 商品管理 |
| favorite-service | 8085 | 收藏商品、收藏店铺 |

## 技术覆盖

Nacos · Sentinel · Seata · RocketMQ · SchedulerX · Sa-Token · OpenFeign · Redis · PostgreSQL · Zipkin

## 文档

- [需求与功能规格](docs/requirements.md) - 完整的业务需求和技术规格

## 目录结构

```
spring/          Spring Cloud Alibaba 代码（Gradle 多项目）
k8s/            Kubernetes 部署配置
go/             Go 代码（占位）
docs/           项目文档
```
