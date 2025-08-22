package com.gls.athena.security.oauth2.authorization.server.customizer;

import org.springframework.security.config.Customizer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2ClientAuthenticationConfigurer;
import org.springframework.stereotype.Component;

/**
 * 客户端认证自定义器
 * 用于自定义OAuth2客户端认证的配置
 *
 * @author george
 */
@Component
public class ClientAuthenticationCustomizer implements Customizer<OAuth2ClientAuthenticationConfigurer> {
    /**
     * 自定义OAuth2客户端认证配置
     *
     * @param configurer OAuth2客户端认证配置器，用于配置客户端认证相关的过滤器和验证逻辑
     */
    @Override
    public void customize(OAuth2ClientAuthenticationConfigurer configurer) {
    }
}

