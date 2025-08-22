package com.gls.athena.security.oauth2.authorization.server.customizer;

import jakarta.annotation.Resource;
import org.springframework.security.config.Customizer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OidcConfigurer;
import org.springframework.stereotype.Component;

/**
 * OIDC配置自定义器
 * 用于自定义OIDC(OpenID Connect)相关的配置
 *
 * @author george
 */
@Component
public class OidcCustomizer implements Customizer<OidcConfigurer> {
    @Resource
    private OidcClientRegistrationEndpointCustomizer oidcClientRegistrationEndpointCustomizer;
    @Resource
    private OidcLogoutEndpointCustomizer oidcLogoutEndpointCustomizer;
    @Resource
    private OidcProviderConfigurationEndpointCustomizer oidcProviderConfigurationEndpointCustomizer;
    @Resource
    private OidcUserInfoEndpointCustomizer oidcUserInfoEndpointCustomizer;

    /**
     * 自定义OIDC配置
     * 该方法用于对OIDC配置进行自定义设置，包括客户端注册端点、注销端点、
     * 提供者配置端点和用户信息端点的自定义配置
     *
     * @param configurer OIDC配置器，用于配置OIDC相关参数
     */
    @Override
    public void customize(OidcConfigurer configurer) {
        // 设置OIDC客户端注册端点的自定义器
        configurer.clientRegistrationEndpoint(oidcClientRegistrationEndpointCustomizer);
        // 设置OIDC注销端点的自定义器
        configurer.logoutEndpoint(oidcLogoutEndpointCustomizer);
        // 配置OIDC提供者配置端点的自定义器
        configurer.providerConfigurationEndpoint(oidcProviderConfigurationEndpointCustomizer);
        // 设置OIDC用户信息端点的自定义器
        configurer.userInfoEndpoint(oidcUserInfoEndpointCustomizer);
    }
}

