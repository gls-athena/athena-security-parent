# Athena Security 安全认证框架

<div align="center">

![GitHub license](https://img.shields.io/badge/license-Apache%202.0-blue.svg)
![Maven Central](https://img.shields.io/maven-central/v/io.github.gls-athena.security/athena-security-parent)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6.5.2-brightgreen)
![OAuth2](https://img.shields.io/badge/OAuth2-2.1-blue)
![Java](https://img.shields.io/badge/Java-21+-orange)

**企业级安全认证框架，基于 Spring Security 构建的完整认证授权解决方案**

[快速开始](#-快速开始) • [功能特性](#-功能特性) • [项目架构](#-项目架构) • [使用指南](#-使用指南) • [API文档](#-api文档)

</div>

## 📖 项目介绍

Athena Security 是一个基于 Spring Security 6.x 和 Spring Boot 3.x 的企业级安全认证框架，提供了完整的认证授权解决方案。框架支持
OAuth2 协议、第三方平台登录、验证码集成、会话管理等功能，采用模块化设计，开箱即用。

## ✨ 功能特性

### 🔐 核心安全功能

- **OAuth2 完整支持** - 实现授权服务器、资源服务器和客户端
- **多样化认证方式** - 支持用户名密码、手机号、邮箱等多种认证方式
- **会话管理** - 灵活的会话管理策略，支持单点登录和会话共享
- **权限控制** - 基于角色和资源的细粒度权限控制

### 🌐 第三方集成

- **微信登录** - 支持微信公众号、小程序、开放平台登录
- **飞书登录** - 支持飞书企业应用登录集成
- **扩展支持** - 易于扩展其他第三方平台登录

### 🛡️ 安全防护

- **验证码保护** - 内置图形验证码、短信验证码功能
- **防暴力破解** - 登录失败锁定、频率限制
- **安全审计** - 完整的安全日志和审计功能
- **CSRF 防护** - 跨站请求伪造防护

### 🏗️ 架构设计

- **模块化设计** - 高度模块化，按需集成
- **RESTful API** - 完整的 REST API 支持
- **云原生友好** - 支持微服务架构和容器化部署
- **高度可配置** - 丰富的配置选项，满足不同场景需求

## 🏗️ 项目架构

```
athena-security-parent/
├── athena-security-bom/                           # 依赖管理BOM
│   └── pom.xml                                   # 统一依赖版本管理
├── athena-security-project/                       # 核心项目模块
│   ├── athena-security-common/                   # 🔧 公共模块
│   │   ├── 安全工具类
│   │   ├── 通用配置
│   │   └── 基础组件
│   ├── athena-security-captcha/                  # 🎯 验证码模块
│   │   ├── 图形验证码
│   │   ├── 短信验证码
│   │   └── 验证码管理
│   ├── athena-security-rest/                     # 🌐 REST API模块
│   │   ├── 认证接口
│   │   ├── 用户管理接口
│   │   └── 权限管理接口
│   ├── athena-security-web/                      # 🖥️ Web安全模块
│   │   ├── Web安全配置
│   │   ├── 过滤器链
│   │   └── 会话管理
│   ├── athena-security-oauth2-authorization-server/ # 🔐 OAuth2授权服务器
│   │   ├── 授权端点
│   │   ├── Token管理
│   │   └── 客户端管理
│   ├── athena-security-oauth2-resource-server/   # 🛡️ OAuth2资源服务器
│   │   ├── 资源保护
│   │   ├── Token验证
│   │   └── 权限检查
│   ├── athena-security-oauth2-client/            # 📱 OAuth2客户端
│   │   ├── 客户端配置
│   │   ├── 授权流程
│   │   └── Token管理
│   ├── athena-security-oauth2-client-wechat/     # 🟢 微信登录客户端
│   │   ├── 微信OAuth2集成
│   │   ├── 用户信息获取
│   │   └── 登录流程实现
│   └── athena-security-oauth2-client-feishu/     # 🟦 飞书登录客户端
│       ├── 飞书OAuth2集成
│       ├── 企业用户信息
│       └── 登录流程实现
├── CODE_OF_CONDUCT.md                            # 行为准则
├── CONTRIBUTING.md                               # 贡献指南
├── LICENSE                                       # Apache 2.0 许可证
└── SECURITY.md                                   # 安全政策
```

## 🚀 快速开始

### 环境要求

- ☕ **Java 17+**
- 🌱 **Spring Boot 3.x**
- 🔒 **Spring Security 6.x**
- 📦 **Maven 3.6+**

### Maven 依赖

在你的项目 `pom.xml` 中添加 BOM 依赖管理：

```xml

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.github.gls-athena.security</groupId>
            <artifactId>athena-security-bom</artifactId>
            <version>0.0.7</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 基础使用

#### 1. 添加核心依赖

```xml

<dependencies>
    <!-- 基础安全模块 -->
    <dependency>
        <groupId>io.github.gls-athena.security.common</groupId>
        <artifactId>athena-security-common</artifactId>
    </dependency>

    <!-- Web安全模块 -->
    <dependency>
        <groupId>io.github.gls-athena.security.web</groupId>
        <artifactId>athena-security-web</artifactId>
    </dependency>

    <!-- 验证码模块（可选） -->
    <dependency>
        <groupId>io.github.gls-athena.security.captcha</groupId>
        <artifactId>athena-security-captcha</artifactId>
    </dependency>
</dependencies>
```

#### 2. OAuth2 授权服务器

```xml

<dependency>
    <groupId>io.github.gls-athena.security.oauth2</groupId>
    <artifactId>athena-security-oauth2-authorization-server</artifactId>
</dependency>
```

#### 3. 第三方登录集成

```xml
<!-- 微信登录 -->
<dependency>
    <groupId>io.github.gls-athena.security.oauth2.client</groupId>
    <artifactId>athena-security-oauth2-client-wechat</artifactId>
</dependency>

        <!-- 飞书登录 -->
<dependency>
<groupId>io.github.gls-athena.security.oauth2.client</groupId>
<artifactId>athena-security-oauth2-client-feishu</artifactId>
</dependency>
```

### 配置示例

```yaml
# application.yml
athena:
  security:
    # 基础安全配置
    web:
      enabled: true
      login-url: /login
      logout-url: /logout

    # OAuth2 配置
    oauth2:
      authorization-server:
        enabled: true
        issuer: http://localhost:8080

      # 第三方登录配置
      client:
        wechat:
          enabled: true
          client-id: ${WECHAT_CLIENT_ID}
          client-secret: ${WECHAT_CLIENT_SECRET}
        feishu:
          enabled: true
          client-id: ${FEISHU_CLIENT_ID}
          client-secret: ${FEISHU_CLIENT_SECRET}

    # 验证码配置
    captcha:
      enabled: true
      type: image # image, sms
      expire-time: 300
```

## 📚 使用指南

### OAuth2 授权服务器配置

```java

@Configuration
@EnableAthenaOAuth2AuthorizationServer
public class OAuth2AuthorizationServerConfig {

    @Bean
    public OAuth2ClientDetailsService clientDetailsService() {
        return new InMemoryOAuth2ClientDetailsService();
    }
}
```

### 第三方登录配置

```java

@Configuration
@EnableAthenaOAuth2Client
public class OAuth2ClientConfig {

    @Bean
    public WechatOAuth2ClientConfig wechatConfig() {
        return WechatOAuth2ClientConfig.builder()
                .clientId("your-wechat-client-id")
                .clientSecret("your-wechat-client-secret")
                .redirectUri("http://localhost:8080/oauth2/callback/wechat")
                .build();
    }
}
```

### 验证码集成

```java

@RestController
public class CaptchaController {

    @Autowired
    private CaptchaService captchaService;

    @GetMapping("/captcha/image")
    public CaptchaResponse generateImageCaptcha() {
        return captchaService.generateImageCaptcha();
    }

    @PostMapping("/captcha/verify")
    public boolean verifyCaptcha(@RequestParam String code,
                                 @RequestParam String token) {
        return captchaService.verify(token, code);
    }
}
```

## 🔧 高级配置

### 自定义安全配置

```java

@Configuration
public class CustomSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard")
                )
                .build();
    }
}
```

### 自定义认证提供者

```java

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        // 自定义认证逻辑
        return new UsernamePasswordAuthenticationToken(
                authentication.getPrincipal(),
                authentication.getCredentials(),
                Collections.emptyList()
        );
    }

    @Override
    public boolean supports(Class<?> authenticationType) {
        return UsernamePasswordAuthenticationToken.class
                .isAssignableFrom(authenticationType);
    }
}
```

## 📖 API 文档

### 认证接口

| 接口              | 方法   | 描述       |
|-----------------|------|----------|
| `/auth/login`   | POST | 用户登录     |
| `/auth/logout`  | POST | 用户登出     |
| `/auth/refresh` | POST | 刷新Token  |
| `/auth/info`    | GET  | 获取当前用户信息 |

### OAuth2 接口

| 接口                   | 方法   | 描述      |
|----------------------|------|---------|
| `/oauth2/authorize`  | GET  | 授权端点    |
| `/oauth2/token`      | POST | Token端点 |
| `/oauth2/revoke`     | POST | Token撤销 |
| `/oauth2/introspect` | POST | Token内省 |

### 第三方登录接口

| 接口                             | 方法  | 描述      |
|--------------------------------|-----|---------|
| `/oauth2/authorization/wechat` | GET | 微信登录跳转  |
| `/oauth2/authorization/feishu` | GET | 飞书登录跳转  |
| `/oauth2/callback/{provider}`  | GET | 第三方登录回调 |

## 🤝 贡献指南

我们欢迎所有形式的贡献！请查看 [CONTRIBUTING.md](CONTRIBUTING.md) 了解如何参与项目开发。

### 提交 Issues

- 🐛 **Bug 报告**: 使用 Bug 报告模板
- 💡 **功能建议**: 使用功能请求模板
- 📖 **文档改进**: 直接提交 PR

### 开发流程

1. Fork 项目
2. 创建功能分支: `git checkout -b feature/new-feature`
3. 提交更改: `git commit -am 'Add new feature'`
4. 推送分支: `git push origin feature/new-feature`
5. 提交 Pull Request

## 📄 许可证

本项目采用 [Apache 2.0](LICENSE) 许可证。

## 🔒 安全政策

如果您发现安全漏洞，请查看我们的 [安全政策](SECURITY.md) 了解如何负责任地报告。

## 📞 联系我们

- 📧 **邮箱**: support@athena-framework.com
- 🐛 **Issues**: [GitHub Issues](https://github.com/gls-athena/athena-security-parent/issues)
- 📚 **文档**: [在线文档](https://docs.athena-framework.com)

## 🙏 致谢

感谢所有为 Athena Security 项目做出贡献的开发者！

---

<div align="center">

**[⬆ 回到顶部](#athena-security-安全认证框架)**

Made with ❤️ by Athena Framework Team

</div>
