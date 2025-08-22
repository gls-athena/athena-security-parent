package com.gls.athena.security.web.customizer;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.stereotype.Component;

/**
 * CSRF自定义器
 * 用于配置和自定义CSRF（跨站请求伪造）保护功能
 *
 * @author george
 */
@Component
public class CsrfCustomizer
        implements Customizer<CsrfConfigurer<HttpSecurity>> {
    /**
     * 自定义CSRF配置
     * 该方法用于对CSRF配置进行自定义，当前实现为禁用CSRF保护
     *
     * @param configurer CSRF配置器，用于配置CSRF相关的安全设置
     */
    @Override
    public void customize(CsrfConfigurer<HttpSecurity> configurer) {
        // 禁用CSRF保护功能
        configurer.disable();
    }
}

