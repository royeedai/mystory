# 小说阅读平台完整开发文档

## 项目结构

```
workspace/
├── database.sql              # 数据库建表SQL
├── novel-api/               # Spring Boot API后端
│   ├── src/main/java/com/novel/
│   │   ├── NovelApplication.java
│   │   ├── config/          # 配置类
│   │   ├── controller/      # 控制器
│   │   ├── service/         # 服务层
│   │   ├── mapper/          # MyBatis Mapper
│   │   ├── entity/          # 实体类
│   │   ├── dto/             # 数据传输对象
│   │   ├── vo/              # 视图对象
│   │   ├── common/          # 公共类
│   │   └── exception/       # 异常处理
│   └── pom.xml
├── novel-admin/             # Vue管理后台
│   ├── src/
│   │   ├── views/           # 页面组件
│   │   ├── components/      # 公共组件
│   │   ├── api/             # API封装
│   │   ├── router/          # 路由配置
│   │   ├── store/           # 状态管理
│   │   └── utils/           # 工具类
│   └── package.json
└── novel-app/               # UniApp小程序
    ├── pages/               # 页面
    ├── components/          # 组件
    ├── api/                 # API封装
    ├── utils/               # 工具类
    ├── manifest.json
    └── pages.json
```

## 快速开始

### 1. 数据库准备

执行 `database.sql` 创建数据库表结构。

注意：测试账号的密码需要重新生成BCrypt加密后的值。

### 2. 后端API启动

```bash
cd novel-api
# 修改 application.yml 中的数据库和Redis配置
mvn spring-boot:run
```

后端服务默认运行在 `http://localhost:8080`

### 3. 管理后台启动

```bash
cd novel-admin
npm install
npm run dev
```

管理后台默认运行在 `http://localhost:3000`

### 4. 小程序启动

使用HBuilderX或微信开发者工具打开 `novel-app` 目录。

## 功能说明

### 后端API

- 认证：后台登录、小程序微信登录
- 小说管理：CRUD、审核
- 章节管理：CRUD、分页阅读
- 阅读功能：阅读记录、试看控制
- 广告激励：观看广告增加试看时间

### 管理后台

- 登录认证
- 小说管理（列表、编辑、删除、提交审核）
- 章节管理
- 审核管理（管理员）
- 用户管理（管理员）

### 小程序

- 微信登录
- 小说列表、搜索
- 小说详情
- 章节列表
- 阅读页面（翻页、试看控制）
- 激励广告
- 个人中心

## 注意事项

1. **微信小程序配置**：
   - 修改 `novel-app/manifest.json` 中的 `appid`
   - 修改 `novel-app/pages/reader/reader.vue` 中的广告位ID

2. **后端配置**：
   - 修改 `application.yml` 中的数据库和Redis连接信息
   - 修改JWT密钥（生产环境必须修改）

3. **微信登录**：
   - 当前实现为简化版本，实际应该调用微信API获取openid
   - 需要在后端实现微信API调用逻辑

4. **密码加密**：
   - 测试账号密码需要重新生成BCrypt加密值
   - 可以使用以下代码生成：
   ```java
   BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
   String encoded = encoder.encode("your-password");
   ```

## API接口文档

### 认证接口
- `POST /api/auth/login` - 后台登录
- `POST /api/auth/wx-login` - 小程序登录

### 小说接口
- `GET /api/novel/list` - 获取小说列表
- `GET /api/novel/{id}` - 获取小说详情
- `POST /api/admin/novel` - 创建小说
- `PUT /api/admin/novel/{id}` - 更新小说
- `DELETE /api/admin/novel/{id}` - 删除小说
- `POST /api/admin/novel/{id}/audit` - 审核小说

### 章节接口
- `GET /api/chapter/novel/{novelId}` - 获取章节列表
- `GET /api/chapter/{id}/content` - 获取章节内容（分页）
- `POST /api/admin/chapter/novel/{novelId}` - 创建章节
- `PUT /api/admin/chapter/{id}` - 更新章节
- `DELETE /api/admin/chapter/{id}` - 删除章节

### 阅读接口
- `GET /api/reading/record` - 获取阅读记录
- `POST /api/reading/record` - 保存阅读进度
- `GET /api/reading/check-trial` - 检查试看权限

### 用户接口
- `GET /api/user/info` - 获取用户信息
- `POST /api/user/ad-reward` - 广告激励奖励

## 开发进度

- [x] 数据库设计
- [x] Spring Boot后端框架
- [x] API接口实现
- [x] Vue管理后台框架
- [x] UniApp小程序框架
- [ ] 微信登录完整实现
- [ ] 章节编辑富文本编辑器
- [ ] 阅读页面优化
- [ ] 测试和优化

## 技术栈

- 后端：Spring Boot 3.2 + MyBatis Plus + MySQL + Redis + JWT
- 管理后台：Vue 3 + Element Plus + Vite
- 小程序：UniApp + Vue 3
