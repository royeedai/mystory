# API接口文档

本文档详细说明小说阅读平台的所有API接口。

## 基础信息

### 基础URL

- 开发环境：`http://localhost:8080/api`
- 生产环境：`https://your-domain.com/api`

### 认证方式

使用JWT Token进行认证，需要在请求头中携带：

```
Authorization: Bearer {token}
```

### 统一响应格式

所有接口使用统一的响应格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

**响应码说明**：
- `200`: 成功
- `400`: 请求参数错误
- `401`: 未认证或token过期
- `403`: 无权限
- `404`: 资源不存在
- `500`: 服务器错误

## 认证接口

### 1. 后台登录

**接口**: `POST /api/auth/login`

**请求体**:
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**响应**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "nickname": "管理员",
      "role": "ADMIN"
    }
  }
}
```

### 2. 微信小程序登录

**接口**: `POST /api/auth/wx-login`

**请求体**:
```json
{
  "code": "wx_login_code_from_miniprogram"
}
```

**响应**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "openid": "wx_openid",
      "nickname": "用户昵称",
      "avatar": "头像URL",
      "trialViewTime": 3600
    }
  }
}
```

## 小说接口

### 1. 获取小说列表

**接口**: `GET /api/novel/list`

**请求参数**:
- `page`: 页码（默认1）
- `size`: 每页数量（默认10）
- `keyword`: 搜索关键词（可选）
- `category`: 分类（可选）
- `status`: 状态（可选，小程序端只返回APPROVED状态）

**响应**:
```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "id": 1,
        "title": "小说标题",
        "authorId": 1,
        "authorName": "作者名",
        "cover": "封面URL",
        "description": "简介",
        "category": "分类",
        "status": "APPROVED",
        "viewCount": 1000,
        "createTime": "2024-01-01 00:00:00"
      }
    ],
    "total": 100,
    "page": 1,
    "size": 10
  }
}
```

### 2. 获取小说详情

**接口**: `GET /api/novel/{id}`

**响应**:
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "title": "小说标题",
    "authorId": 1,
    "authorName": "作者名",
    "cover": "封面URL",
    "description": "简介",
    "category": "分类",
    "status": "APPROVED",
    "viewCount": 1000,
    "createTime": "2024-01-01 00:00:00"
  }
}
```

### 3. 创建小说（管理端）

**接口**: `POST /api/admin/novel`

**认证**: 需要登录（作者或管理员）

**请求体**:
```json
{
  "title": "小说标题",
  "cover": "封面URL",
  "description": "简介",
  "category": "分类"
}
```

**响应**:
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 1
  }
}
```

### 4. 更新小说（管理端）

**接口**: `PUT /api/admin/novel/{id}`

**认证**: 需要登录（作者只能更新自己的小说，管理员可以更新所有小说）

**请求体**:
```json
{
  "title": "小说标题",
  "cover": "封面URL",
  "description": "简介",
  "category": "分类"
}
```

### 5. 删除小说（管理端）

**接口**: `DELETE /api/admin/novel/{id}`

**认证**: 需要登录（作者只能删除自己的小说，管理员可以删除所有小说）

### 6. 提交审核（管理端）

**接口**: `POST /api/admin/novel/{id}/audit`

**认证**: 需要登录（作者）

**请求体**:
```json
{
  "status": "PENDING"
}
```

### 7. 审核小说（管理端）

**接口**: `POST /api/admin/novel/{id}/audit`

**认证**: 需要登录（管理员）

**请求体**:
```json
{
  "status": "APPROVED",
  "remark": "审核通过"
}
```

**状态值**:
- `DRAFT`: 草稿
- `PENDING`: 待审核
- `APPROVED`: 已审核
- `REJECTED`: 已拒绝

## 章节接口

### 1. 获取章节列表

**接口**: `GET /api/chapter/novel/{novelId}`

**请求参数**:
- `novelId`: 小说ID（路径参数）

**响应**:
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "novelId": 1,
      "title": "第一章",
      "chapterNum": 1,
      "wordCount": 3000,
      "createTime": "2024-01-01 00:00:00"
    }
  ]
}
```

### 2. 获取章节内容（分页）

**接口**: `GET /api/chapter/{id}/content`

**请求参数**:
- `id`: 章节ID（路径参数）
- `page`: 页码（默认1）
- `size`: 每页字数（默认2000）

**响应**:
```json
{
  "code": 200,
  "data": {
    "chapterId": 1,
    "title": "第一章",
    "content": "<p>章节内容HTML...</p>",
    "page": 1,
    "totalPages": 5,
    "wordCount": 10000
  }
}
```

### 3. 创建章节（管理端）

**接口**: `POST /api/admin/chapter/novel/{novelId}`

**认证**: 需要登录（作者只能为自己的小说创建章节，管理员可以为所有小说创建章节）

**请求体**:
```json
{
  "title": "第一章",
  "content": "<p>章节内容HTML...</p>",
  "chapterNum": 1
}
```

**响应**:
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 1,
    "wordCount": 3000
  }
}
```

### 4. 更新章节（管理端）

**接口**: `PUT /api/admin/chapter/{id}`

**认证**: 需要登录（作者只能更新自己的章节，管理员可以更新所有章节）

**请求体**:
```json
{
  "title": "第一章",
  "content": "<p>章节内容HTML...</p>",
  "chapterNum": 1
}
```

### 5. 删除章节（管理端）

**接口**: `DELETE /api/admin/chapter/{id}`

**认证**: 需要登录（作者只能删除自己的章节，管理员可以删除所有章节）

### 6. 获取章节详情（管理端）

**接口**: `GET /api/admin/chapter/{id}`

**认证**: 需要登录

**响应**:
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "novelId": 1,
    "title": "第一章",
    "content": "<p>章节内容HTML...</p>",
    "chapterNum": 1,
    "wordCount": 3000,
    "createTime": "2024-01-01 00:00:00"
  }
}
```

## 阅读接口

### 1. 获取阅读记录

**接口**: `GET /api/reading/record`

**认证**: 需要登录（小程序用户）

**请求参数**:
- `novelId`: 小说ID（可选，不传则返回所有阅读记录）

**响应**:
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "userId": 1,
      "novelId": 1,
      "novelTitle": "小说标题",
      "chapterId": 1,
      "chapterTitle": "第一章",
      "pageIndex": 1,
      "lastReadTime": "2024-01-01 00:00:00"
    }
  ]
}
```

### 2. 保存阅读进度

**接口**: `POST /api/reading/record`

**认证**: 需要登录（小程序用户）

**请求体**:
```json
{
  "novelId": 1,
  "chapterId": 1,
  "pageIndex": 1
}
```

**响应**:
```json
{
  "code": 200,
  "message": "保存成功"
}
```

### 3. 检查试看权限

**接口**: `GET /api/reading/check-trial`

**认证**: 需要登录（小程序用户）

**请求参数**:
- `novelId`: 小说ID

**响应**:
```json
{
  "code": 200,
  "data": {
    "canRead": true,
    "trialViewTime": 3600,
    "message": "可以阅读"
  }
}
```

**响应说明**:
- `canRead`: 是否可以阅读
- `trialViewTime`: 剩余试看时间（秒）
- `message`: 提示信息

## 用户接口

### 1. 获取用户信息

**接口**: `GET /api/user/info`

**认证**: 需要登录（小程序用户）

**响应**:
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "openid": "wx_openid",
    "nickname": "用户昵称",
    "avatar": "头像URL",
    "trialViewTime": 3600,
    "isTrialEnabled": true
  }
}
```

### 2. 广告激励奖励

**接口**: `POST /api/user/ad-reward`

**认证**: 需要登录（小程序用户）

**请求体**:
```json
{
  "rewardTime": 300
}
```

**响应**:
```json
{
  "code": 200,
  "message": "奖励成功",
  "data": {
    "trialViewTime": 3900
  }
}
```

**说明**: `rewardTime` 为奖励的试看时间（秒），通常为300秒（5分钟）

## 管理端接口

### 1. 获取审核列表

**接口**: `GET /api/admin/novel/audit-list`

**认证**: 需要登录（管理员）

**请求参数**:
- `page`: 页码（默认1）
- `size`: 每页数量（默认10）
- `status`: 状态（可选，PENDING/APPROVED/REJECTED）

**响应**:
```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "id": 1,
        "title": "小说标题",
        "authorId": 1,
        "authorName": "作者名",
        "status": "PENDING",
        "auditUserId": null,
        "auditTime": null,
        "auditRemark": null,
        "createTime": "2024-01-01 00:00:00"
      }
    ],
    "total": 10,
    "page": 1,
    "size": 10
  }
}
```

### 2. 获取用户列表（管理端）

**接口**: `GET /api/admin/user/list`

**认证**: 需要登录（管理员）

**请求参数**:
- `page`: 页码（默认1）
- `size`: 每页数量（默认10）
- `keyword`: 搜索关键词（可选）

**响应**:
```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "id": 1,
        "openid": "wx_openid",
        "nickname": "用户昵称",
        "avatar": "头像URL",
        "trialViewTime": 3600,
        "createTime": "2024-01-01 00:00:00"
      }
    ],
    "total": 100,
    "page": 1,
    "size": 10
  }
}
```

### 3. 更新用户状态（管理端）

**接口**: `PUT /api/admin/user/{id}/status`

**认证**: 需要登录（管理员）

**请求体**:
```json
{
  "status": 1
}
```

**状态值**:
- `0`: 禁用
- `1`: 启用

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未认证或token过期 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |

## 请求示例

### cURL示例

```bash
# 登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 获取小说列表（需要token）
curl -X GET http://localhost:8080/api/novel/list \
  -H "Authorization: Bearer {token}"

# 创建小说（需要token）
curl -X POST http://localhost:8080/api/admin/novel \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"title":"新小说","description":"简介","category":"分类"}'
```

### JavaScript示例

```javascript
// 登录
const loginResponse = await fetch('http://localhost:8080/api/auth/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    username: 'admin',
    password: 'admin123'
  })
});

const loginData = await loginResponse.json();
const token = loginData.data.token;

// 获取小说列表
const novelResponse = await fetch('http://localhost:8080/api/novel/list', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

const novelData = await novelResponse.json();
console.log(novelData);
```

## 注意事项

1. **Token过期**: Token过期后需要重新登录获取新token
2. **权限控制**: 管理端接口需要相应的权限，普通用户无法访问
3. **分页参数**: 列表接口都支持分页，默认每页10条
4. **时间格式**: 所有时间字段使用 `yyyy-MM-dd HH:mm:ss` 格式
5. **内容格式**: 章节内容使用HTML格式存储和传输
