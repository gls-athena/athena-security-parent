package com.gls.athena.security.oauth2.authorization.server.customizer;

import org.springframework.security.config.Customizer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2TokenIntrospectionEndpointConfigurer;
import org.springframework.stereotype.Component;

/**
 * Token introspection端点自定义配置器
 * 用于自定义OAuth2令牌内省端点的配置
 *
 * @author george
 */
@Component
public class TokenIntrospectionEndpointCustomizer implements Customizer<OAuth2TokenIntrospectionEndpointConfigurer> {

    /**
     * 自定义OAuth2令牌内省端点配置
     *
     * @param configurer OAuth2令牌内省端点配置器，用于配置令牌内省相关的安全设置
     */
    @Override
    public void customize(OAuth2TokenIntrospectionEndpointConfigurer configurer) {
    }
}
