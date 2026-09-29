-- =====================================================================
-- 电商平台数据库初始化脚本（数据库唯一入口）
-- =====================================================================
-- 库名   ：mall
-- 字符集 ：utf8mb4
-- 连接   ：127.0.0.1:3306/mall
-- 账号   ：root
-- 密码   ：123456（本机本地开发默认值，可按需修改）
--
-- 使用方式（命令行执行，注意：Windows 的 mysql 客户端无法打开
--   含中文的绝对路径，请先进入 backend 目录再用相对路径）：
--   cd backend
--   mysql -uroot -p123456 -e "source src/main/resources/db/db-init.sql"
--
-- 说明：本文件为数据库唯一入口，业务表随模块开发逐步追加到此处；
--       所有脚本幂等（IF NOT EXISTS），可重复执行。
-- =====================================================================

CREATE DATABASE IF NOT EXISTS mall
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE mall;

-- =====================================================================
-- 用户表（认证鉴权模块）
-- =====================================================================
CREATE TABLE IF NOT EXISTS `user` (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    username    VARCHAR(50)  NOT NULL COMMENT '用户名（登录账号）',
    password    VARCHAR(100) NOT NULL COMMENT '密码（BCrypt 加密）',
    nickname    VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '昵称',
    role        TINYINT      NOT NULL DEFAULT 1 COMMENT '角色：1 普通用户 / 2 管理员',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1 正常 / 0 禁用',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删 / 1 已删',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

-- 兼容旧表：若 user 表缺少 role 列则补加（幂等）
SET @col_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = 'mall' AND TABLE_NAME = 'user' AND COLUMN_NAME = 'role'
);
SET @ddl := IF(@col_exists = 0,
    'ALTER TABLE `user` ADD COLUMN role TINYINT NOT NULL DEFAULT 1 COMMENT ''角色：1 普通用户 / 2 管理员'' AFTER nickname',
    'SELECT 1');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =====================================================================
-- 商品分类表（商品模块）
-- =====================================================================
CREATE TABLE IF NOT EXISTS `category` (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    name        VARCHAR(50)  NOT NULL COMMENT '分类名称',
    sort        INT          NOT NULL DEFAULT 0 COMMENT '排序（越小越靠前）',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1 启用 / 0 禁用',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删 / 1 已删',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品分类表';

-- =====================================================================
-- 商品表（商品模块；第一版单规格，后续拆 SKU 表）
-- =====================================================================
CREATE TABLE IF NOT EXISTS `product` (
    id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    category_id BIGINT        NOT NULL COMMENT '分类 id',
    name        VARCHAR(100)  NOT NULL COMMENT '商品名称',
    main_image  VARCHAR(255)  NOT NULL DEFAULT '' COMMENT '主图 URL',
    price       BIGINT        NOT NULL DEFAULT 0 COMMENT '售价（单位：分）',
    stock       INT           NOT NULL DEFAULT 0 COMMENT '库存',
    status      TINYINT       NOT NULL DEFAULT 1 COMMENT '状态：1 上架 / 0 下架',
    deleted     TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删 / 1 已删',
    create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_category (category_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品表';

-- =====================================================================
-- 购物车表（购物车模块；用户 + 商品唯一，重复加购累加数量）
-- =====================================================================
CREATE TABLE IF NOT EXISTS `cart` (
    id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id     BIGINT      NOT NULL COMMENT '用户 id',
    product_id  BIGINT      NOT NULL COMMENT '商品 id',
    quantity    INT         NOT NULL DEFAULT 1 COMMENT '数量',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_product (user_id, product_id),
    KEY idx_user (user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='购物车表';

-- =====================================================================
-- 订单表（订单模块；主键雪花 id，商品信息以明细快照留存）
-- =====================================================================
CREATE TABLE IF NOT EXISTS `order` (
    id               BIGINT      NOT NULL COMMENT '主键（雪花 id）',
    order_no         VARCHAR(32) NOT NULL COMMENT '订单号',
    user_id          BIGINT      NOT NULL COMMENT '用户 id',
    total_price      BIGINT      NOT NULL DEFAULT 0 COMMENT '订单总价（单位：分）',
    address_snapshot VARCHAR(255) NOT NULL DEFAULT '' COMMENT '收货地址快照（收货人+手机+地址，下单时固化）',
    status           TINYINT     NOT NULL DEFAULT 0 COMMENT '状态：0 待支付 / 1 已支付 / 2 已取消',
    create_time      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_user (user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='订单表';

-- 兼容旧表：若 order 表缺少 address_snapshot 列则补加（幂等）
SET @col_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = 'mall' AND TABLE_NAME = 'order' AND COLUMN_NAME = 'address_snapshot'
);
SET @ddl := IF(@col_exists = 0,
    'ALTER TABLE `order` ADD COLUMN address_snapshot VARCHAR(255) NOT NULL DEFAULT '''' COMMENT ''收货地址快照'' AFTER total_price',
    'SELECT 1');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =====================================================================
-- 订单明细表（订单模块；商品信息下单时快照，后续商品变更不影响历史订单）
-- =====================================================================
CREATE TABLE IF NOT EXISTS `order_item` (
    id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_id      BIGINT       NOT NULL COMMENT '订单 id',
    product_id    BIGINT       NOT NULL COMMENT '商品 id',
    product_name  VARCHAR(100) NOT NULL COMMENT '商品名称（快照）',
    product_image VARCHAR(255) NOT NULL DEFAULT '' COMMENT '商品主图（快照）',
    price         BIGINT       NOT NULL DEFAULT 0 COMMENT '成交单价（单位：分，快照）',
    quantity      INT          NOT NULL DEFAULT 1 COMMENT '数量',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_order (order_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='订单明细表';

-- =====================================================================
-- 收货地址表（地址模块；归属用户，下单时快照到 order.address_snapshot）
-- =====================================================================
CREATE TABLE IF NOT EXISTS `address` (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id     BIGINT       NOT NULL COMMENT '用户 id',
    receiver    VARCHAR(50)  NOT NULL COMMENT '收货人',
    phone       VARCHAR(20)  NOT NULL COMMENT '手机号',
    region      VARCHAR(100) NOT NULL DEFAULT '' COMMENT '省市区',
    detail      VARCHAR(255) NOT NULL COMMENT '详细地址',
    is_default  TINYINT      NOT NULL DEFAULT 0 COMMENT '是否默认：1 是 / 0 否',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_user (user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='收货地址表';

-- =====================================================================
-- 演示数据（可选）：首次初始化或演示用，可自行删除。
-- 说明：使用显式 id + ON DUPLICATE KEY UPDATE，脚本重复执行不会重复插入。
-- =====================================================================
INSERT INTO category (id, name, sort, status) VALUES
(1, '手机数码', 1, 1),
(2, '电脑办公', 2, 1),
(3, '服饰鞋包', 3, 1),
(4, '食品生鲜', 4, 1),
(5, '美妆个护', 5, 1)
ON DUPLICATE KEY UPDATE name = VALUES(name), sort = VALUES(sort), status = VALUES(status);

INSERT INTO product (id, category_id, name, main_image, price, stock, status) VALUES
(1, 1, '华为 Mate 70 Pro',   'https://picsum.photos/seed/huawei70/400/400', 699900, 100, 1),
(2, 1, 'iPhone 16 Pro',      'https://picsum.photos/seed/iphone16/400/400', 899900, 80, 1),
(3, 1, '小米 15 手机',       'https://picsum.photos/seed/xiaomi15/400/400', 429900, 150, 1),
(4, 1, '苹果 AirPods Pro 3', 'https://picsum.photos/seed/airpods/400/400',  189900, 200, 1),
(5, 2, '联想拯救者 Y9000P',  'https://picsum.photos/seed/lenovo/400/400',  999900, 60, 1),
(6, 2, '罗技 MX Master 3S 鼠标', 'https://picsum.photos/seed/mouse/400/400',    69900, 300, 1),
(7, 2, '樱桃机械键盘',       'https://picsum.photos/seed/keyboard/400/400', 39900, 260, 1),
(8, 3, '优衣库连帽卫衣',     'https://picsum.photos/seed/hoodie/400/400',   19900, 500, 1),
(9, 3, 'Nike Air 运动鞋',    'https://picsum.photos/seed/nike/400/400',    89900, 180, 1),
(10, 4, '智利车厘子 2kg',    'https://picsum.photos/seed/cherry/400/400',   12900, 400, 1),
(11, 4, '阳澄湖大闸蟹礼盒',  'https://picsum.photos/seed/crab/400/400',     29900, 120, 1),
(12, 5, '兰蔻小黑瓶精华',    'https://picsum.photos/seed/lancome/400/400',  108000, 90, 1)
ON DUPLICATE KEY UPDATE name = VALUES(name), main_image = VALUES(main_image),
                        price = VALUES(price), stock = VALUES(stock), status = VALUES(status);

-- 演示收货地址（user_id 5 = demo_store，6 = cartuser，与演示账号对应）
INSERT INTO address (id, user_id, receiver, phone, region, detail, is_default) VALUES
(1, 5, '演示店主', '13800000001', '浙江省 杭州市 西湖区', '文三路 100 号 1 栋 101 室', 1),
(2, 6, '陈同学',   '13900000002', '浙江省 杭州市 余杭区', '良睦路 1399 号 梦想小镇 3 号楼', 1)
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id), receiver = VALUES(receiver), phone = VALUES(phone),
                        region = VALUES(region), detail = VALUES(detail), is_default = VALUES(is_default);

-- 演示管理员（admin/123456，role=2 管理员；哈希为 123456 的 BCrypt 值）
INSERT INTO `user` (id, username, password, nickname, role, status) VALUES
(7, 'admin', '$2a$10$osMwiKzsn.Lg7.H/XwFYJ.1am7UAZst3aZpa2Vs1RSJDERezx1JaW', '管理员', 2, 1)
ON DUPLICATE KEY UPDATE password = VALUES(password), nickname = VALUES(nickname),
                        role = VALUES(role), status = VALUES(status);