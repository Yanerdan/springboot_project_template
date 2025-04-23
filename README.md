### 项目概述

这是一个基于 Spring Boot 构建的后端项目，主要实现了用户账户的注册、登录、密码重置等功能，同时集成了 Spring Security 进行权限管理，使用 JWT（JSON Web Token）进行身份验证，RabbitMQ 进行消息队列处理，Redis 进行缓存和限流操作，MyBatis-Plus 进行数据库操作。

### 项目运行流程

#### 1. 启动项目

项目的入口类是`MyProjectBackendApplication`，通过`SpringApplication.run(MyProjectBackendApplication.class, args)`启动 Spring Boot 应用程序。在启动过程中，Spring Boot 会自动加载配置类、组件和过滤器等。

#### 2. 过滤器处理

- **CorsFilter**：设置跨域请求头，允许来自`http://localhost:5173`的请求，处理跨域问题。
- **FlowLimitFilter**：对请求进行限流处理，使用 Redis 记录请求数量和频率，当请求频率超过限制时，返回 403 状态码和提示信息。

#### 3. 安全配置

`SecurityConfiguration`类配置了 Spring Security 的过滤器链，定义了哪些请求需要认证，哪些请求可以匿名访问，以及登录、注销、异常处理等逻辑。

#### 4. 控制器处理请求

控制器负责接收客户端的请求，并调用相应的服务层方法进行处理。例如，`AuthorizeController`处理用户注册、登录、密码重置等请求。

#### 5. 服务层处理业务逻辑

服务层实现具体的业务逻辑，如用户注册、密码重置等。例如，`AccountServiceImpl`实现了`AccountService`接口，处理用户账户相关的业务逻辑。

#### 6. 数据访问层操作数据库

使用 MyBatis-Plus 进行数据库操作，`AccountMapper`继承自`BaseMapper<Account>`，可以进行基本的增删改查操作。

#### 7. 消息队列处理

使用 RabbitMQ 进行消息队列处理，当用户请求验证码时，会将验证码信息发送到`mail`队列，`MailQueueListener`监听该队列，接收到消息后发送邮件。

### 具体实现功能

#### 1. 用户注册

- 用户通过`/api/auth/register`接口发送注册请求，请求中包含邮箱、验证码、用户名和密码。
- 服务层验证验证码的有效性，检查邮箱和用户名是否已存在，若验证通过，则将用户信息保存到数据库。

#### 2. 用户登录

- 用户通过`/api/auth/login`接口发送登录请求，请求中包含用户名和密码。
- Spring Security 验证用户名和密码，若验证通过，则生成 JWT 并返回给客户端。

#### 3. 密码重置

- 用户通过`/api/auth/ask-code`接口请求重置密码的验证码，请求中包含邮箱和类型（`reset`）。
- 服务层生成验证码并发送到`mail`队列，同时将验证码保存到 Redis 中。
- 用户通过`/api/auth/reset-confirm`接口验证验证码的有效性。
- 用户通过`/api/auth/reset-password`接口重置密码，服务层验证验证码后更新用户的密码。

#### 4. 限流和跨域处理

- `FlowLimitFilter`对请求进行限流处理，防止恶意请求。
- `CorsFilter`处理跨域请求，允许来自指定域名的请求。

### 交互方式

#### 1. 客户端与服务器交互

客户端通过 HTTP 请求与服务器进行交互，请求中包含必要的参数，服务器处理请求后返回 JSON 格式的响应。

#### 2. 服务器内部组件交互

- 控制器调用服务层方法处理业务逻辑。
- 服务层调用数据访问层方法进行数据库操作。
- 服务层通过 RabbitMQ 发送消息，消息队列监听器接收消息并处理。

### 功能连贯方式

#### 1. 用户注册流程

- 客户端发送请求到`AuthorizeController`的`register`方法。
- `register`方法调用`AccountService`的`registerEmailAccount`方法。
- `registerEmailAccount`方法验证验证码，检查邮箱和用户名是否已存在，若验证通过，则调用`AccountMapper`的`save`方法将用户信息保存到数据库。

#### 2. 用户登录流程

- 客户端发送请求到`/api/auth/login`接口。
- Spring Security 验证用户名和密码，调用`AccountService`的`loadUserByUsername`方法获取用户信息。
- 若验证通过，`SecurityConfiguration`的`onAuthenticationSuccess`方法生成 JWT 并返回给客户端。

#### 3. 密码重置流程

- 客户端发送请求到`AuthorizeController`的`askVerifyCode`方法。
- `askVerifyCode`方法调用`AccountService`的`registerEmailVerifyCode`方法。
- `registerEmailVerifyCode`方法生成验证码并发送到`mail`队列，同时将验证码保存到 Redis 中。
- 客户端发送请求到`AuthorizeController`的`resetConfirm`方法，验证验证码的有效性。
- 客户端发送请求到`AuthorizeController`的`resetConfirm`方法（这里应该是`resetPassword`方法），重置密码。
- `resetPassword`方法调用`AccountService`的`resetEmailAccountPassword`方法，验证验证码后更新用户的密码。

通过以上流程，各个功能模块之间相互协作，实现了用户账户的注册、登录、密码重置等功能。
