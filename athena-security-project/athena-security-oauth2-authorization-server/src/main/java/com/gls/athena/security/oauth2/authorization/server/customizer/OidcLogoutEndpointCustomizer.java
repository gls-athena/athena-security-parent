package com.gls.athena.security.oauth2.authorization.server.customizer;

import org.springframework.security.config.Customizer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OidcLogoutEndpointConfigurer;
import org.springframework.stereotype.Component;

/**
 * OIDC登出端点自定义器
 * 用于自定义OIDC登出端点的配置
 *
 * @author george
 */
@Component
public class OidcLogoutEndpointCustomizer implements Customizer<OidcLogoutEndpointConfigurer> {

    /**
     * 自定义OIDC登出端点配置
     *
     * @param configurer OIDC登出端点配置器，用于配置登出相关的安全设置
     */
    @Override
    public void customize(OidcLogoutEndpointConfigurer configurer) {
    }
}
