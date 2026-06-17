CREATE DATABASE IF NOT EXISTS delay_task DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE delay_task;

DROP TABLE IF EXISTS delay_task;
CREATE TABLE delay_task (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    task_name VARCHAR(128) NOT NULL COMMENT '任务名称',
    task_type VARCHAR(64) NOT NULL COMMENT '任务类型',
    task_param TEXT COMMENT '任务参数',
    task_status VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '任务状态: PENDING, PROCESSING, SUCCESS, FAILED, RETRY, CANCELLED',
    retry_count INT NOT NULL DEFAULT 0 COMMENT '已重试次数',
    max_retry_count INT NOT NULL DEFAULT 3 COMMENT '最大重试次数',
    retry_interval INT NOT NULL DEFAULT 5 COMMENT '重试间隔(秒)',
    execute_time DATETIME NOT NULL COMMENT '执行时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT 0 COMMENT '删除标记: 0-未删除, 1-已删除',
    PRIMARY KEY (id),
    INDEX idx_task_status (task_status),
    INDEX idx_execute_time (execute_time),
    INDEX idx_task_type (task_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='延迟任务表';

DROP TABLE IF EXISTS task_log;
CREATE TABLE task_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    task_id BIGINT NOT NULL COMMENT '任务ID',
    task_name VARCHAR(128) NOT NULL COMMENT '任务名称',
    execute_status VARCHAR(32) NOT NULL COMMENT '执行状态: SUCCESS, FAILED, RETRY',
    execute_result TEXT COMMENT '执行结果',
    error_msg TEXT COMMENT '错误信息',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    execute_node VARCHAR(128) COMMENT '执行节点',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    INDEX idx_task_id (task_id),
    INDEX idx_execute_status (execute_status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务执行日志表';
