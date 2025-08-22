package com.gls.athena.security.web.customizer;

import com.gls.athena.security.common.config.SecurityCommonProperties;
import jakarta.annotation.Resource;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

/**
 * 授权请求自定义器
 * 用于配置HTTP请求的授权规则，指定哪些URL需要认证，哪些URL可以匿名访问
 *
 * @author george
 */
@Component
public class AuthorizeHttpRequestsCustomizer
        implements Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> {

    @Resource
    private SecurityCommonProperties securityCommonProperties;

    /**
     * 自定义授权请求配置
     * 配置URL访问权限规则，包括忽略认证的URL、静态资源、登录页面等
     *
     * @param registry 授权管理器请求匹配器注册器，用于配置请求匹配和授权规则
     */
    @Override
    public void customize(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {

        registry
                // 配置忽略认证的URL，这些URL可以匿名访问
                .requestMatchers(securityCommonProperties.getIgnoreUrls()).permitAll()
                // 配置静态资源URL，这些资源不需要认证即可访问
                .requestMatchers(securityCommonProperties.getStaticUrls()).permitAll()
                // 配置登录页面URL，登录相关页面和请求不需要认证
                .requestMatchers(securityCommonProperties.getLoginPage()).permitAll()
                // 配置所有其他请求都需要认证
                .anyRequest().authenticated();
    }
}

