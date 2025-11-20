# 一键部署说明

本脚本用于一键编译和部署小说阅读平台的三端应用（Java后端、管理后台、移动端H5）。

## 目录结构

部署完成后，会在项目根目录生成 `public` 目录，结构如下：

```
public/
├── jar/
│   └── novel-api.jar          # Java后端jar包
├── admin/                      # 管理后台静态文件
│   ├── index.html
│   ├── assets/
│   └── ...
└── mobile/                     # 移动端H5静态文件
    ├── index.html
    ├── static/
    └── ...
```

## 使用方法

### 1. 执行部署脚本

```bash
./deploy.sh
```

脚本会自动执行以下操作：
1. 编译Java后端（Maven打包）
2. 编译管理后台（Vite构建）
3. 编译移动端H5（uni-app构建，如果工具可用）

### 2. 前置要求

- **Java**: JDK 17+
- **Maven**: 3.6+
- **Node.js**: 16+
- **npm**: 8+
- **uni-app构建工具**（可选）:
  - HBuilderX，或
  - uni-app CLI (`npm install -g @dcloudio/uni-cli`)

### 3. 配置说明

#### 管理后台 (novel-admin)
- Base路径: `/admin/`
- API请求: 使用相对路径 `/api`，会自动请求当前域名的后端接口

#### 移动端H5 (novel-app)
- Base路径: `/mobile/`
- API请求: 使用相对路径 `/api`，会自动请求当前域名的后端接口

#### Java后端 (novel-api)
- 端口: 8080
- Context Path: `/api`

## Web服务器配置

### Nginx配置示例

参考 `nginx.conf.example` 文件，主要配置如下：

```nginx
# 后端API代理
location /api {
    proxy_pass http://localhost:8080;
    # ... 其他代理配置
}

# 管理后台
location /admin {
    alias /path/to/public/admin;
    try_files $uri $uri/ /admin/index.html;
}

# 移动端H5
location /mobile {
    alias /path/to/public/mobile;
    try_files $uri $uri/ /mobile/index.html;
}
```

### 部署步骤

1. **执行部署脚本**:
   ```bash
   ./deploy.sh
   ```

2. **启动Java后端**:
   ```bash
   java -jar public/jar/novel-api.jar
   ```

3. **配置Web服务器**:
   - 将 `public/admin` 和 `public/mobile` 目录部署到Web服务器
   - 配置Nginx反向代理（参考 `nginx.conf.example`）
   - 确保 `/api` 路径代理到后端服务（localhost:8080）

4. **访问应用**:
   - 管理后台: `http://your-domain.com/admin`
   - 移动端: `http://your-domain.com/mobile`
   - API接口: `http://your-domain.com/api`

## 注意事项

1. **uni-app构建**: 如果脚本无法自动构建uni-app，请手动使用HBuilderX或uni-app CLI构建H5版本，然后将输出目录内容复制到 `public/mobile/` 目录。

2. **API路径**: 前端应用已配置为使用相对路径 `/api`，会自动请求当前域名的后端接口，无需修改配置。

3. **跨域问题**: 如果前后端不在同一域名，需要配置CORS。后端已配置允许跨域，但请根据实际情况调整。

4. **静态资源路径**: 管理后台和移动端的静态资源路径已自动配置，无需手动修改。

5. **生产环境**: 部署到生产环境前，请确保：
   - 修改数据库连接配置
   - 修改JWT密钥
   - 配置HTTPS
   - 设置合适的日志级别

## 故障排查

### Java编译失败
- 检查JDK版本是否为17+
- 检查Maven是否正确安装
- 检查网络连接（下载依赖）

### 管理后台编译失败
- 检查Node.js和npm版本
- 运行 `cd novel-admin && npm install` 安装依赖
- 检查是否有语法错误

### uni-app构建失败
- 脚本会提示手动构建方法
- 使用HBuilderX构建H5版本
- 确保manifest.json中H5的base路径配置为 `/mobile/`
