package com.gls.athena.security.web.customizer;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.stereotype.Component;

/**
 * 资源服务器自定义配置类
 * 用于配置OAuth2资源服务器的相关参数
 *
 * @author george
 */
@Component
public class Oauth2ResourceServerCustomizer
        implements Customizer<OAuth2ResourceServerConfigurer<HttpSecurity>> {

    /**
     * 自定义OAuth2资源服务器配置
     * 配置不透明令牌的默认设置
     *
     * @param configurer OAuth2资源服务器配置器，用于配置资源服务器相关参数
     */
    @Override
    public void customize(OAuth2ResourceServerConfigurer<HttpSecurity> configurer) {
        // 默认配置
        configurer.opaqueToken(Customizer.withDefaults());
    }

}
