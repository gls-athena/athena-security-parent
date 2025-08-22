package com.gls.athena.security.web.customizer;

import com.gls.athena.security.common.config.SecurityCommonProperties;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 异常处理自定义器
 * 用于配置Spring Security的异常处理机制，主要处理未认证请求的跳转逻辑
 *
 * @author george
 */
@Component
public class ExceptionHandlingCustomizer
        implements Customizer<ExceptionHandlingConfigurer<HttpSecurity>> {

    /**
     * rest 安全属性配置
     */
    @Resource
    private SecurityCommonProperties securityCommonProperties;

    /**
     * 自定义异常处理配置
     * 配置未认证请求的处理方式，对于HTML请求跳转到登录页面
     *
     * @param configurer 异常处理配置器，用于配置认证入口点等异常处理逻辑
     */
    @Override
    public void customize(ExceptionHandlingConfigurer<HttpSecurity> configurer) {
        // 配置登录入口点
        AuthenticationEntryPoint authenticationEntryPoint = new LoginUrlAuthenticationEntryPoint(securityCommonProperties.getLoginPage());
        // 创建请求匹配器
        RequestMatcher requestMatcher = createRequestMatcher();
        // 配置异常处理 - 登录入口点
        configurer.defaultAuthenticationEntryPointFor(authenticationEntryPoint, requestMatcher);
    }

    /**
     * 创建请求匹配器 - 仅匹配HTML请求
     * 用于区分不同类型的请求，只对HTML类型的请求应用登录页面跳转逻辑
     *
     * @return 请求匹配器，用于匹配text/html类型的请求
     */
    private RequestMatcher createRequestMatcher() {
        MediaTypeRequestMatcher requestMatcher = new MediaTypeRequestMatcher(MediaType.TEXT_HTML);
        requestMatcher.setIgnoredMediaTypes(Set.of(MediaType.ALL));
        return requestMatcher;
    }
}

