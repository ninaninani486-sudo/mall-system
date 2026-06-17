#!/bin/bash

APP_NAME="delay-task-platform"
APP_VERSION="1.0.0"
JAR_FILE="${APP_NAME}-${APP_VERSION}.jar"
LOG_DIR="/opt/logs/${APP_NAME}"
PID_FILE="/var/run/${APP_NAME}.pid"

# 检查Java环境
check_java() {
    if ! command -v java &> /dev/null; then
        echo "错误: 未找到Java环境"
        exit 1
    fi
    echo "Java版本: $(java -version 2>&1 | head -n 1)"
}

# 检查Redis
check_redis() {
    if ! command -v redis-cli &> /dev/null; then
        echo "警告: 未找到redis-cli"
        return 1
    fi
    if redis-cli ping | grep -q "PONG"; then
        echo "Redis连接正常"
        return 0
    else
        echo "Redis连接失败"
        return 1
    fi
}

# 检查RabbitMQ
check_rabbitmq() {
    if ! command -v rabbitmqctl &> /dev/null; then
        echo "警告: 未找到rabbitmqctl"
        return 1
    fi
    if rabbitmqctl status &> /dev/null; then
        echo "RabbitMQ运行正常"
        return 0
    else
        echo "RabbitMQ未运行"
        return 1
    fi
}

# 创建日志目录
init_log_dir() {
    mkdir -p ${LOG_DIR}
    chmod 755 ${LOG_DIR}
}

# 启动应用
start() {
    check_java
    init_log_dir

    if [ -f ${PID_FILE} ]; then
        PID=$(cat ${PID_FILE})
        if ps -p ${PID} > /dev/null 2>&1; then
            echo "${APP_NAME}已经在运行 (PID: ${PID})"
            return 1
        fi
    fi

    echo "正在启动 ${APP_NAME}..."
    nohup java -jar ${JAR_FILE} \
        --spring.profiles.active=prod \
        > ${LOG_DIR}/startup.log 2>&1 &

    echo $! > ${PID_FILE}
    echo "${APP_NAME}启动成功 (PID: $!)"
    echo "日志文件: ${LOG_DIR}/startup.log"
}

# 停止应用
stop() {
    if [ ! -f ${PID_FILE} ]; then
        echo "${APP_NAME}未运行"
        return 1
    fi

    PID=$(cat ${PID_FILE})
    if ps -p ${PID} > /dev/null 2>&1; then
        echo "正在停止 ${APP_NAME} (PID: ${PID})..."
        kill ${PID}
        sleep 5

        if ps -p ${PID} > /dev/null 2>&1; then
            echo "强制停止 ${APP_NAME}..."
            kill -9 ${PID}
        fi

        rm -f ${PID_FILE}
        echo "${APP_NAME}已停止"
    else
        rm -f ${PID_FILE}
        echo "${APP_NAME}未运行"
    fi
}

# 重启应用
restart() {
    stop
    sleep 2
    start
}

# 查看状态
status() {
    if [ -f ${PID_FILE} ]; then
        PID=$(cat ${PID_FILE})
        if ps -p ${PID} > /dev/null 2>&1; then
            echo "${APP_NAME}正在运行 (PID: ${PID})"
            echo "内存使用: $(ps -p ${PID} -o %mem | tail -1)%"
            echo "CPU使用: $(ps -p ${PID} -o %cpu | tail -1)%"
        else
            echo "${APP_NAME}已停止 (PID文件存在但进程不存在)"
        fi
    else
        echo "${APP_NAME}未运行"
    fi
}

# 查看日志
logs() {
    if [ -f ${LOG_DIR}/startup.log ]; then
        tail -f ${LOG_DIR}/startup.log
    else
        echo "日志文件不存在"
    fi
}

# 健康检查
health_check() {
    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/task/list 2>/dev/null)
    if [ "${HTTP_CODE}" = "200" ]; then
        echo "应用健康检查通过"
        return 0
    else
        echo "应用健康检查失败 (HTTP状态码: ${HTTP_CODE})"
        return 1
    fi
}

# 显示帮助
show_help() {
    echo "用法: $0 {start|stop|restart|status|logs|health|check}"
    echo ""
    echo "命令:"
    echo "  start    启动应用"
    echo "  stop     停止应用"
    echo "  restart  重启应用"
    echo "  status   查看状态"
    echo "  logs     查看日志"
    echo "  health   健康检查"
    echo "  check    检查环境"
}

case "$1" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        restart
        ;;
    status)
        status
        ;;
    logs)
        logs
        ;;
    health)
        health_check
        ;;
    check)
        check_java
        check_redis
        check_rabbitmq
        ;;
    *)
        show_help
        exit 1
        ;;
esac
