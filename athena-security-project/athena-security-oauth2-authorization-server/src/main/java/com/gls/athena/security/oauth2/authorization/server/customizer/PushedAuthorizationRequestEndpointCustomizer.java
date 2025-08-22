package com.gls.athena.security.oauth2.authorization.server.customizer;

import org.springframework.security.config.Customizer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2PushedAuthorizationRequestEndpointConfigurer;
import org.springframework.stereotype.Component;

/**
 * Pushed Authorization Request端点自定义器
 * 用于自定义OAuth2 Pushed Authorization Request端点的配置
 * 实现Spring Security的Customizer接口，专门用于配置OAuth2PushedAuthorizationRequestEndpointConfigurer
 *
 * @author george
 */
@Component
public class PushedAuthorizationRequestEndpointCustomizer implements Customizer<OAuth2PushedAuthorizationRequestEndpointConfigurer> {

    /**
     * 自定义OAuth2 Pushed Authorization Request端点配置
     *
     * @param configurer OAuth2 Pushed Authorization Request端点配置器，用于配置相关的安全设置和端点行为
     */
    @Override
    public void customize(OAuth2PushedAuthorizationRequestEndpointConfigurer configurer) {
    }
}
