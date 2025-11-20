# 开发指南

本文档介绍项目的技术栈、项目结构、开发环境配置和开发规范。

## 技术栈

### 后端技术

- **框架**: Spring Boot 3.2.0
- **ORM**: MyBatis Plus 3.5.5
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **安全**: Spring Security + JWT (jjwt 0.12.3)
- **工具**: Lombok、Validation
- **Java版本**: JDK 17+

### 管理后台技术

- **框架**: Vue 3.3.4 + Vite 5.0.8
- **UI组件**: Element Plus 2.4.4
- **路由**: Vue Router 4.2.5
- **状态管理**: Pinia 2.1.7
- **富文本编辑器**: Quill 1.3.7 (@vueup/vue-quill 1.2.0)
- **HTTP客户端**: Axios 1.6.2
- **构建工具**: Vite

### 小程序技术

- **框架**: UniApp + Vue 3
- **平台**: 微信小程序
- **构建工具**: HBuilderX 或 uni-app CLI

## 项目结构

### 后端结构 (novel-api)

```
novel-api/
├── src/main/java/com/novel/
│   ├── NovelApplication.java      # 启动类
│   ├── config/                     # 配置类
│   │   ├── SecurityConfig.java     # Spring Security配置
│   │   ├── JwtAuthenticationFilter.java  # JWT认证过滤器
│   │   ├── RedisConfig.java        # Redis配置
│   │   └── WebMvcConfig.java       # Web MVC配置
│   ├── controller/                 # 控制器层
│   │   ├── AuthController.java     # 认证控制器
│   │   ├── NovelController.java    # 小说控制器
│   │   ├── ChapterController.java  # 章节控制器
│   │   ├── ReadingController.java  # 阅读控制器
│   │   ├── UserController.java     # 用户控制器
│   │   └── admin/                  # 管理端控制器
│   │       ├── AdminNovelController.java
│   │       ├── AdminChapterController.java
│   │       └── AdminUserController.java
│   ├── service/                    # 服务层
│   │   ├── AuthService.java        # 认证服务
│   │   ├── NovelService.java       # 小说服务
│   │   ├── ChapterService.java     # 章节服务
│   │   ├── ReadingService.java     # 阅读服务
│   │   ├── UserService.java        # 用户服务
│   │   └── AdRewardService.java    # 广告奖励服务
│   ├── mapper/                     # MyBatis Mapper
│   │   ├── SysUserMapper.java
│   │   ├── AppUserMapper.java
│   │   ├── NovelMapper.java
│   │   ├── ChapterMapper.java
│   │   ├── ReadingRecordMapper.java
│   │   └── AdWatchRecordMapper.java
│   ├── entity/                     # 实体类
│   │   ├── SysUser.java            # 系统用户
│   │   ├── AppUser.java            # 小程序用户
│   │   ├── Novel.java              # 小说
│   │   ├── Chapter.java            # 章节
│   │   ├── ReadingRecord.java      # 阅读记录
│   │   └── AdWatchRecord.java      # 广告观看记录
│   ├── dto/                        # 数据传输对象
│   │   ├── LoginDTO.java
│   │   ├── WxLoginDTO.java
│   │   ├── NovelDTO.java
│   │   ├── ChapterDTO.java
│   │   └── AuditDTO.java
│   ├── vo/                         # 视图对象
│   │   ├── LoginVO.java
│   │   ├── NovelVO.java
│   │   ├── ChapterVO.java
│   │   ├── ChapterListVO.java
│   │   ├── ReadingRecordVO.java
│   │   ├── TrialCheckVO.java
│   │   └── AppUserVO.java
│   ├── common/                     # 公共类
│   │   ├── Result.java             # 统一响应结果
│   │   ├── JwtUtil.java            # JWT工具类
│   │   ├── SecurityUtil.java       # 安全工具类
│   │   ├── UserDetails.java        # 用户详情
│   │   └── WxApiUtil.java          # 微信API工具类
│   └── exception/                  # 异常处理
│       └── GlobalExceptionHandler.java
└── src/main/resources/
    ├── application.yml             # 应用配置
    └── mapper/                     # MyBatis XML映射文件
```

### 管理后台结构 (novel-admin)

```
novel-admin/
├── src/
│   ├── main.js                    # 入口文件
│   ├── App.vue                    # 根组件
│   ├── api/                       # API封装
│   │   └── index.js
│   ├── router/                    # 路由配置
│   │   └── index.js
│   ├── store/                     # 状态管理
│   │   └── user.js
│   ├── utils/                     # 工具类
│   │   └── request.js             # Axios封装
│   └── views/                     # 页面组件
│       ├── layout/                # 布局组件
│       │   └── Layout.vue
│       ├── login/                 # 登录页
│       │   └── Login.vue
│       ├── dashboard/             # 仪表盘
│       │   └── Dashboard.vue
│       ├── novel/                 # 小说管理
│       │   ├── NovelList.vue
│       │   ├── NovelEdit.vue
│       │   ├── ChapterList.vue
│       │   └── ChapterEdit.vue
│       ├── audit/                 # 审核管理
│       │   └── AuditList.vue
│       └── user/                  # 用户管理
│           └── UserList.vue
├── index.html
├── vite.config.js
└── package.json
```

### 小程序结构 (novel-app)

```
novel-app/
├── pages/                         # 页面
│   ├── index/                     # 首页
│   │   └── index.vue
│   ├── novel-detail/              # 小说详情
│   │   └── novel-detail.vue
│   ├── chapter-list/              # 章节列表
│   │   └── chapter-list.vue
│   ├── reader/                    # 阅读页
│   │   └── reader.vue
│   └── my/                        # 个人中心
│       └── my.vue
├── api/                           # API封装
│   └── index.js
├── utils/                         # 工具类
│   └── request.js
├── App.vue                        # 根组件
├── manifest.json                  # 应用配置
└── pages.json                     # 页面配置
```

## 开发环境配置

### 1. 后端开发环境

#### 安装JDK 17+

```bash
# 检查Java版本
java -version

# 应该显示 java version "17" 或更高版本
```

#### 安装Maven 3.6+

```bash
# 检查Maven版本
mvn -version

# 应该显示 Apache Maven 3.6.0 或更高版本
```

#### 配置数据库

1. 安装MySQL 8.0+
2. 创建数据库：

```sql
CREATE DATABASE novel_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. 执行建表SQL：

```bash
mysql -u root -p novel_db < database.sql
```

#### 配置Redis

1. 安装Redis 6.0+
2. 启动Redis服务：

```bash
redis-server
```

#### 配置微信小程序

在 `application.yml` 中配置或使用环境变量：

```bash
export WX_APPID=your_appid
export WX_SECRET=your_secret
```

#### 生成测试账号密码

测试账号密码需要BCrypt加密，可以使用以下代码生成：

```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "your_password";
        String encoded = encoder.encode(password);
        System.out.println("Encoded password: " + encoded);
    }
}
```

### 2. 管理后台开发环境

#### 安装Node.js 16+

```bash
# 检查Node.js版本
node -v

# 应该显示 v16.0.0 或更高版本
```

#### 安装依赖

```bash
cd novel-admin
npm install
```

#### 启动开发服务器

```bash
npm run dev
```

管理后台将在 `http://localhost:3000` 运行。

### 3. 小程序开发环境

#### 方式一：使用HBuilderX

1. 下载并安装 [HBuilderX](https://www.dcloud.io/hbuilderx.html)
2. 打开HBuilderX，导入 `novel-app` 目录
3. 配置小程序AppID（在 `manifest.json` 中）
4. 运行 -> 运行到小程序模拟器 -> 微信开发者工具

#### 方式二：使用微信开发者工具

1. 下载并安装 [微信开发者工具](https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html)
2. 使用微信开发者工具打开 `novel-app` 目录
3. 配置小程序AppID

## 开发规范

### 代码风格

#### Java代码规范

- 使用4个空格缩进
- 类名使用大驼峰命名（PascalCase）
- 方法名和变量名使用小驼峰命名（camelCase）
- 常量使用全大写下划线分隔（UPPER_SNAKE_CASE）
- 使用Lombok简化代码

#### Vue代码规范

- 使用2个空格缩进
- 组件名使用大驼峰命名（PascalCase）
- 文件名使用大驼峰命名（PascalCase）
- 使用 `<script setup>` 语法
- 使用组合式API（Composition API）

### Git提交规范

- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建/工具相关

示例：

```bash
git commit -m "feat: 添加评论功能"
git commit -m "fix: 修复阅读进度保存问题"
```

### API设计规范

- RESTful风格
- 统一响应格式（使用 `Result` 类）
- 统一错误处理（使用 `GlobalExceptionHandler`）
- 使用JWT进行认证
- API路径前缀：`/api`（后台接口）、`/api/admin`（管理端接口）

### 数据库规范

- 表名使用小写下划线分隔（snake_case）
- 字段名使用小写下划线分隔（snake_case）
- 主键统一使用 `id`（BIGINT AUTO_INCREMENT）
- 创建时间和更新时间字段统一命名：`create_time`、`update_time`
- 使用逻辑删除（`deleted` 字段）

## 常见问题

### 1. 后端启动失败

**问题**: 数据库连接失败

**解决**: 
- 检查MySQL服务是否启动
- 检查 `application.yml` 中的数据库配置
- 检查数据库是否存在

**问题**: Redis连接失败

**解决**:
- 检查Redis服务是否启动
- 检查 `application.yml` 中的Redis配置

### 2. 管理后台编译失败

**问题**: 依赖安装失败

**解决**:
```bash
cd novel-admin
rm -rf node_modules package-lock.json
npm install
```

### 3. 微信登录失败

**问题**: 微信API调用失败

**解决**:
- 检查 `WX_APPID` 和 `WX_SECRET` 是否正确配置
- 检查小程序AppID是否与后端配置一致
- 检查网络连接

### 4. 密码加密问题

**问题**: 测试账号无法登录

**解决**: 
- 测试账号密码需要重新生成BCrypt加密值
- 参考上面的"生成测试账号密码"部分

## 调试技巧

### 后端调试

1. 使用IDE断点调试
2. 查看日志输出（日志级别在 `application.yml` 中配置）
3. 使用Postman测试API接口

### 前端调试

1. 使用浏览器开发者工具
2. 使用Vue DevTools
3. 查看Network面板检查API请求

### 小程序调试

1. 使用微信开发者工具的调试工具
2. 查看Console输出
3. 使用Network面板检查API请求

## 参考资料

- [Spring Boot官方文档](https://spring.io/projects/spring-boot)
- [Vue 3官方文档](https://cn.vuejs.org/)
- [UniApp官方文档](https://uniapp.dcloud.net.cn/)
- [Element Plus官方文档](https://element-plus.org/zh-CN/)
- [MyBatis Plus官方文档](https://baomidou.com/)
