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

- ✅ **认证系统**：后台登录、小程序微信登录（完整实现，支持微信API调用）
- ✅ **小说管理**：CRUD、审核、状态管理
- ✅ **章节管理**：CRUD、分页阅读、字数统计
- ✅ **阅读功能**：阅读记录、试看控制、阅读进度保存
- ✅ **广告激励**：观看广告增加试看时间
- ✅ **用户管理**：小程序用户管理、系统用户管理

### 管理后台

- ✅ **登录认证**：JWT认证、权限控制
- ✅ **小说管理**：列表、编辑、删除、提交审核、状态管理
- ✅ **章节管理**：列表、富文本编辑、字数统计、权限控制
- ✅ **审核管理**：审核列表、审核操作、审核备注（管理员）
- ✅ **用户管理**：用户列表、用户状态管理（管理员）

### 小程序

- ✅ **微信登录**：完整的微信登录流程，自动注册新用户
- ✅ **小说浏览**：小说列表、搜索、分类筛选
- ✅ **小说详情**：详情展示、章节列表、阅读量统计
- ✅ **章节列表**：章节列表展示、阅读进度标识
- ✅ **阅读页面**：
  - 翻页功能（上一页/下一页）
  - 字体大小调整（24-48rpx）
  - 行间距调整（1.2-2.5倍）
  - 夜间模式
  - 背景色选择（4种背景色）
  - 阅读进度自动保存
  - 设置持久化
- ✅ **激励广告**：观看广告增加试看时间
- ✅ **个人中心**：用户信息、试看时间显示

## 注意事项

1. **微信小程序配置**：
   - 修改 `novel-app/manifest.json` 中的 `appid`
   - 修改 `novel-app/pages/reader/reader.vue` 中的广告位ID
   - 配置微信小程序AppID和Secret（通过环境变量 `WX_APPID` 和 `WX_SECRET`）

2. **后端配置**：
   - 修改 `application.yml` 中的数据库和Redis连接信息
   - 修改JWT密钥（生产环境必须修改）
   - 配置微信小程序AppID和Secret（用于微信登录）

3. **微信登录**：
   - 已完整实现微信API调用逻辑
   - 需要配置正确的AppID和Secret才能正常使用
   - 新用户会自动注册，老用户自动登录

4. **密码加密**：
   - 测试账号密码需要重新生成BCrypt加密值
   - 可以使用以下代码生成：
   ```java
   BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
   String encoded = encoder.encode("your-password");
   ```

5. **富文本编辑**：
   - 章节内容以HTML格式存储
   - 阅读时会自动渲染HTML内容
   - 字数统计会自动去除HTML标签

6. **阅读设置**：
   - 阅读设置会保存在本地存储中
   - 阅读进度会自动同步到服务器

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

### 已完成功能 ✅

- [x] 数据库设计
- [x] Spring Boot后端框架搭建
- [x] API接口实现（认证、小说、章节、阅读、用户）
- [x] Vue管理后台框架搭建
- [x] UniApp小程序框架搭建
- [x] 微信登录完整实现（支持微信API调用）
- [x] 章节编辑富文本编辑器（Quill编辑器）
- [x] 阅读页面优化（翻页、字体、行距、背景、夜间模式）
- [x] 阅读进度保存和同步
- [x] 广告激励功能
- [x] 权限控制系统
- [x] 基础测试和优化

### 下一阶段开发规划 🚀

#### 1. 搜索功能优化（优先级：高）
- [ ] 全文搜索功能（Elasticsearch集成）
- [ ] 热门搜索词统计
- [ ] 搜索历史记录
- [ ] 搜索建议和自动补全
- [ ] 搜索结果排序优化

#### 2. 评论系统（优先级：高）
- [ ] 章节评论功能
- [ ] 评论回复功能
- [ ] 评论点赞/点踩
- [ ] 评论审核机制
- [ ] 评论通知功能

#### 3. 收藏和书架功能（优先级：高）
- [ ] 小说收藏功能
- [ ] 个人书架管理
- [ ] 书架分类（在读、已读、想读）
- [ ] 收藏列表展示
- [ ] 书架同步功能

#### 4. 推荐系统（优先级：中）
- [ ] 热门推荐算法
- [ ] 个性化推荐（基于阅读历史）
- [ ] 相似小说推荐
- [ ] 分类推荐
- [ ] 推荐位管理

#### 5. 数据统计和分析（优先级：中）
- [ ] 阅读数据统计（阅读量、阅读时长）
- [ ] 用户行为分析
- [ ] 小说热度排行
- [ ] 作者数据统计
- [ ] 管理后台数据看板

#### 6. 支付和会员系统（优先级：中）
- [ ] VIP会员体系
- [ ] 付费章节功能
- [ ] 支付集成（微信支付）
- [ ] 会员权益管理
- [ ] 订单管理

#### 7. 消息通知系统（优先级：中）
- [ ] 系统消息推送
- [ ] 评论回复通知
- [ ] 审核结果通知
- [ ] 消息中心页面
- [ ] 消息已读/未读状态

#### 8. 性能优化（优先级：中）
- [ ] Redis缓存优化
- [ ] 数据库查询优化
- [ ] 接口响应时间优化
- [ ] 前端资源加载优化
- [ ] CDN集成

#### 9. 安全加固（优先级：高）
- [ ] 接口限流（防止刷接口）
- [ ] 防爬虫机制
- [ ] 敏感词过滤
- [ ] 内容安全审核（AI审核）
- [ ] 数据加密传输

#### 10. 多端同步（优先级：低）
- [ ] 阅读进度多端同步
- [ ] 设置多端同步
- [ ] 书架多端同步

#### 11. 其他功能优化（优先级：低）
- [ ] 小说分类管理优化
- [ ] 标签系统
- [ ] 作者专区
- [ ] 打赏功能
- [ ] 分享功能优化

## 技术栈

### 后端技术
- **框架**：Spring Boot 3.2
- **ORM**：MyBatis Plus 3.5.5
- **数据库**：MySQL 8.0
- **缓存**：Redis
- **安全**：Spring Security + JWT
- **工具**：Lombok、Validation

### 管理后台技术
- **框架**：Vue 3 + Vite
- **UI组件**：Element Plus
- **路由**：Vue Router 4
- **状态管理**：Pinia
- **富文本编辑器**：Quill (@vueup/vue-quill)
- **HTTP客户端**：Axios

### 小程序技术
- **框架**：UniApp + Vue 3
- **平台**：微信小程序

### 部署相关
- **构建工具**：Maven、npm
- **部署脚本**：Shell脚本（deploy.sh）
- **Web服务器**：Nginx（参考 nginx.conf.example）

## 部署说明

详细部署说明请参考 [DEPLOY.md](./DEPLOY.md)

## 功能特性

详细功能特性说明请参考 [FEATURES.md](./FEATURES.md)
