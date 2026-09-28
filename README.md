# 电商平台

前后端分离电商平台项目（单体起步，规划演进为微服务）。

## 技术栈

| 端 | 技术 |
|---|---|
| 后端 | Spring Boot 4.1.1 · Spring Security 7（JWT） · MyBatis-Plus 3.5.17 · MySQL 8 · Redis · springdoc-openapi |
| 前端 | Vue 3 · Vite · Pinia · Vue Router · Element Plus · Axios |

> 版本说明：MyBatis-Plus 需使用 `mybatis-plus-spring-boot4-starter`（Boot 4 专用坐标）；JDK 21 LTS。

## 目录结构

```
电商平台/
├── backend/            # 后端（Spring Boot 单体）
│   └── src/main/
│       ├── java/com/mall/
│       │   ├── controller/     # 接口层
│       │   ├── service/        # 业务接口
│       │   ├── service/impl/   # 业务实现
│       │   ├── mapper/         # MyBatis-Plus Mapper
│       │   ├── entity/
│       │   │   ├── dto/        # 请求对象 XxxDTO
│       │   │   ├── po/         # 数据库持久对象
│       │   │   └── vo/         # 返回对象 XxxVO
│       │   ├── config/         # 安全/跨域等配置
│       │   └── common/         # Result/异常/JWT 工具
│       └── resources/
│           ├── application.yml
│           └── db/db-init.sql  # 数据库初始化脚本（唯一入口）
├── frontend/           # 前端（Vue 3）
│   └── src/
│       ├── views/      # 页面（Login / Home）
│       ├── router/     # 路由与登录守卫
│       ├── stores/     # Pinia 状态
│       └── utils/      # axios 封装（自动携带 JWT）
└── assets/             # 设计资产（不入库）
```

## 快速启动

### 1. 初始化数据库

```bash
cd backend
mysql -uroot -p123456 -e "source src/main/resources/db/db-init.sql"
```

### 2. 启动后端（8080）

```bash
cd backend
$env:MYSQL_PASSWORD="123456"   # Windows PowerShell；密码与本机 MySQL 一致即可
mvn spring-boot:run
```

- 接口文档（Swagger）：http://127.0.0.1:8080/swagger-ui.html

### 3. 启动前端（5173）

```bash
cd frontend
npm install
npm run dev
```

访问 http://127.0.0.1:5173 ，开发环境 `/api` 由 Vite 代理到后端 8080。

## 已实现功能

- 用户注册、登录（BCrypt 加密，JWT 无状态鉴权，2 小时过期）
- 登录态守卫：未登录访问受保护接口自动跳转登录页
- 接口清单：
  - `POST /api/user/register` — 注册（免登录）
  - `POST /api/user/login` — 登录，返回 JWT（免登录）
  - `GET /api/user/me` — 当前用户信息（需 `Authorization: Bearer <token>`）
  - `GET /api/system/ping` — 健康检查（免登录）

## 演进路线

1. **阶段一（当前）**：Spring Boot 单体，模块按分层组织（controller/service/mapper/entity）
2. **阶段二**：按业务域拆分模块（用户/商品/订单/支付）
3. **阶段三**：演进 Spring Cloud（Nacos 注册中心/配置中心 + OpenFeign + Gateway），各业务域独立为微服务

## 开发约定

- 请求对象放 `entity/dto`（`XxxDTO`），返回对象放 `entity/vo`（`XxxVO`），数据库对象放 `entity/po`
- 业务统一返回 `Result{code, message, data}`，`code=0` 表示成功
- 数据库脚本统一维护在 `db-init.sql`，幂等可重复执行
