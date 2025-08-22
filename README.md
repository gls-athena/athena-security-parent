# Athena Security 安全认证框架

![GitHub](https://img.shields.io/github/license/athena/athena-security)
![Maven Central](https://img.shields.io/maven-central/v/com.gls.athena.security/athena-security-parent)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6.x-brightgreen)
![OAuth2](https://img.shields.io/badge/OAuth2-2.1-blue)

Athena Security 是一个基于 Spring Security 的企业级安全认证框架，提供了完整的认证授权解决方案，支持OAuth2、第三方登录、IAM服务等功能。

## ✨ 特性

- 🔐 **完整的OAuth2支持** - 包含授权服务器、资源服务器和客户端
- 🌐 **多平台第三方登录** - 支持微信、飞书等主流平台
- 🛡️ **企业级安全** - 基于Spring Security的安全防护
- 🎯 **验证码集成** - 内置验证码功能
- 📱 **RESTful API** - 完整的REST API支持
- 🔄 **Session管理** - 灵活的会话管理策略
- 🏗️ **模块化设计** - 高度模块化，按需集成
- 🚀 **开箱即用** - 提供完整的示例项目

## 🏗️ 项目架构

```
athena-security-parent
├── athena-security-bom                    # 依赖管理BOM
├── athena-security-project                # 核心项目模块
│   ├── athena-security-common             # 公共组件
│   ├── athena-security-captcha            # 验证码功能
│   ├── athena-security-web                # Web安全配置
│   ├── athena-security-rest               # REST API支持
│   ├── athena-security-oauth2-client      # OAuth2客户端
│   ├── athena-security-oauth2-authorization-server  # OAuth2授权服务器
│   ├── athena-security-oauth2-resource-server       # OAuth2资源服务器
│   ├── athena-security-oauth2-client-feishu         # 飞书OAuth2客户端
│   └── athena-security-oauth2-client-wechat         # 微信OAuth2客户端
└── athena-security-samples                # 示例项目
    └── athena-security-iam                # IAM身份认证管理示例
        ├── athena-security-iam-boot       # IAM启动模块
        ├── athena-security-iam-sdk        # IAM SDK
        └── db                             # 数据库脚本
```

## 📦 核心模块

### athena-security-web

Web安全核心模块，提供基础的Web安全配置和定制化支持。

**主要功能：**

- Spring Security Web配置
- 认证成功/失败处理器
- Session管理定制
- OAuth2资源服务器集成
- 异常处理定制

### athena-security-oauth2-authorization-server

OAuth2授权服务器模块，基于Spring Authorization Server实现。

**主要功能：**

- OAuth2授权码模式
- 客户端凭证模式
- 刷新令牌支持
- PKCE支持
- OpenID Connect支持

### athena-security-oauth2-client

OAuth2客户端模块，支持作为OAuth2客户端接入其他服务。

**主要功能：**

- 授权码流程
- 客户端凭证流程
- 令牌刷新
- 用户信息获取

### athena-security-captcha

验证码功能模块，提供多种验证码实现。

**主要功能：**

- 图形验证码
- 短信验证码
- 邮箱验证码
- 滑块验证码

### athena-security-oauth2-client-feishu / wechat

第三方平台OAuth2客户端，支持飞书和微信平台的OAuth2登录。

## 🚀 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- Redis 6.0+
- MySQL 8.0+

### 2. 依赖引入

在你的项目中引入BOM依赖：

```xml

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.github.gls-athena.security</groupId>
            <artifactId>athena-security-bom</artifactId>
            <version>${revision}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

根据需要引入具体模块：

```xml

<dependencies>
    <!-- Web安全模块 -->
    <dependency>
        <groupId>io.github.gls-athena.security.web</groupId>
        <artifactId>athena-security-web</artifactId>
    </dependency>

    <!-- OAuth2授权服务器 -->
    <dependency>
        <groupId>io.github.gls-athena.security.oauth2.authorization.server</groupId>
        <artifactId>athena-security-oauth2-authorization-server</artifactId>
    </dependency>

    <!-- 验证码功能 -->
    <dependency>
        <groupId>io.github.gls-athena.security.captcha</groupId>
        <artifactId>athena-security-captcha</artifactId>
    </dependency>
</dependencies>
```

### 3. 基础配置

在你的Spring Boot应用中启用安全功能：

```java

@SpringBootApplication
@EnableWebSecurity
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 4. 运行示例项目

克隆项目并运行IAM示例：

```bash
# 克隆项目
git clone <repository-url>
cd athena-security-parent

# 编译项目
mvn clean install

# 运行IAM示例
cd athena-security-samples/athena-security-iam/athena-security-iam-boot
mvn spring-boot:run
```

访问 `http://localhost:8080` 查看IAM管理界面。

## 📚 详细文档

### OAuth2配置示例

```yaml
spring:
  security:
    oauth2:
      authorization-server:
        client:
          client-1:
            registration:
              client-id: "client-1"
              client-secret: "{noop}secret"
              client-authentication-methods:
                - "client_secret_basic"
              authorization-grant-types:
                - "authorization_code"
                - "refresh_token"
              redirect-uris:
                - "http://localhost:8080/login/oauth2/code/client-1"
              scopes:
                - "read"
                - "write"
```

### 第三方登录配置

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          wechat:
            client-id: "your-wechat-app-id"
            client-secret: "your-wechat-app-secret"
            scope: "snsapi_login"
            authorization-grant-type: "authorization_code"
            redirect-uri: "{baseUrl}/login/oauth2/code/wechat"
          feishu:
            client-id: "your-feishu-app-id"
            client-secret: "your-feishu-app-secret"
            scope: "user:email"
            authorization-grant-type: "authorization_code"
            redirect-uri: "{baseUrl}/login/oauth2/code/feishu"
```

## 🛠️ 开发指南

### 自定义认证处理器

```java

@Component
public class CustomAuthenticationSuccessHandler
        extends DefaultAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) {
        // 自定义成功处理逻辑
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
```

### 自定义OAuth2客户端

```java

@Configuration
public class OAuth2ClientConfig {

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(
                customClientRegistration()
        );
    }

    private ClientRegistration customClientRegistration() {
        return ClientRegistration.withRegistrationId("custom")
                .clientId("client-id")
                .clientSecret("client-secret")
                .scope("read", "write")
                .authorizationUri("https://provider.com/oauth2/authorize")
                .tokenUri("https://provider.com/oauth2/token")
                .userInfoUri("https://provider.com/oauth2/userinfo")
                .userNameAttributeName("id")
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .build();
    }
}
```

## 🤝 贡献指南

我们欢迎所有形式的贡献！请查看 [CONTRIBUTING.md](CONTRIBUTING.md) 了解如何参与项目开发。

### 开发流程

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 📄 许可证

本项目采用 [MIT License](LICENSE) 许可证。

## 🔗 相关链接

- [Spring Security 官方文档](https://spring.io/projects/spring-security)
- [Spring Authorization Server](https://spring.io/projects/spring-authorization-server)
- [OAuth2 规范](https://tools.ietf.org/html/rfc6749)
- [OpenID Connect](https://openid.net/connect/)

## 📞 联系我们

- 项目主页: [Athena Security](https://github.com/athena/athena-security)
- 问题反馈: [Issues](https://github.com/athena/athena-security/issues)
- 邮箱: athena-security@gls.com

---

⭐ 如果这个项目对你有帮助，请给我们一个Star！
