package com.gls.athena.security.oauth2.authorization.server.customizer;

import org.springframework.security.config.Customizer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationEndpointConfigurer;
import org.springframework.stereotype.Component;

/**
 * 授权端点自定义器类
 * 用于自定义OAuth2授权服务器的授权端点配置
 * 实现Spring Security的Customizer接口，专门用于配置OAuth2AuthorizationEndpointConfigurer
 *
 * @author george
 */
@Component
public class AuthorizationEndpointCustomizer implements Customizer<OAuth2AuthorizationEndpointConfigurer> {

    /**
     * 自定义OAuth2授权端点配置
     *
     * @param configurer OAuth2授权端点配置器，用于配置授权端点的各种行为和属性
     */
    @Override
    public void customize(OAuth2AuthorizationEndpointConfigurer configurer) {

    }
}
