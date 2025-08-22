package com.gls.athena.security.oauth2.authorization.server.customizer;

import org.springframework.security.config.Customizer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2TokenRevocationEndpointConfigurer;
import org.springframework.stereotype.Component;

/**
 * Token撤销端点自定义器
 * 用于自定义OAuth2令牌撤销端点的配置
 *
 * @author george
 */
@Component
public class TokenRevocationEndpointCustomizer implements Customizer<OAuth2TokenRevocationEndpointConfigurer> {

    /**
     * 自定义OAuth2令牌撤销端点配置
     *
     * @param configurer OAuth2令牌撤销端点配置器，用于配置令牌撤销相关的安全设置
     */
    @Override
    public void customize(OAuth2TokenRevocationEndpointConfigurer configurer) {
    }
}

