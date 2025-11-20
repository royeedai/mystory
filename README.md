# 小说阅读平台

一个基于 Spring Boot + Vue 3 + UniApp 的完整小说阅读平台，包含后端API、管理后台和微信小程序三端应用。

## 项目简介

本项目是一个功能完整的小说阅读平台，支持：
- 📚 小说和章节的完整管理
- 👥 用户认证和权限管理
- 📖 丰富的阅读体验（字体、行距、背景、夜间模式等）
- 🎁 广告激励系统
- ✅ 内容审核流程
- 📱 微信小程序端

## 快速开始

### 前置要求

- **Java**: JDK 17+
- **Maven**: 3.6+
- **Node.js**: 16+
- **MySQL**: 8.0+
- **Redis**: 6.0+

### 1. 克隆项目

```bash
git clone <repository-url>
cd novel-platform
```

### 2. 数据库初始化

执行 `database.sql` 创建数据库表结构：

```bash
mysql -u root -p < database.sql
```

### 3. 配置环境

#### 后端配置

编辑 `novel-api/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/novel_db
    username: your_username
    password: your_password
  redis:
    host: localhost
    port: 6379
```

#### 微信小程序配置

设置环境变量（或修改 `application.yml`）：

```bash
export WX_APPID=your_appid
export WX_SECRET=your_secret
```

### 4. 启动服务

#### 启动后端

```bash
cd novel-api
mvn spring-boot:run
```

后端服务运行在 `http://localhost:8080`

#### 启动管理后台

```bash
cd novel-admin
npm install
npm run dev
```

管理后台运行在 `http://localhost:3000`

#### 启动小程序

使用 HBuilderX 或微信开发者工具打开 `novel-app` 目录。

### 5. 测试账号

- **管理员**: `admin` / `admin123`
- **作者**: `author1` / `author123`

> ⚠️ 注意：测试账号密码需要重新生成BCrypt加密值，请参考[开发指南](./docs/development.md)

## 项目结构

```
novel-platform/
├── novel-api/          # Spring Boot 后端API
├── novel-admin/        # Vue 3 管理后台
├── novel-app/          # UniApp 微信小程序
├── database.sql        # 数据库建表SQL
├── deploy.sh           # 一键部署脚本
├── nginx.conf.example  # Nginx配置示例
└── docs/               # 文档目录
    ├── development.md  # 开发指南
    ├── deployment.md   # 部署文档
    ├── features.md     # 功能说明
    ├── api.md          # API接口文档
    └── database.md     # 数据库设计
```

## 文档导航

- 📖 [开发指南](./docs/development.md) - 技术栈、项目结构、开发环境配置
- 🚀 [部署文档](./docs/deployment.md) - 生产环境部署指南
- ✨ [功能说明](./docs/features.md) - 详细功能特性说明
- 🔌 [API接口](./docs/api.md) - 完整的API接口文档
- 🗄️ [数据库设计](./docs/database.md) - 数据库表结构说明

## 技术栈

### 后端
- Spring Boot 3.2
- MyBatis Plus 3.5.5
- Spring Security + JWT
- MySQL 8.0
- Redis

### 管理后台
- Vue 3 + Vite
- Element Plus
- Vue Router 4
- Pinia
- Quill 富文本编辑器

### 小程序
- UniApp + Vue 3
- 微信小程序

## 主要功能

### 后端API
- ✅ 认证系统（后台登录、微信登录）
- ✅ 小说管理（CRUD、审核、状态管理）
- ✅ 章节管理（CRUD、分页阅读、字数统计）
- ✅ 阅读功能（阅读记录、试看控制、进度保存）
- ✅ 广告激励（观看广告增加试看时间）
- ✅ 用户管理（小程序用户、系统用户）

### 管理后台
- ✅ 登录认证（JWT认证、权限控制）
- ✅ 小说管理（列表、编辑、删除、审核）
- ✅ 章节管理（列表、富文本编辑、字数统计）
- ✅ 审核管理（审核列表、审核操作）
- ✅ 用户管理（用户列表、状态管理）

### 小程序
- ✅ 微信登录（完整登录流程）
- ✅ 小说浏览（列表、搜索、分类筛选）
- ✅ 阅读功能（翻页、字体、行距、背景、夜间模式）
- ✅ 激励广告（观看广告增加试看时间）
- ✅ 个人中心（用户信息、试看时间）

## 开发进度

### 已完成 ✅
- [x] 数据库设计
- [x] Spring Boot后端框架
- [x] API接口实现
- [x] Vue管理后台
- [x] UniApp小程序
- [x] 微信登录
- [x] 富文本编辑器
- [x] 阅读页面优化
- [x] 权限控制系统

### 规划中 🚀
- [ ] 全文搜索（Elasticsearch）
- [ ] 评论系统
- [ ] 收藏和书架
- [ ] 推荐系统
- [ ] 支付和会员系统
- [ ] 消息通知系统

详细规划请参考[功能说明](./docs/features.md)

## 许可证

[MIT License](LICENSE)

## 贡献

欢迎提交 Issue 和 Pull Request！
