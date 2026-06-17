# 分布式延迟任务调度平台

## 项目简介

基于Spring Boot + Redis + RabbitMQ实现的分布式延迟任务调度系统，支持任务创建、执行、重试、取消等功能。

## 技术栈

- **Spring Boot 2.7.5** - 核心框架
- **MyBatis Plus 3.5.2** - ORM框架
- **MySQL 8.0** - 数据库
- **Redis** - 缓存 + 分布式锁
- **RabbitMQ** - 消息队列
- **Redisson** - Redis分布式锁客户端
- **Knife4j** - API文档

## 核心功能

1. **任务管理** - 创建、查询、取消延迟任务
2. **分布式锁** - 使用Redisson实现分布式锁，防止任务重复执行
3. **消息队列** - 使用RabbitMQ实现异步任务处理
4. **任务重试** - 支持自动重试机制
5. **执行日志** - 记录任务执行过程和结果

## 项目结构

```
delay-task-platform/
├── src/main/java/com/delaytask/
│   ├── config/          # 配置类
│   ├── controller/      # 控制器
│   ├── entity/          # 实体类
│   ├── mapper/          # 数据访问层
│   ├── mq/             # RabbitMQ相关
│   ├── redis/          # Redis相关
│   ├── service/        # 业务逻辑层
│   └── util/           # 工具类
├── src/main/resources/
│   └── application.yml # 配置文件
├── docs/               # 文档
└── deploy/             # 部署脚本
```

## 快速开始

### 1. 环境准备

- JDK 1.8+
- MySQL 8.0+
- Redis 7.0+
- RabbitMQ 3.9+

### 2. 数据库初始化

```bash
mysql -u root -p < docs/schema.sql
```

### 3. 启动应用

```bash
mvn clean package
java -jar target/delay-task-platform-1.0.0.jar
```

### 4. Docker部署

```bash
cd deploy
docker-compose up -d
```

## API接口

### 创建延迟任务

```bash
POST /api/task/create
参数:
  - taskName: 任务名称
  - taskType: 任务类型 (ORDER_TIMEOUT, SMS_REMINDER, DATA_CLEANUP)
  - taskParam: 任务参数
  - executeTime: 执行时间 (yyyy-MM-dd HH:mm:ss)
```

### 取消任务

```bash
POST /api/task/cancel/{taskId}
```

### 查询任务列表

```bash
GET /api/task/list?page=1&size=10
```

### 查询任务日志

```bash
GET /api/task/log/{taskId}
```

## Linux运维

### 脚本使用

```bash
# 赋予执行权限
chmod +x deploy/app.sh

# 启动应用
./deploy/app.sh start

# 停止应用
./deploy/app.sh stop

# 查看状态
./deploy/app.sh status

# 查看日志
./deploy/app.sh logs

# 健康检查
./deploy/app.sh health

# 检查环境
./deploy/app.sh check
```

### 常用Linux命令

```bash
# 查看端口占用
netstat -tlnp | grep 8080

# 查看进程
ps -ef | grep delay-task

# 查看日志
tail -f /opt/logs/delay-task-platform/startup.log

# 查看Redis状态
redis-cli info memory

# 查看RabbitMQ队列
rabbitmqctl list_queues
```

## 简历描述建议

在简历中可以这样描述这个项目：

> **分布式延迟任务调度平台**
> - 基于Spring Boot构建的分布式任务调度系统，支持延迟任务的创建、执行、重试和取消
> - 使用Redis实现分布式锁，确保任务在分布式环境下的幂等性，避免重复执行
> - 集成RabbitMQ实现异步消息处理，通过死信队列实现任务重试机制
> - 编写Shell脚本实现应用的自动化部署和运维，包括环境检查、健康监控等功能
> - 使用Docker Compose实现一键部署，包含MySQL、Redis、RabbitMQ等中间件

## 扩展功能

1. **任务优先级** - 支持不同优先级的任务
2. **任务依赖** - 支持任务之间的依赖关系
3. **任务分片** - 支持大任务的分片处理
4. **监控面板** - 集成Prometheus + Grafana监控
5. **告警通知** - 任务失败时发送告警通知
