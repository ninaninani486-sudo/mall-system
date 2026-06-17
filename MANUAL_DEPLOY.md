# 手动部署指南

## 一、环境准备

### 1. 安装JDK 1.8

下载地址：https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html

安装后配置环境变量：
```
JAVA_HOME = C:\Program Files\Java\jdk1.8.0_xxx
PATH = %JAVA_HOME%\bin
```

验证：
```bash
java -version
```

### 2. 安装Maven

下载地址：https://maven.apache.org/download.cgi

解压后配置环境变量：
```
MAVEN_HOME = D:\apache-maven-3.8.x
PATH = %MAVEN_HOME%\bin
```

验证：
```bash
mvn -version
```

### 3. 安装MySQL 8.0

下载地址：https://dev.mysql.com/downloads/installer/

安装时设置root密码为：root

### 4. 安装Redis

下载地址：https://github.com/tporadowski/redis/releases

解压后运行：
```bash
redis-server
```

### 5. 安装RabbitMQ

下载地址：https://www.rabbitmq.com/install-windows.html

安装后启动RabbitMQ服务。

---

## 二、初始化数据库

打开CMD或PowerShell，执行：

```bash
mysql -u root -p < D:\Projects\delay-task-platform\docs\schema.sql
```

输入密码：root

---

## 三、编译运行项目

### 1. 编译项目

```bash
cd D:\Projects\delay-task-platform
mvn clean package -DskipTests
```

### 2. 运行项目

```bash
java -jar target/delay-task-platform-1.0.0.jar
```

---

## 四、验证

浏览器访问：
- API文档：http://localhost:8080/doc.html
- 任务列表：http://localhost:8080/api/task/list

---

## 五、常见问题

### MySQL连接失败
检查MySQL服务是否启动，密码是否正确

### Redis连接失败
检查Redis服务是否启动，默认端口6379

### RabbitMQ连接失败
检查RabbitMQ服务是否启动，默认端口5672

### 端口被占用
```bash
netstat -ano | findstr :8080
taskkill /PID <进程ID> /F
```
