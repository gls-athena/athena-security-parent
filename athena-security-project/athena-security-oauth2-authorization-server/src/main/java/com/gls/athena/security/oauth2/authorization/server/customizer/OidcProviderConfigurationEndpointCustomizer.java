package com.gls.athena.security.oauth2.authorization.server.customizer;

import org.springframework.security.config.Customizer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OidcProviderConfigurationEndpointConfigurer;
import org.springframework.stereotype.Component;

/**
 * OIDC提供者配置端点自定义器
 * <p>
 * 用于自定义OIDC提供者配置端点的配置，实现Spring Security的Customizer接口，
 * 允许对OidcProviderConfigurationEndpointConfigurer进行定制化配置。
 * </p>
 *
 * @author george
 */
@Component
public class OidcProviderConfigurationEndpointCustomizer implements Customizer<OidcProviderConfigurationEndpointConfigurer> {

    /**
     * 自定义OIDC提供者配置端点配置
     * <p>
     * 该方法用于对OIDC提供者配置端点进行自定义配置，通过传入的configurer参数
     * 可以修改端点的相关配置选项。
     * </p>
     *
     * @param configurer OIDC提供者配置端点配置器，用于配置相关的端点行为
     */
    @Override
    public void customize(OidcProviderConfigurationEndpointConfigurer configurer) {
    }
}
