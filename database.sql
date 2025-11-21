-- 小说阅读平台数据库设计

-- 1. 系统用户表（作者+管理员）
CREATE TABLE `sys_user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（MD5加密）',
  `nickname` VARCHAR(50) COMMENT '昵称',
  `role` VARCHAR(20) NOT NULL DEFAULT 'AUTHOR' COMMENT '角色：AUTHOR-作者，ADMIN-管理员',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '系统用户表（作者+管理员）';

-- 2. 小程序用户表
CREATE TABLE `app_user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `openid` VARCHAR(100) UNIQUE NOT NULL COMMENT '微信openid',
  `unionid` VARCHAR(100) COMMENT '微信unionid',
  `nickname` VARCHAR(50) COMMENT '昵称',
  `avatar` VARCHAR(255) COMMENT '头像',
  `trial_view_time` INT DEFAULT 0 COMMENT '试看剩余时间（秒）',
  `is_trial_enabled` TINYINT DEFAULT 1 COMMENT '是否允许试看：0-否，1-是',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '小程序用户表';

-- 3. 小说表
CREATE TABLE `novel` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `title` VARCHAR(200) NOT NULL COMMENT '小说标题',
  `author_id` BIGINT NOT NULL COMMENT '作者ID',
  `cover` VARCHAR(255) COMMENT '封面图',
  `description` TEXT COMMENT '简介',
  `category` VARCHAR(50) COMMENT '分类',
  `status` VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿，PENDING-待审核，APPROVED-已审核，REJECTED-已拒绝',
  `audit_user_id` BIGINT COMMENT '审核人ID',
  `audit_time` DATETIME COMMENT '审核时间',
  `audit_remark` VARCHAR(500) COMMENT '审核备注',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `view_count` INT DEFAULT 0 COMMENT '阅读量',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`author_id`) REFERENCES `sys_user`(`id`),
  FOREIGN KEY (`audit_user_id`) REFERENCES `sys_user`(`id`)
) COMMENT '小说表';

-- 4. 章节表
CREATE TABLE `chapter` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `novel_id` BIGINT NOT NULL COMMENT '小说ID',
  `title` VARCHAR(200) NOT NULL COMMENT '章节标题',
  `content` LONGTEXT NOT NULL COMMENT '章节内容',
  `chapter_num` INT NOT NULL COMMENT '章节序号',
  `word_count` INT DEFAULT 0 COMMENT '字数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`novel_id`) REFERENCES `novel`(`id`) ON DELETE CASCADE,
  UNIQUE KEY `uk_novel_chapter` (`novel_id`, `chapter_num`)
) COMMENT '章节表';

-- 5. 阅读记录表
CREATE TABLE `reading_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `novel_id` BIGINT NOT NULL COMMENT '小说ID',
  `chapter_id` BIGINT NOT NULL COMMENT '当前章节ID',
  `page_index` INT DEFAULT 1 COMMENT '当前页码',
  `last_read_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `app_user`(`id`),
  FOREIGN KEY (`novel_id`) REFERENCES `novel`(`id`),
  FOREIGN KEY (`chapter_id`) REFERENCES `chapter`(`id`),
  UNIQUE KEY `uk_user_novel` (`user_id`, `novel_id`)
) COMMENT '阅读记录表';

-- 6. 广告观看记录表
CREATE TABLE `ad_watch_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `ad_type` VARCHAR(20) NOT NULL COMMENT '广告类型：REWARD-激励广告',
  `reward_time` INT NOT NULL COMMENT '奖励时间（秒）',
  `watch_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `app_user`(`id`)
) COMMENT '广告观看记录表';

-- 插入测试数据
-- 管理员账号：admin / admin123
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `role`, `status`) VALUES
('admin', '0192023a7bbd73250516f069df18b500', '管理员', 'ADMIN', 1);
-- 密码：admin123 (MD5加密后的值)

-- 作者账号：author1 / author123
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `role`, `status`) VALUES
('author1', 'e22591bbe1941fcc4b78972d4c60281f', '作者1', 'AUTHOR', 1);
-- 密码：author123 (MD5加密后的值)
