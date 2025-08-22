package com.gls.athena.security.oauth2.authorization.server.customizer;

import org.springframework.security.config.Customizer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerMetadataEndpointConfigurer;
import org.springframework.stereotype.Component;

/**
 * 授权服务器元数据端点自定义器
 * 用于自定义OAuth2授权服务器的元数据端点配置
 * 实现Spring Security的Customizer接口，专门用于配置OAuth2AuthorizationServerMetadataEndpointConfigurer
 *
 * @author george
 */
@Component
public class AuthorizationServerMetadataEndpointCustomizer implements Customizer<OAuth2AuthorizationServerMetadataEndpointConfigurer> {

    /**
     * 自定义OAuth2授权服务器元数据端点配置
     *
     * @param configurer OAuth2授权服务器元数据端点配置器，用于配置相关的端点行为和属性
     */
    @Override
    public void customize(OAuth2AuthorizationServerMetadataEndpointConfigurer configurer) {
    }
}
