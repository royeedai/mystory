# 数据库设计文档

本文档详细说明小说阅读平台的数据库表结构设计。

## 数据库信息

- **数据库名**: `novel_db`
- **字符集**: `utf8mb4`
- **排序规则**: `utf8mb4_unicode_ci`
- **MySQL版本**: 8.0+

## 表结构说明

### 1. 系统用户表 (sys_user)

存储后台用户信息，包括管理员和作者。

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键ID | PRIMARY KEY, AUTO_INCREMENT |
| username | VARCHAR(50) | 用户名 | UNIQUE, NOT NULL |
| password | VARCHAR(255) | 密码（BCrypt加密） | NOT NULL |
| nickname | VARCHAR(50) | 昵称 | |
| role | VARCHAR(20) | 角色：AUTHOR-作者，ADMIN-管理员 | NOT NULL, DEFAULT 'AUTHOR' |
| status | TINYINT | 状态：0-禁用，1-启用 | DEFAULT 1 |
| create_time | DATETIME | 创建时间 | DEFAULT CURRENT_TIMESTAMP |
| update_time | DATETIME | 更新时间 | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |

**索引**:
- PRIMARY KEY (`id`)
- UNIQUE KEY (`username`)

**角色说明**:
- `AUTHOR`: 作者，可以创建和管理自己的小说
- `ADMIN`: 管理员，拥有所有权限，可以审核小说和管理用户

### 2. 小程序用户表 (app_user)

存储微信小程序用户信息。

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键ID | PRIMARY KEY, AUTO_INCREMENT |
| openid | VARCHAR(100) | 微信openid | UNIQUE, NOT NULL |
| unionid | VARCHAR(100) | 微信unionid | |
| nickname | VARCHAR(50) | 昵称 | |
| avatar | VARCHAR(255) | 头像URL | |
| trial_view_time | INT | 试看剩余时间（秒） | DEFAULT 0 |
| is_trial_enabled | TINYINT | 是否允许试看：0-否，1-是 | DEFAULT 1 |
| create_time | DATETIME | 创建时间 | DEFAULT CURRENT_TIMESTAMP |
| update_time | DATETIME | 更新时间 | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |

**索引**:
- PRIMARY KEY (`id`)
- UNIQUE KEY (`openid`)

**说明**:
- `openid`: 微信用户的唯一标识，用于微信登录
- `unionid`: 微信开放平台统一标识（可选）
- `trial_view_time`: 试看剩余时间，观看广告可以增加

### 3. 小说表 (novel)

存储小说基本信息。

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键ID | PRIMARY KEY, AUTO_INCREMENT |
| title | VARCHAR(200) | 小说标题 | NOT NULL |
| author_id | BIGINT | 作者ID | NOT NULL, FOREIGN KEY |
| cover | VARCHAR(255) | 封面图URL | |
| description | TEXT | 简介 | |
| category | VARCHAR(50) | 分类 | |
| status | VARCHAR(20) | 状态：DRAFT-草稿，PENDING-待审核，APPROVED-已审核，REJECTED-已拒绝 | DEFAULT 'DRAFT' |
| audit_user_id | BIGINT | 审核人ID | FOREIGN KEY |
| audit_time | DATETIME | 审核时间 | |
| audit_remark | VARCHAR(500) | 审核备注 | |
| sort_order | INT | 排序 | DEFAULT 0 |
| view_count | INT | 阅读量 | DEFAULT 0 |
| create_time | DATETIME | 创建时间 | DEFAULT CURRENT_TIMESTAMP |
| update_time | DATETIME | 更新时间 | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |

**索引**:
- PRIMARY KEY (`id`)
- FOREIGN KEY (`author_id`) REFERENCES `sys_user`(`id`)
- FOREIGN KEY (`audit_user_id`) REFERENCES `sys_user`(`id`)
- INDEX (`status`)
- INDEX (`category`)

**状态说明**:
- `DRAFT`: 草稿，作者可以继续编辑
- `PENDING`: 待审核，等待管理员审核
- `APPROVED`: 已审核，可以对外展示
- `REJECTED`: 已拒绝，需要修改后重新提交

### 4. 章节表 (chapter)

存储章节内容。

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键ID | PRIMARY KEY, AUTO_INCREMENT |
| novel_id | BIGINT | 小说ID | NOT NULL, FOREIGN KEY |
| title | VARCHAR(200) | 章节标题 | NOT NULL |
| content | LONGTEXT | 章节内容（HTML格式） | NOT NULL |
| chapter_num | INT | 章节序号 | NOT NULL |
| word_count | INT | 字数 | DEFAULT 0 |
| create_time | DATETIME | 创建时间 | DEFAULT CURRENT_TIMESTAMP |
| update_time | DATETIME | 更新时间 | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |

**索引**:
- PRIMARY KEY (`id`)
- FOREIGN KEY (`novel_id`) REFERENCES `novel`(`id`) ON DELETE CASCADE
- UNIQUE KEY (`novel_id`, `chapter_num`)

**说明**:
- `content`: 章节内容以HTML格式存储，支持富文本格式
- `chapter_num`: 章节序号，同一小说内唯一
- `word_count`: 字数统计，自动计算（去除HTML标签）
- 删除小说时会级联删除所有章节（ON DELETE CASCADE）

### 5. 阅读记录表 (reading_record)

存储用户的阅读进度。

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键ID | PRIMARY KEY, AUTO_INCREMENT |
| user_id | BIGINT | 用户ID（小程序用户） | NOT NULL, FOREIGN KEY |
| novel_id | BIGINT | 小说ID | NOT NULL, FOREIGN KEY |
| chapter_id | BIGINT | 当前章节ID | NOT NULL, FOREIGN KEY |
| page_index | INT | 当前页码 | DEFAULT 1 |
| last_read_time | DATETIME | 最后阅读时间 | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |

**索引**:
- PRIMARY KEY (`id`)
- FOREIGN KEY (`user_id`) REFERENCES `app_user`(`id`)
- FOREIGN KEY (`novel_id`) REFERENCES `novel`(`id`)
- FOREIGN KEY (`chapter_id`) REFERENCES `chapter`(`id`)
- UNIQUE KEY (`user_id`, `novel_id`)

**说明**:
- 每个用户对每本小说只有一条阅读记录
- `page_index`: 当前阅读的页码，用于分页阅读
- `last_read_time`: 最后阅读时间，自动更新

### 6. 广告观看记录表 (ad_watch_record)

存储用户观看广告的记录。

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键ID | PRIMARY KEY, AUTO_INCREMENT |
| user_id | BIGINT | 用户ID（小程序用户） | NOT NULL, FOREIGN KEY |
| ad_type | VARCHAR(20) | 广告类型：REWARD-激励广告 | NOT NULL |
| reward_time | INT | 奖励时间（秒） | NOT NULL |
| watch_time | DATETIME | 观看时间 | DEFAULT CURRENT_TIMESTAMP |

**索引**:
- PRIMARY KEY (`id`)
- FOREIGN KEY (`user_id`) REFERENCES `app_user`(`id`)
- INDEX (`user_id`, `watch_time`)

**说明**:
- `ad_type`: 广告类型，目前只有REWARD（激励广告）
- `reward_time`: 观看广告后奖励的试看时间（秒）
- 用于记录用户观看广告的历史，便于统计和分析

## 表关系图

```
sys_user (系统用户)
  ├── novel (小说) [author_id]
  └── novel (小说) [audit_user_id]

novel (小说)
  └── chapter (章节) [novel_id] ON DELETE CASCADE

app_user (小程序用户)
  ├── reading_record (阅读记录) [user_id]
  └── ad_watch_record (广告记录) [user_id]

reading_record (阅读记录)
  ├── novel (小说) [novel_id]
  └── chapter (章节) [chapter_id]
```

## 数据库初始化

### 执行建表SQL

```bash
mysql -u root -p novel_db < database.sql
```

### 测试数据

建表SQL中包含以下测试账号：

#### 管理员账号
- **用户名**: `admin`
- **密码**: `admin123`（BCrypt加密）
- **角色**: `ADMIN`

#### 作者账号
- **用户名**: `author1`
- **密码**: `author123`（BCrypt加密）
- **角色**: `AUTHOR`

> ⚠️ **重要提示**: 测试账号的密码是BCrypt加密后的值，实际使用时需要重新生成。可以使用以下Java代码生成：

```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String encoded = encoder.encode("your_password");
System.out.println(encoded);
```

## 数据库优化建议

### 1. 索引优化

已创建的索引：
- 主键索引：所有表的 `id` 字段
- 唯一索引：`sys_user.username`、`app_user.openid`、`chapter(novel_id, chapter_num)`、`reading_record(user_id, novel_id)`
- 外键索引：所有外键字段
- 普通索引：`novel.status`、`novel.category`、`ad_watch_record(user_id, watch_time)`

### 2. 查询优化

- 小说列表查询：使用 `status` 和 `category` 索引
- 章节列表查询：使用 `novel_id` 索引，按 `chapter_num` 排序
- 阅读记录查询：使用 `user_id` 索引

### 3. 存储优化

- 章节内容使用 `LONGTEXT` 类型，支持大文本存储
- 使用 `utf8mb4` 字符集，支持emoji等特殊字符
- 定期清理过期的广告观看记录（可选）

### 4. 性能优化

- 阅读量统计可以考虑使用Redis缓存，定期同步到数据库
- 热门小说列表可以使用Redis缓存
- 阅读记录可以设置过期时间，自动清理旧记录

## 数据备份

### 备份命令

```bash
# 备份整个数据库
mysqldump -u root -p novel_db > novel_db_backup_$(date +%Y%m%d).sql

# 只备份表结构
mysqldump -u root -p --no-data novel_db > novel_db_structure.sql

# 只备份数据
mysqldump -u root -p --no-create-info novel_db > novel_db_data.sql
```

### 恢复命令

```bash
# 恢复整个数据库
mysql -u root -p novel_db < novel_db_backup_20240101.sql
```

## 数据迁移

### 添加新字段

```sql
-- 示例：为小说表添加新字段
ALTER TABLE `novel` 
ADD COLUMN `tags` VARCHAR(200) COMMENT '标签' AFTER `category`;
```

### 修改字段类型

```sql
-- 示例：修改字段类型
ALTER TABLE `novel` 
MODIFY COLUMN `description` TEXT COMMENT '简介';
```

### 添加索引

```sql
-- 示例：添加索引
CREATE INDEX `idx_novel_create_time` ON `novel`(`create_time`);
```

## 注意事项

1. **字符集**: 必须使用 `utf8mb4` 字符集，以支持emoji等特殊字符
2. **外键约束**: 删除小说时会级联删除所有章节
3. **唯一约束**: 用户名、openid、章节序号等字段有唯一性约束
4. **时间字段**: 所有时间字段使用 `DATETIME` 类型，时区为 `Asia/Shanghai`
5. **密码加密**: 系统用户密码必须使用BCrypt加密存储
6. **逻辑删除**: 可以考虑添加 `deleted` 字段实现逻辑删除（当前使用物理删除）
