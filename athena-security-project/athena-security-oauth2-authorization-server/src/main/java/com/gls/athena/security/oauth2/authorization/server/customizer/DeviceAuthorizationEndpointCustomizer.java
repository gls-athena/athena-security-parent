package com.gls.athena.security.oauth2.authorization.server.customizer;

import org.springframework.security.config.Customizer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2DeviceAuthorizationEndpointConfigurer;
import org.springframework.stereotype.Component;

/**
 * 设备授权端点自定义器
 * 用于自定义OAuth2设备授权端点的配置
 * 实现Spring Security的Customizer接口，专门用于配置OAuth2DeviceAuthorizationEndpointConfigurer
 *
 * @author george
 */
@Component
public class DeviceAuthorizationEndpointCustomizer implements Customizer<OAuth2DeviceAuthorizationEndpointConfigurer> {

    /**
     * 自定义OAuth2设备授权端点配置
     *
     * @param configurer OAuth2设备授权端点配置器，用于配置设备授权端点的相关参数和行为
     */
    @Override
    public void customize(OAuth2DeviceAuthorizationEndpointConfigurer configurer) {
    }
}
