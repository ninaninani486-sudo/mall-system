-- ================================================
-- 商城系统完整数据库设计
-- ================================================

CREATE DATABASE IF NOT EXISTS mall_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE mall_system;

-- 1. 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(64) NOT NULL COMMENT '用户名',
    `password` VARCHAR(128) NOT NULL COMMENT '密码(BCrypt加密)',
    `nickname` VARCHAR(64) DEFAULT '' COMMENT '昵称',
    `avatar` VARCHAR(256) DEFAULT '' COMMENT '头像',
    `phone` VARCHAR(20) DEFAULT '' COMMENT '手机号',
    `email` VARCHAR(64) DEFAULT '' COMMENT '邮箱',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 用户账户表(余额)
DROP TABLE IF EXISTS `user_account`;
CREATE TABLE `user_account` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '账户ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `balance` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '账户余额',
    `frozen_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '冻结金额',
    `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户账户表';

-- 3. 商品表
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `name` VARCHAR(128) NOT NULL COMMENT '商品名称',
    `description` TEXT COMMENT '商品描述',
    `price` DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    `stock` INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    `sales` INT NOT NULL DEFAULT 0 COMMENT '销量',
    `image` VARCHAR(256) DEFAULT '' COMMENT '商品图片',
    `category` VARCHAR(64) DEFAULT '' COMMENT '分类',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-下架, 1-上架',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 4. 购物车表
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
    `checked` TINYINT NOT NULL DEFAULT 1 COMMENT '是否选中: 0-未选中, 1-选中',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 5. 订单表
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(64) NOT NULL COMMENT '订单编号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    `pay_amount` DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    `status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '订单状态: PENDING/PAID/SHIPPED/COMPLETED/CANCELLED/REFUNDED',
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `pay_type` VARCHAR(32) DEFAULT '' COMMENT '支付方式: BALANCE/WECHAT/ALIPAY',
    `address` VARCHAR(256) DEFAULT '' COMMENT '收货地址',
    `remark` VARCHAR(256) DEFAULT '' COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 6. 订单商品表
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单商品ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `product_name` VARCHAR(128) NOT NULL COMMENT '商品名称(冗余)',
    `product_image` VARCHAR(256) DEFAULT '' COMMENT '商品图片(冗余)',
    `price` DECIMAL(10,2) NOT NULL COMMENT '商品单价',
    `quantity` INT NOT NULL COMMENT '数量',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '小计金额',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品表';

-- 7. 支付记录表
DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '支付ID',
    `payment_no` VARCHAR(64) NOT NULL COMMENT '支付流水号',
    `order_no` VARCHAR(64) NOT NULL COMMENT '订单编号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    `pay_type` VARCHAR(32) NOT NULL COMMENT '支付方式: BALANCE/WECHAT/ALIPAY',
    `status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING/SUCCESS/FAILED/REFUNDED',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_payment_no` (`payment_no`),
    KEY `idx_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- 8. 账户流水表
DROP TABLE IF EXISTS `account_transaction`;
CREATE TABLE `account_transaction` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '流水ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `type` VARCHAR(32) NOT NULL COMMENT '类型: RECHARGE/PAY/REFUND',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '交易金额',
    `balance_before` DECIMAL(10,2) NOT NULL COMMENT '交易前余额',
    `balance_after` DECIMAL(10,2) NOT NULL COMMENT '交易后余额',
    `order_no` VARCHAR(64) DEFAULT '' COMMENT '关联订单号',
    `remark` VARCHAR(256) DEFAULT '' COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户流水表';

-- 9. 延迟任务表(保留原有)
DROP TABLE IF EXISTS `delay_task`;
CREATE TABLE `delay_task` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    `task_name` VARCHAR(128) NOT NULL COMMENT '任务名称',
    `task_type` VARCHAR(64) NOT NULL COMMENT '任务类型',
    `task_param` TEXT COMMENT '任务参数',
    `task_status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '任务状态',
    `retry_count` INT NOT NULL DEFAULT 0 COMMENT '已重试次数',
    `max_retry_count` INT NOT NULL DEFAULT 3 COMMENT '最大重试次数',
    `retry_interval` INT NOT NULL DEFAULT 5 COMMENT '重试间隔(秒)',
    `execute_time` DATETIME NOT NULL COMMENT '执行时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` INT NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_task_status` (`task_status`),
    KEY `idx_execute_time` (`execute_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='延迟任务表';

-- 10. 任务执行日志表
DROP TABLE IF EXISTS `task_log`;
CREATE TABLE `task_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `task_id` BIGINT NOT NULL COMMENT '任务ID',
    `task_name` VARCHAR(128) NOT NULL COMMENT '任务名称',
    `execute_status` VARCHAR(32) NOT NULL DEFAULT 'PROCESSING' COMMENT '执行状态',
    `execute_result` TEXT COMMENT '执行结果',
    `error_msg` TEXT COMMENT '错误信息',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME COMMENT '结束时间',
    `execute_node` VARCHAR(128) COMMENT '执行节点',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务执行日志表';

-- ================================================
-- 初始化数据
-- ================================================

-- 插入测试用户 (密码: 123456, MD5加密)
INSERT INTO `user` (`username`, `password`, `nickname`, `phone`) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', '管理员', '13800138000'),
('zhangsan', 'e10adc3949ba59abbe56e057f20f883e', '张三', '13800138001');

-- 插入用户账户
INSERT INTO `user_account` (`user_id`, `balance`, `frozen_amount`) VALUES
(1, 10000.00, 0.00),
(2, 5000.00, 0.00);

-- 插入测试商品
INSERT INTO `product` (`name`, `description`, `price`, `stock`, `sales`, `image`, `category`) VALUES
('iPhone 15 Pro', 'Apple最新旗舰手机，A17 Pro芯片，钛金属设计', 8999.00, 100, 50, 'https://via.placeholder.com/300', '手机'),
('MacBook Pro 14', 'M3 Pro芯片，14英寸Liquid Retina XDR显示屏', 14999.00, 50, 30, 'https://via.placeholder.com/300', '电脑'),
('AirPods Pro 2', '自适应音频，USB-C充电，主动降噪', 1899.00, 200, 100, 'https://via.placeholder.com/300', '配件'),
('iPad Air', 'M1芯片，10.9英寸Liquid Retina显示屏', 4799.00, 80, 40, 'https://via.placeholder.com/300', '平板'),
('Apple Watch Ultra 2', '钛金属表壳，双频GPS，水深仪', 6499.00, 60, 25, 'https://via.placeholder.com/300', '手表'),
('小米14', '骁龙8 Gen3，徕卡光学镜头', 3999.00, 150, 80, 'https://via.placeholder.com/300', '手机'),
('华为Mate60', '麒麟9000S，卫星通话', 5999.00, 120, 60, 'https://via.placeholder.com/300', '手机'),
('联想ThinkPad X1', '14英寸轻薄本，Intel 13代酷睿', 9999.00, 40, 20, 'https://via.placeholder.com/300', '电脑'),
('索尼WH-1000XM5', '头戴式降噪耳机，30小时续航', 2499.00, 100, 70, 'https://via.placeholder.com/300', '配件'),
('Switch OLED', '7英寸OLED屏幕，增强版掌机', 2399.00, 90, 55, 'https://via.placeholder.com/300', '游戏机');
