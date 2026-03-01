package com.gls.athena.security.web.config;

import com.gls.athena.security.oauth2.client.customizer.Oauth2LoginCustomizer;
import com.gls.athena.security.rest.configurer.RestLoginConfigurer;
import com.gls.athena.security.web.customizer.AuthorizeHttpRequestsCustomizer;
import com.gls.athena.security.web.customizer.CsrfCustomizer;
import com.gls.athena.security.web.customizer.ExceptionHandlingCustomizer;
import com.gls.athena.security.web.customizer.Oauth2ResourceServerCustomizer;
import com.gls.athena.security.web.customizer.RestLoginCustomizer;
import com.gls.athena.security.web.customizer.SessionManagementCustomizer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 安全Web配置类
 * 用于配置Spring Security的Web安全策略，包括授权、CSRF保护、会话管理、
 * 异常处理、REST登录、OAuth2登录以及资源服务器（opaque token）等功能
 *
 * @author george
 */
@Configuration
@RequiredArgsConstructor
public class SecurityWebConfig {

    private final AuthorizeHttpRequestsCustomizer authorizeHttpRequestsCustomizer;
    private final CsrfCustomizer csrfCustomizer;
    private final SessionManagementCustomizer sessionManagementCustomizer;
    private final RestLoginCustomizer restLoginCustomizer;
    private final Oauth2LoginCustomizer oauth2LoginCustomizer;
    private final ExceptionHandlingCustomizer exceptionHandlingCustomizer;
    private final Oauth2ResourceServerCustomizer oauth2ResourceServerCustomizer;

    /**
     * 配置安全过滤器链
     * 该方法定义了Web应用的安全策略，包括请求授权、CSRF保护、会话管理、
     * 异常处理、REST登录、OAuth2登录和资源服务器配置
     *
     * @param http HttpSecurity对象，用于配置Web安全
     * @return SecurityFilterChain 安全过滤器链
     * @throws Exception 配置过程中可能抛出的异常
     */
    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        // 配置授权请求
        http.authorizeHttpRequests(authorizeHttpRequestsCustomizer);
        // 配置CSRF（无状态 REST 场景下禁用）
        http.csrf(csrfCustomizer);
        // 配置会话管理（无状态策略，不创建 HttpSession）
        http.sessionManagement(sessionManagementCustomizer);
        // 配置REST登录
        http.with(RestLoginConfigurer.restLogin(), restLoginCustomizer);
        // 配置OAuth2登录
        http.oauth2Login(oauth2LoginCustomizer);
        // 配置OAuth2资源服务器（opaque token 验证）
        http.oauth2ResourceServer(oauth2ResourceServerCustomizer);
        // 配置异常处理：HTML请求重定向到登录页，API请求返回 401
        http.exceptionHandling(exceptionHandlingCustomizer);
        return http.build();
    }

}

