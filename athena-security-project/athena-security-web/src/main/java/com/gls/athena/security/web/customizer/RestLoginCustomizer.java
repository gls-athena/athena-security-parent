package com.gls.athena.security.web.customizer;

import com.gls.athena.security.common.config.SecurityCommonProperties;
import com.gls.athena.security.rest.configurer.RestLoginConfigurer;
import jakarta.annotation.Resource;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

/**
 * REST登录配置定制器组件
 * 用于自定义REST登录相关的安全配置
 *
 * @author george
 */
@Component
public class RestLoginCustomizer implements Customizer<RestLoginConfigurer<HttpSecurity>> {
    @Resource
    private SecurityCommonProperties securityCommonProperties;

    /**
     * 自定义REST登录配置
     * 通过设置登录页面来自定义REST登录行为
     *
     * @param configurer REST登录配置器，用于配置HTTP安全相关的登录功能
     */
    @Override
    public void customize(RestLoginConfigurer<HttpSecurity> configurer) {
        // 设置登录页面路径
        configurer.loginPage(securityCommonProperties.getLoginPage());
    }
}

