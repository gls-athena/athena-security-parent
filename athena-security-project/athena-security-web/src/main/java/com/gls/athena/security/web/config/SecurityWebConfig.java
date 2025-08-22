package com.gls.athena.security.web.config;

import com.gls.athena.security.oauth2.client.customizer.Oauth2LoginCustomizer;
import com.gls.athena.security.rest.configurer.RestLoginConfigurer;
import com.gls.athena.security.web.customizer.AuthorizeHttpRequestsCustomizer;
import com.gls.athena.security.web.customizer.CsrfCustomizer;
import com.gls.athena.security.web.customizer.RestLoginCustomizer;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 安全Web配置类
 * 用于配置Spring Security的Web安全策略，包括授权、CSRF保护、会话管理、异常处理和REST登录等功能
 *
 * @author george
 */
@Configuration
public class SecurityWebConfig {
    @Resource
    private AuthorizeHttpRequestsCustomizer authorizeHttpRequestsCustomizer;
    @Resource
    private CsrfCustomizer csrfCustomizer;
    @Resource
    private RestLoginCustomizer restLoginCustomizer;
    @Resource
    private Oauth2LoginCustomizer oauth2LoginCustomizer;

    /**
     * 配置安全过滤器链
     * 该方法定义了Web应用的安全策略，包括请求授权、CSRF保护、会话管理、异常处理和REST登录配置
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
        // 配置CSRF
        http.csrf(csrfCustomizer);
        // 配置REST登录
        http.with(RestLoginConfigurer.restLogin(), restLoginCustomizer);
        // 配置OAuth2登录
        http.oauth2Login(oauth2LoginCustomizer);
        // 构建安全过滤器链
        return http.build();
    }

}
