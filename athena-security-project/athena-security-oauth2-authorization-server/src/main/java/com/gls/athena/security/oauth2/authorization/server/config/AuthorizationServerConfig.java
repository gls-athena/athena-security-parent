package com.gls.athena.security.oauth2.authorization.server.config;

import com.gls.athena.security.oauth2.authorization.server.customizer.AuthorizationServerCustomizer;
import com.gls.athena.security.web.customizer.AuthorizeHttpRequestsCustomizer;
import com.gls.athena.security.web.customizer.ExceptionHandlingCustomizer;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * OAuth2 授权服务器安全配置类
 * <p>
 * 该类用于配置 OAuth2 授权服务器相关的安全策略，包括请求匹配、授权规则和异常处理等。
 *
 * @author george
 */
@Configuration
public class AuthorizationServerConfig {

    @Resource
    private AuthorizationServerCustomizer authorizationServerCustomizer;

    @Resource
    private AuthorizeHttpRequestsCustomizer authorizeHttpRequestsCustomizer;

    @Resource
    private ExceptionHandlingCustomizer exceptionHandlingCustomizer;

    /**
     * 配置OAuth2授权服务器的安全过滤器链
     * <p>
     * 该方法构建一个专门用于处理 OAuth2 授权服务器相关端点的安全过滤器链，
     * 包括令牌颁发、授权确认等核心功能的安全控制。
     *
     * @param http HttpSecurity对象，用于配置web安全
     * @return SecurityFilterChain 安全过滤器链
     * @throws Exception 配置过程中可能抛出的异常
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        // 创建OAuth2授权服务器配置器
        OAuth2AuthorizationServerConfigurer authorizationServer = new OAuth2AuthorizationServerConfigurer();

        // 设置安全匹配器，只处理授权服务器端点的请求
        http.securityMatcher(authorizationServer.getEndpointsMatcher());

        // 应用授权服务器自定义配置
        http.with(authorizationServer, authorizationServerCustomizer);

        // 应用HTTP请求授权自定义配置
        http.authorizeHttpRequests(authorizeHttpRequestsCustomizer);

        // 应用异常处理自定义配置
        http.exceptionHandling(exceptionHandlingCustomizer);

        // 构建并返回安全过滤器链
        return http.build();
    }
}
