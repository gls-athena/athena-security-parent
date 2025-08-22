package com.gls.athena.security.oauth2.client.customizer;

import com.gls.athena.security.oauth2.client.delegate.DelegateOauth2AccessTokenResponseClient;
import com.gls.athena.security.oauth2.client.delegate.DelegateOauth2AuthorizationRequestResolver;
import jakarta.annotation.Resource;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2ClientConfigurer;
import org.springframework.stereotype.Component;

/**
 * OAuth2客户端配置自定义器
 * 用于自定义OAuth2客户端的授权码模式配置
 *
 * @author george
 */
@Component
public class Oauth2ClientCustomizer implements Customizer<OAuth2ClientConfigurer<HttpSecurity>> {
    /**
     * 注入授权请求解析器
     */
    @Resource
    private DelegateOauth2AuthorizationRequestResolver oauth2AuthorizationRequestResolver;
    /**
     * 注入访问令牌响应客户端
     * 用于处理OAuth2授权码授予请求的访问令牌响应
     */
    @Resource
    private DelegateOauth2AccessTokenResponseClient oauth2AccessTokenResponseClient;

    /**
     * 自定义OAuth2客户端配置
     *
     * @param configurer OAuth2客户端配置器，用于配置OAuth2客户端相关功能
     */
    @Override
    public void customize(OAuth2ClientConfigurer<HttpSecurity> configurer) {
        // 配置授权码模式的自定义设置
        configurer.authorizationCodeGrant(this::customizeAuthorizationCodeGrant);
    }

    /**
     * 自定义授权码模式配置
     *
     * @param configurer 授权码模式配置器，用于配置授权码模式相关的组件
     */
    private void customizeAuthorizationCodeGrant(OAuth2ClientConfigurer<HttpSecurity>.AuthorizationCodeGrantConfigurer configurer) {
        // 设置自定义的授权请求解析器
        configurer.authorizationRequestResolver(oauth2AuthorizationRequestResolver);
        // 设置自定义的访问令牌响应客户端
        configurer.accessTokenResponseClient(oauth2AccessTokenResponseClient);
    }
}

