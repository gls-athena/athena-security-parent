package com.gls.athena.security.oauth2.authorization.server.customizer;

import jakarta.annotation.Resource;
import org.springframework.security.config.Customizer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.stereotype.Component;

/**
 * OAuth2授权服务器配置自定义器
 * 用于自定义OAuth2授权服务器的配置行为
 *
 * @author george
 */
@Component
public class AuthorizationServerCustomizer implements Customizer<OAuth2AuthorizationServerConfigurer> {

    @Resource
    private AuthorizationEndpointCustomizer authorizationEndpointCustomizer;
    @Resource
    private AuthorizationServerMetadataEndpointCustomizer authorizationServerMetadataEndpointCustomizer;
    @Resource
    private ClientAuthenticationCustomizer clientAuthenticationCustomizer;
    @Resource
    private DeviceAuthorizationEndpointCustomizer deviceAuthorizationEndpointCustomizer;
    @Resource
    private DeviceVerificationEndpointCustomizer deviceVerificationEndpointCustomizer;
    @Resource
    private OidcCustomizer oidcCustomizer;
    @Resource
    private PushedAuthorizationRequestEndpointCustomizer pushedAuthorizationRequestEndpointCustomizer;
    @Resource
    private TokenEndpointCustomizer tokenEndpointCustomizer;
    @Resource
    private TokenIntrospectionEndpointCustomizer tokenIntrospectionEndpointCustomizer;
    @Resource
    private TokenRevocationEndpointCustomizer tokenRevocationEndpointCustomizer;

    /**
     * 自定义OAuth2授权服务器配置
     * 通过该方法可以对OAuth2授权服务器的各种配置进行自定义设置
     *
     * @param configurer OAuth2授权服务器配置器，用于配置授权服务器的各项参数和行为
     */
    @Override
    public void customize(OAuth2AuthorizationServerConfigurer configurer) {
        // 定制授权端点
        configurer.authorizationEndpoint(authorizationEndpointCustomizer);
        // 定制授权服务器元数据端点
        configurer.authorizationServerMetadataEndpoint(authorizationServerMetadataEndpointCustomizer);
        // 定制客户端认证
        configurer.clientAuthentication(clientAuthenticationCustomizer);
        // 定制设备授权端点
        configurer.deviceAuthorizationEndpoint(deviceAuthorizationEndpointCustomizer);
        // 定制设备验证端点
        configurer.deviceVerificationEndpoint(deviceVerificationEndpointCustomizer);
        // 定制OIDC相关配置
        configurer.oidc(oidcCustomizer);
        // 定制推送授权请求端点
        configurer.pushedAuthorizationRequestEndpoint(pushedAuthorizationRequestEndpointCustomizer);
        // 定制令牌端点
        configurer.tokenEndpoint(tokenEndpointCustomizer);
        // 定制令牌验证端点
        configurer.tokenIntrospectionEndpoint(tokenIntrospectionEndpointCustomizer);
        // 定制令牌撤销端点
        configurer.tokenRevocationEndpoint(tokenRevocationEndpointCustomizer);
    }
}

