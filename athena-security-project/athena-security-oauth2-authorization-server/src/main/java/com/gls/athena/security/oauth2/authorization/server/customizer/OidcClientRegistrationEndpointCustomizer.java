package com.gls.athena.security.oauth2.authorization.server.customizer;

import org.springframework.security.config.Customizer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OidcClientRegistrationEndpointConfigurer;
import org.springframework.stereotype.Component;

/**
 * OIDC客户端注册端点自定义器
 * 用于自定义OIDC客户端注册端点的配置
 *
 * @author george
 */
@Component
public class OidcClientRegistrationEndpointCustomizer implements Customizer<OidcClientRegistrationEndpointConfigurer> {

    /**
     * 自定义OIDC客户端注册端点配置
     *
     * @param configurer OIDC客户端注册端点配置器，用于配置客户端注册相关的安全设置
     */
    @Override
    public void customize(OidcClientRegistrationEndpointConfigurer configurer) {
    }
}
