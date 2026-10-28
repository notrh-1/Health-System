# 健康管理系统 (jkglxt)

## 项目简介
健康管理系统后端，基于 Spring Boot 3.x + MyBatis Plus 开发。

## 技术栈
- Spring Boot 3.2.0
- MyBatis Plus 3.5.5
- Spring Security + JWT
- MySQL 8.0
- Hutool
- Lombok

## 快速开始

### 1. 数据库初始化
执行 `schema.sql` 文件创建数据库和表结构

```bash
mysql -u root -p < schema.sql
```

### 2. 修改配置
编辑 `src/main/resources/application-dev.yml` 中的数据库连接信息

### 3. 启动项目
```bash
mvn spring-boot:run
```

或使用 IDE 运行 `JkglxtApplication.java`

### 4. 访问项目
- 后端地址: http://localhost:8080/api
- Swagger 文档: http://localhost:8080/api/doc (如果配置了Swagger)

## 默认账号
- 管理员: admin / admin
- 测试用户: test / test

## 项目结构
```
jkglxt/
├── src/main/java/com/jkglxt/
│   ├── common/          # 通用类
│   ├── config/          # 配置类
│   ├── controller/      # 控制器
│   ├── dto/             # 数据传输对象
│   ├── entity/          # 实体类
│   ├── exception/       # 异常处理
│   ├── filter/          # 过滤器
│   ├── mapper/          # MyBatis Mapper
│   ├── service/         # 业务逻辑
│   └── util/            # 工具类
└── src/main/resources/
    ├── application.yml
    └── application-dev.yml
```

## API 接口
- POST /api/auth/login - 用户登录
- POST /api/auth/register - 用户注册
- GET /api/user/list - 用户列表
- GET /api/interface/list - 接口列表
- GET /api/project/list - 项目列表
- 更多接口详见代码...
