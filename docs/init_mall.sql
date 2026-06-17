USE mall_system;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(64) NOT NULL,
    `password` VARCHAR(128) NOT NULL,
    `nickname` VARCHAR(64) DEFAULT '',
    `avatar` VARCHAR(256) DEFAULT '',
    `phone` VARCHAR(20) DEFAULT '',
    `email` VARCHAR(64) DEFAULT '',
    `status` TINYINT NOT NULL DEFAULT 1,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `user_account`;
CREATE TABLE `user_account` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `balance` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `frozen_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `version` INT NOT NULL DEFAULT 0,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(128) NOT NULL,
    `description` TEXT,
    `price` DECIMAL(10,2) NOT NULL,
    `stock` INT NOT NULL DEFAULT 0,
    `sales` INT NOT NULL DEFAULT 0,
    `image` VARCHAR(256) DEFAULT '',
    `category` VARCHAR(64) DEFAULT '',
    `status` TINYINT NOT NULL DEFAULT 1,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `quantity` INT NOT NULL DEFAULT 1,
    `checked` TINYINT NOT NULL DEFAULT 1,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_no` VARCHAR(64) NOT NULL,
    `user_id` BIGINT NOT NULL,
    `total_amount` DECIMAL(10,2) NOT NULL,
    `pay_amount` DECIMAL(10,2) NOT NULL,
    `status` VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    `pay_time` DATETIME DEFAULT NULL,
    `pay_type` VARCHAR(32) DEFAULT '',
    `address` VARCHAR(256) DEFAULT '',
    `remark` VARCHAR(256) DEFAULT '',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `product_name` VARCHAR(128) NOT NULL,
    `product_image` VARCHAR(256) DEFAULT '',
    `price` DECIMAL(10,2) NOT NULL,
    `quantity` INT NOT NULL,
    `total_amount` DECIMAL(10,2) NOT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `payment_no` VARCHAR(64) NOT NULL,
    `order_no` VARCHAR(64) NOT NULL,
    `user_id` BIGINT NOT NULL,
    `amount` DECIMAL(10,2) NOT NULL,
    `pay_type` VARCHAR(32) NOT NULL,
    `status` VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_payment_no` (`payment_no`),
    KEY `idx_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `account_transaction`;
CREATE TABLE `account_transaction` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `type` VARCHAR(32) NOT NULL,
    `amount` DECIMAL(10,2) NOT NULL,
    `balance_before` DECIMAL(10,2) NOT NULL,
    `balance_after` DECIMAL(10,2) NOT NULL,
    `order_no` VARCHAR(64) DEFAULT '',
    `remark` VARCHAR(256) DEFAULT '',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `user` (`username`, `password`, `nickname`, `phone`) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', 'Admin', '13800138000'),
('zhangsan', 'e10adc3949ba59abbe56e057f20f883e', 'ZhangSan', '13800138001');

INSERT INTO `user_account` (`user_id`, `balance`, `frozen_amount`) VALUES
(1, 10000.00, 0.00),
(2, 5000.00, 0.00);

INSERT INTO `product` (`name`, `description`, `price`, `stock`, `sales`, `image`, `category`) VALUES
('iPhone 15 Pro', 'Apple flagship phone', 8999.00, 100, 50, 'https://via.placeholder.com/300', 'Phone'),
('MacBook Pro 14', 'M3 Pro chip laptop', 14999.00, 50, 30, 'https://via.placeholder.com/300', 'Computer'),
('AirPods Pro 2', 'Active noise cancelling', 1899.00, 200, 100, 'https://via.placeholder.com/300', 'Accessory'),
('iPad Air', 'M1 chip tablet', 4799.00, 80, 40, 'https://via.placeholder.com/300', 'Tablet'),
('Apple Watch Ultra 2', 'Titanium smartwatch', 6499.00, 60, 25, 'https://via.placeholder.com/300', 'Watch'),
('Xiaomi 14', 'Snapdragon 8 Gen3', 3999.00, 150, 80, 'https://via.placeholder.com/300', 'Phone'),
('Huawei Mate60', 'Kirin 9000S', 5999.00, 120, 60, 'https://via.placeholder.com/300', 'Phone'),
('ThinkPad X1', 'Intel 13th gen laptop', 9999.00, 40, 20, 'https://via.placeholder.com/300', 'Computer'),
('Sony WH-1000XM5', 'Noise cancelling headset', 2499.00, 100, 70, 'https://via.placeholder.com/300', 'Accessory'),
('Switch OLED', 'Nintendo gaming console', 2399.00, 90, 55, 'https://via.placeholder.com/300', 'Gaming');
