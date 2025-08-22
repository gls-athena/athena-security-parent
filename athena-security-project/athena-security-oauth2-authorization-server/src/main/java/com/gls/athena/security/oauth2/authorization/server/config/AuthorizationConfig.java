package com.gls.athena.security.oauth2.authorization.server.config;

import com.gls.athena.security.oauth2.authorization.server.redis.converter.Oauth2AuthorizationConsentConverter;
import com.gls.athena.security.oauth2.authorization.server.redis.converter.Oauth2AuthorizationConverter;
import com.gls.athena.security.oauth2.authorization.server.redis.service.RedisOauth2AuthorizationConsentServiceImpl;
import com.gls.athena.security.oauth2.authorization.server.redis.service.RedisOauth2AuthorizationServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.util.UUID;

/**
 * OAuth2 授权服务器配置类
 * <p>
 * 用于配置授权服务、会话注册表以及客户端信息存储等核心组件。
 *
 * @author george
 */
@Configuration
public class AuthorizationConfig {

    /**
     * 提供 OAuth2 授权服务的 Bean 实例
     * <p>
     * 当容器中不存在 OAuth2AuthorizationService 类型的 Bean 时，创建一个基于 Redis 的实现。
     *
     * @param converter                  OAuth2 授权对象转换器，用于序列化和反序列化授权信息
     * @param registeredClientRepository 已注册客户端信息仓库，用于获取客户端详情
     * @return OAuth2AuthorizationService 授权服务实例
     */
    @Bean
    @ConditionalOnMissingBean(OAuth2AuthorizationService.class)
    public OAuth2AuthorizationService authorizationService(Oauth2AuthorizationConverter converter,
                                                           RegisteredClientRepository registeredClientRepository) {
        return new RedisOauth2AuthorizationServiceImpl(converter, registeredClientRepository);
    }

    /**
     * 提供 OAuth2 授权同意服务的 Bean 实例
     * <p>
     * 当容器中不存在 OAuth2AuthorizationConsentService 类型的 Bean 时，创建一个基于 Redis 的实现。
     *
     * @param converter 授权同意信息转换器，用于序列化和反序列化授权同意信息
     * @return OAuth2AuthorizationConsentService 授权同意服务实例
     */
    @Bean
    @ConditionalOnMissingBean(OAuth2AuthorizationConsentService.class)
    public OAuth2AuthorizationConsentService authorizationConsentService(Oauth2AuthorizationConsentConverter converter) {
        return new RedisOauth2AuthorizationConsentServiceImpl(converter);
    }

    /**
     * 提供会话注册表的 Bean 实例
     * <p>
     * 当容器中不存在 SessionRegistry 类型的 Bean 时，创建一个基于内存的实现。
     *
     * @return SessionRegistry 会话注册表实例
     */
    @Bean
    @ConditionalOnMissingBean(SessionRegistry.class)
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    /**
     * 注册客户端信息仓库
     * <p>
     * 配置一个默认的客户端（public-client），支持多种授权方式，包括授权码模式、刷新令牌、客户端凭证、密码模式和手机号码模式。
     * 同时设置重定向 URI、登出后重定向 URI、作用域及客户端和令牌相关设置。
     * 使用内存存储方式管理注册客户端信息。
     *
     * @return RegisteredClientRepository 客户端信息仓库实例
     */
    @Bean
    @ConditionalOnMissingBean
    public RegisteredClientRepository registeredClientRepository() {
        // 构建一个注册客户端对象，包含认证方式、授权类型、回调地址、作用域等配置
        RegisteredClient messagingClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("public-client")
                .clientSecret("{noop}secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .authorizationGrantType(AuthorizationServerConstants.PASSWORD)
                .authorizationGrantType(AuthorizationServerConstants.MOBILE)
                .redirectUri("http://localhost:8080/login/oauth2/code/public-client-oidc")
                .redirectUri("http://localhost:8080/authorized")
                .redirectUri("https://www.baidu.com")
                .postLogoutRedirectUri("http://localhost:8080/logged-out")
                .scope("openid")
                .scope("profile")
                .scope("message.read")
                .scope("message.write")
                .scope("user.read")
                .clientSettings(ClientSettings.builder().build())
                .tokenSettings(TokenSettings.builder().build())
                .build();

        // 返回基于内存的客户端信息仓库
        return new InMemoryRegisteredClientRepository(messagingClient);
    }
}
