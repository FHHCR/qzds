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
│           └── db/db-init.sql  # 数据库初始化脚本（唯一入口，幂等）
├── frontend/           # 前端（Vue 3）
│   └── src/
│       ├── api/        # 接口封装（user/product/cart/order/system）
│       ├── views/      # 页面（Login/Home/ProductList/ProductDetail/Cart/OrderList/OrderDetail）
│       ├── router/     # 路由与登录守卫
│       ├── stores/     # Pinia 状态
│       └── utils/      # axios 封装（自动携带 JWT，按 code 解包）
└── assets/             # 设计资产（不入库）
```

## 快速启动

### 1. 初始化数据库

```bash
cd backend
mysql -uroot -p123456 -e "source src/main/resources/db/db-init.sql"
```

> 脚本幂等：创建六张表（user/category/product/cart/order/order_item）+ 12 个演示商品 + 5 个分类，可重复执行。

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
- 商品：分类列表、分页列表（关键词/分类筛选）、详情（下架返回 404）
- 购物车：加购、列表、改数量、删除（下单自动清理）
- 订单：勾选结算下单（事务扣库存 + 明细快照）、列表/详情、模拟支付、取消（恢复库存）

### 接口清单

免登录：

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | /api/user/register | 注册 |
| POST | /api/user/login | 登录，返回 JWT |
| GET | /api/product/category/list | 商品分类 |
| GET | /api/product/list | 商品分页（page/size/categoryId/keyword） |
| GET | /api/product/{id} | 商品详情（下架 404） |
| GET | /api/system/ping | 健康检查 |

需登录（`Authorization: Bearer <token>`）：

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | /api/user/me | 当前用户信息 |
| POST | /api/cart | 加购 {productId, quantity} |
| GET | /api/cart/list | 购物车列表 |
| PUT | /api/cart/{id} | 修改数量 {quantity} |
| DELETE | /api/cart/{id} | 删除条目 |
| POST | /api/order | 创建订单 {cartIds[]} |
| GET | /api/order/list | 订单列表 |
| GET | /api/order/{id} | 订单详情 |
| PUT | /api/order/{id}/pay | 支付（模拟） |
| PUT | /api/order/{id}/cancel | 取消（仅待支付） |

演示账号：`demo_store/123456`、`cartuser/123456`。

## 演进路线

1. **阶段一（当前）**：Spring Boot 单体，认证鉴权 + 商品 + 购物车 + 订单主流程
2. **阶段二**：收货地址/结算页、商品搜索、后台管理（商品/订单管理端）
3. **阶段三**：演进 Spring Cloud（Nacos 注册中心/配置中心 + OpenFeign + Gateway），各业务域独立为微服务

## 开发约定

- 请求对象放 `entity/dto`（`XxxDTO`），返回对象放 `entity/vo`（`XxxVO`），数据库对象放 `entity/po`
- 业务统一返回 `Result{code, message, data}`，`code=0` 表示成功
- 数据库脚本统一维护在 `db-init.sql`，幂等可重复执行
- 订单主键为雪花 id，超出 JS 安全整数，序列化为字符串（`tools.jackson` 注解），前端按字符串透传
