# 部署文档

本文档介绍如何将小说阅读平台部署到生产环境。

## 部署架构

```
┌─────────────┐
│   Nginx     │  (反向代理 + 静态文件服务)
└──────┬──────┘
       │
       ├─── /api/* ──────> Spring Boot (8080)
       ├─── /admin/* ────> 管理后台静态文件
       └─── /mobile/* ───> 移动端H5静态文件
```

## 前置要求

### 服务器要求

- **操作系统**: Linux (推荐 Ubuntu 20.04+ 或 CentOS 7+)
- **CPU**: 2核+
- **内存**: 4GB+
- **磁盘**: 20GB+

### 软件要求

- **Java**: JDK 17+
- **Maven**: 3.6+
- **Node.js**: 16+
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **Nginx**: 1.18+

## 一键部署

### 1. 执行部署脚本

项目根目录提供了 `deploy.sh` 一键部署脚本：

```bash
chmod +x deploy.sh
./deploy.sh
```

脚本会自动执行以下操作：
1. 编译Java后端（Maven打包）
2. 编译管理后台（Vite构建）
3. 编译移动端H5（如果工具可用）

### 2. 部署目录结构

部署完成后，会在项目根目录生成 `public` 目录：

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

## 手动部署步骤

### 1. 编译后端

```bash
cd novel-api
mvn clean package -DskipTests
cp target/novel-api-*.jar ../public/jar/novel-api.jar
```

### 2. 编译管理后台

```bash
cd novel-admin
npm install
npm run build
cp -r dist/* ../public/admin/
```

### 3. 编译移动端H5

#### 方式一：使用HBuilderX

1. 打开HBuilderX
2. 导入 `novel-app` 目录
3. 发行 -> 网站-H5 -> 发行
4. 将构建输出目录内容复制到 `public/mobile/`

#### 方式二：使用uni-app CLI

```bash
npm install -g @dcloudio/uni-cli
cd novel-app
uni build -p h5
cp -r dist/build/h5/* ../public/mobile/
```

## 服务器配置

### 1. 数据库配置

#### 创建数据库

```sql
CREATE DATABASE novel_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 导入表结构

```bash
mysql -u root -p novel_db < database.sql
```

#### 创建数据库用户（推荐）

```sql
CREATE USER 'novel_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON novel_db.* TO 'novel_user'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Redis配置

#### 启动Redis

```bash
# 使用systemd启动（Ubuntu/Debian）
sudo systemctl start redis
sudo systemctl enable redis

# 或手动启动
redis-server /etc/redis/redis.conf
```

#### 配置Redis密码（推荐）

编辑 `/etc/redis/redis.conf`：

```
requirepass your_redis_password
```

### 3. 后端配置

#### 修改配置文件

编辑 `novel-api/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/novel_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: novel_user
    password: your_password
  redis:
    host: localhost
    port: 6379
    password: your_redis_password
    database: 0

jwt:
  secret: your-production-secret-key-change-this  # ⚠️ 必须修改
  expiration: 86400000
  app-expiration: 604800000

wx:
  appid: ${WX_APPID:}
  secret: ${WX_SECRET:}

server:
  port: 8080
  servlet:
    context-path: /api

logging:
  level:
    com.novel: info  # 生产环境使用info级别
    org.springframework.security: warn
```

#### 设置环境变量

```bash
export WX_APPID=your_appid
export WX_SECRET=your_secret
```

#### 启动后端服务

```bash
# 方式一：直接运行
java -jar public/jar/novel-api.jar

# 方式二：使用systemd（推荐）
sudo nano /etc/systemd/system/novel-api.service
```

创建systemd服务文件：

```ini
[Unit]
Description=Novel API Service
After=network.target mysql.service redis.service

[Service]
Type=simple
User=www-data
WorkingDirectory=/path/to/novel-platform/public/jar
ExecStart=/usr/bin/java -jar -Xms512m -Xmx1024m novel-api.jar
Restart=always
RestartSec=10
Environment="WX_APPID=your_appid"
Environment="WX_SECRET=your_secret"

[Install]
WantedBy=multi-user.target
```

启动服务：

```bash
sudo systemctl daemon-reload
sudo systemctl start novel-api
sudo systemctl enable novel-api
```

### 4. Nginx配置

#### 安装Nginx

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install nginx

# CentOS/RHEL
sudo yum install nginx
```

#### 配置Nginx

参考项目根目录的 `nginx.conf.example`，创建配置文件：

```bash
sudo nano /etc/nginx/sites-available/novel-platform
```

配置内容：

```nginx
server {
    listen 80;
    server_name your-domain.com;  # 修改为您的域名

    # 后端API代理
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # 超时设置
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
        
        # WebSocket支持（如果需要）
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }

    # 管理后台
    location /admin {
        alias /path/to/public/admin;  # 修改为实际路径
        try_files $uri $uri/ /admin/index.html;
        index index.html;
    }

    # 移动端H5
    location /mobile {
        alias /path/to/public/mobile;  # 修改为实际路径
        try_files $uri $uri/ /mobile/index.html;
        index index.html;
    }

    # 根路径重定向到移动端
    location = / {
        return 301 /mobile/;
    }

    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # 日志
    access_log /var/log/nginx/novel-platform-access.log;
    error_log /var/log/nginx/novel-platform-error.log;
}
```

启用配置：

```bash
sudo ln -s /etc/nginx/sites-available/novel-platform /etc/nginx/sites-enabled/
sudo nginx -t  # 测试配置
sudo systemctl reload nginx
```

### 5. HTTPS配置（推荐）

使用Let's Encrypt免费SSL证书：

```bash
# 安装Certbot
sudo apt install certbot python3-certbot-nginx

# 获取证书
sudo certbot --nginx -d your-domain.com

# 自动续期
sudo certbot renew --dry-run
```

## 部署检查清单

### 部署前检查

- [ ] 数据库已创建并导入表结构
- [ ] Redis服务已启动
- [ ] 后端配置文件已修改（数据库、Redis、JWT密钥）
- [ ] 微信小程序AppID和Secret已配置
- [ ] 测试账号密码已重新生成BCrypt加密值

### 部署后检查

- [ ] 后端服务正常启动（检查日志）
- [ ] Nginx配置正确（`nginx -t`）
- [ ] API接口可访问（`curl http://localhost:8080/api/health`）
- [ ] 管理后台可访问（`http://your-domain.com/admin`）
- [ ] 移动端可访问（`http://your-domain.com/mobile`）
- [ ] 微信登录功能正常
- [ ] 静态资源加载正常

## 性能优化

### 1. JVM参数优化

```bash
java -jar -Xms1g -Xmx2g -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+HeapDumpOnOutOfMemoryError \
  novel-api.jar
```

### 2. Nginx优化

```nginx
# 启用gzip压缩
gzip on;
gzip_vary on;
gzip_min_length 1024;
gzip_types text/plain text/css text/xml text/javascript application/json application/javascript application/xml+rss;

# 连接池优化
upstream backend {
    server localhost:8080 max_fails=3 fail_timeout=30s;
    keepalive 32;
}
```

### 3. MySQL优化

编辑 `/etc/mysql/my.cnf`：

```ini
[mysqld]
innodb_buffer_pool_size = 1G
innodb_log_file_size = 256M
max_connections = 200
```

### 4. Redis优化

编辑 `/etc/redis/redis.conf`：

```
maxmemory 512mb
maxmemory-policy allkeys-lru
```

## 监控和日志

### 1. 应用日志

后端日志位置：`logs/` 目录（如果配置了日志文件）

查看日志：

```bash
tail -f logs/novel-api.log
```

### 2. Nginx日志

```bash
# 访问日志
tail -f /var/log/nginx/novel-platform-access.log

# 错误日志
tail -f /var/log/nginx/novel-platform-error.log
```

### 3. 系统监控

使用系统工具监控：

```bash
# CPU和内存
top
htop

# 磁盘使用
df -h

# 网络连接
netstat -tulpn
```

## 故障排查

### 后端服务无法启动

1. 检查Java版本：`java -version`
2. 检查端口占用：`netstat -tulpn | grep 8080`
3. 查看日志：`journalctl -u novel-api -f`

### Nginx 502错误

1. 检查后端服务是否运行：`systemctl status novel-api`
2. 检查后端日志
3. 检查Nginx错误日志：`tail -f /var/log/nginx/error.log`

### 静态资源404

1. 检查文件路径是否正确
2. 检查文件权限：`ls -la /path/to/public/admin`
3. 检查Nginx配置中的alias路径

### 数据库连接失败

1. 检查MySQL服务：`systemctl status mysql`
2. 检查数据库用户权限
3. 检查防火墙设置

## 备份和恢复

### 数据库备份

```bash
# 备份
mysqldump -u root -p novel_db > backup_$(date +%Y%m%d).sql

# 恢复
mysql -u root -p novel_db < backup_20240101.sql
```

### 应用备份

```bash
# 备份整个public目录
tar -czf novel-platform-backup-$(date +%Y%m%d).tar.gz public/
```

## 安全建议

1. **修改默认密码**：所有默认密码必须修改
2. **使用HTTPS**：生产环境必须启用HTTPS
3. **防火墙配置**：只开放必要端口（80, 443, 22）
4. **定期更新**：及时更新系统和软件包
5. **日志审计**：定期检查日志，发现异常
6. **权限控制**：使用最小权限原则
7. **JWT密钥**：使用强随机密钥，定期更换

## 更新部署

### 更新后端

```bash
# 1. 备份当前版本
cp public/jar/novel-api.jar public/jar/novel-api.jar.backup

# 2. 重新编译
cd novel-api
mvn clean package -DskipTests
cp target/novel-api-*.jar ../public/jar/novel-api.jar

# 3. 重启服务
sudo systemctl restart novel-api
```

### 更新前端

```bash
# 1. 备份当前版本
cp -r public/admin public/admin.backup
cp -r public/mobile public/mobile.backup

# 2. 重新编译（参考上面的编译步骤）

# 3. 无需重启，Nginx会自动使用新文件
```

## 回滚

如果新版本有问题，可以快速回滚：

```bash
# 回滚后端
sudo systemctl stop novel-api
cp public/jar/novel-api.jar.backup public/jar/novel-api.jar
sudo systemctl start novel-api

# 回滚前端
rm -rf public/admin public/mobile
cp -r public/admin.backup public/admin
cp -r public/mobile.backup public/mobile
```
