package com.gls.athena.security.web.customizer;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.stereotype.Component;

/**
 * 会话管理自定义器
 * 用于配置Spring Security的会话管理策略
 *
 * @author george
 */
@Component
public class SessionManagementCustomizer
        implements Customizer<SessionManagementConfigurer<HttpSecurity>> {
    /**
     * 自定义会话管理配置
     * 设置会话创建策略为无状态模式，不创建或使用HTTP会话
     *
     * @param configurer 会话管理配置器，用于配置会话相关策略
     */
    @Override
    public void customize(SessionManagementConfigurer<HttpSecurity> configurer) {
        // 配置会话创建策略为STATELESS，表示不创建会话，适用于RESTful API等无状态应用
        configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}

