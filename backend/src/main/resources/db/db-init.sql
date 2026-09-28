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
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1 正常 / 0 禁用',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删 / 1 已删',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';