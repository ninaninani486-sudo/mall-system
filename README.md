# mall-system 商城系统

Spring Boot + Vue3 前后端分离商城系统，支持用户登录、商品浏览、购物车、订单管理、余额/微信/支付宝支付（模拟）、RabbitMQ 延迟队列自动取消超时订单。

## 技术栈

### 后端

- **Spring Boot 2.7.5** - 核心框架
- **MyBatis Plus 3.5.2** - ORM 框架
- **MySQL 8.0** - 数据库
- **Redis** - 缓存 + 分布式锁
- **RabbitMQ** - 消息队列（延迟任务）
- **Redisson** - Redis 分布式锁客户端
- **SpringDoc OpenAPI** - API 文档
- **JWT** - 用户认证
- **Druid** - 数据库连接池

### 前端

- **Vue 3** - 前端框架
- **Element Plus** - UI 组件库
- **Pinia** - 状态管理
- **Vue Router** - 路由管理
- **Axios** - HTTP 请求
- **Vite** - 构建工具

## 核心功能

| 模块 | 功能 |
|------|------|
| **用户** | 注册、登录、JWT 鉴权 |
| **商品** | 商品列表、分类筛选、关键词搜索、商品详情 |
| **购物车** | 添加/删除商品、修改数量、购物车列表 |
| **订单** | 创建订单、订单列表、订单详情、取消订单 |
| **支付** | 余额支付、微信支付（模拟）、支付宝支付（模拟） |
| **账户** | 余额查询、充值、交易流水 |
| **延迟任务** | 超时订单自动取消、任务重试、执行日志 |

## 项目结构

```
mall-system/
├── src/main/java/com/delaytask/
│   ├── config/               # 配置类（Redis、RabbitMQ、MyBatis-Plus等）
│   ├── controller/           # 控制器
│   │   ├── UserController        # 用户接口
│   │   ├── ProductController     # 商品接口
│   │   ├── CartController        # 购物车接口
│   │   ├── OrderController       # 订单接口
│   │   ├── PaymentController     # 支付接口
│   │   ├── AccountController     # 账户接口
│   │   ├── TaskController        # 延迟任务接口
│   │   └── ImageProxyController  # 图片代理
│   ├── dto/                  # 数据传输对象
│   ├── entity/               # 实体类
│   ├── mapper/               # MyBatis-Plus Mapper
│   ├── mq/                   # RabbitMQ 延迟队列
│   ├── redis/                # Redis/Redisson 操作
│   ├── service/              # 业务逻辑层
│   └── util/                 # 工具类
├── frontend/                 # Vue 3 前端
│   └── src/
│       ├── api/              # API 请求
│       ├── views/            # 页面
│       ├── router/           # 路由
│       ├── store/            # 状态管理
│       └── components/       # 公共组件
├── docs/                     # SQL 脚本
├── deploy/                   # Docker 部署
└── stress_test.py            # 压测脚本
```

## 快速开始

### 1. 环境准备

- JDK 1.8+
- MySQL 8.0+
- Redis 7.0+
- RabbitMQ 3.9+
- Node.js 16+

### 2. 数据库初始化

```bash
mysql -u root -p < docs/mall_schema.sql
```

### 3. 启动后端

```bash
mvn clean package -DskipTests
java -jar target/delay-task-platform-1.0.0.jar
```

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

### 5. Docker 部署

```bash
cd deploy
docker-compose up -d
```

## API 接口

### 用户

```
POST  /api/user/login      # 登录
POST  /api/user/register   # 注册
GET   /api/user/info       # 用户信息
```

### 商品

```
GET   /api/product/list        # 商品列表（支持分页、关键词、分类）
GET   /api/product/detail/{id} # 商品详情
```

### 购物车

```
GET   /api/cart/list        # 购物车列表
POST  /api/cart/add         # 添加商品
POST  /api/cart/update      # 修改数量
POST  /api/cart/remove      # 移除商品
```

### 订单

```
POST  /api/order/create           # 创建订单
GET   /api/order/detail/{orderNo} # 订单详情
GET   /api/order/list             # 订单列表
POST  /api/order/cancel/{orderNo} # 取消订单
```

### 支付

```
POST  /api/payment/pay           # 支付
POST  /api/payment/wechat/notify # 微信支付回调
POST  /api/payment/alipay/notify # 支付宝支付回调
```

### 账户

```
GET   /api/account/balance       # 查询余额
POST  /api/account/recharge      # 充值
GET   /api/account/transactions  # 交易流水
```

### 延迟任务

```
POST  /api/task/create          # 创建延迟任务
POST  /api/task/cancel/{taskId} # 取消任务
GET   /api/task/list            # 任务列表
GET   /api/task/log/{taskId}    # 任务日志
```

## Docker 部署

```bash
cd deploy
docker-compose up -d
```

### 脚本管理

```bash
chmod +x deploy/app.sh
./deploy/app.sh start   # 启动
./deploy/app.sh stop    # 停止
./deploy/app.sh status  # 状态
./deploy/app.sh logs    # 日志
./deploy/app.sh health  # 健康检查
```
