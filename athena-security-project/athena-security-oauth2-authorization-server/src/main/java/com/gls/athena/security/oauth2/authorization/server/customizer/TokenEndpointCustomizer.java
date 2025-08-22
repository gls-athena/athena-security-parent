package com.gls.athena.security.oauth2.authorization.server.customizer;

import com.gls.athena.security.oauth2.authorization.server.authentication.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.Customizer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2TokenEndpointConfigurer;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.stereotype.Component;

/**
 * OAuth2令牌端点自定义配置器
 * <p>
 * 用于自定义OAuth2令牌端点的相关配置，支持多种认证方式：
 * <ul>
 *   <li>密码认证 - 传统的用户名/密码认证方式</li>
 *   <li>手机认证 - 通过手机号和验证码进行认证</li>
 *   <li>邮箱认证 - 通过邮箱和验证码进行认证</li>
 * </ul>
 * 每种认证方式都有对应的认证转换器和认证提供者，转换器负责将HTTP请求转换为认证对象，
 * 提供者负责处理具体的认证逻辑。
 *
 * @author george
 */
@Slf4j
@Component
public class TokenEndpointCustomizer implements Customizer<OAuth2TokenEndpointConfigurer> {
    @Resource
    private OAuth2AuthorizationService authorizationService;
    @Resource
    private OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;
    @Resource
    private SessionRegistry sessionRegistry;
    @Resource
    private UserDetailsService userDetailsService;
    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 自定义OAuth2令牌端点配置
     * <p>
     * 该方法会为令牌端点添加三种不同的认证方式支持：
     * <ol>
     *   <li>密码认证：使用用户名和密码进行认证</li>
     *   <li>手机认证：使用手机号和验证码进行认证</li>
     *   <li>邮箱认证：使用邮箱和验证码进行认证</li>
     * </ol>
     * 每种认证方式都注册了对应的认证转换器（converter）和认证提供者（provider）。
     *
     * @param configurer OAuth2令牌端点配置器，用于配置令牌端点的相关参数和行为
     */
    @Override
    public void customize(OAuth2TokenEndpointConfigurer configurer) {
        // 添加密码认证转换器和提供者
        configurer.accessTokenRequestConverter(new PasswordAuthenticationConverter())
                .authenticationProvider(new PasswordAuthenticationProvider(
                        authorizationService, tokenGenerator, sessionRegistry, userDetailsService, passwordEncoder));

        // 添加手机认证转换器和提供者
        configurer.accessTokenRequestConverter(new MobileAuthenticationConverter())
                .authenticationProvider(new MobileAuthenticationProvider(
                        authorizationService, tokenGenerator, sessionRegistry, userDetailsService));

        // 添加邮箱认证转换器和提供者
        configurer.accessTokenRequestConverter(new EmailAuthenticationConverter())
                .authenticationProvider(new EmailAuthenticationProvider(
                        authorizationService, tokenGenerator, sessionRegistry, userDetailsService));
    }

}
