package com.gls.athena.security.oauth2.authorization.server.customizer;

import org.springframework.security.config.Customizer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2DeviceVerificationEndpointConfigurer;
import org.springframework.stereotype.Component;

/**
 * 设备验证端点自定义器类
 * 用于自定义OAuth2设备验证端点的配置
 *
 * @author george
 */
@Component
public class DeviceVerificationEndpointCustomizer implements Customizer<OAuth2DeviceVerificationEndpointConfigurer> {

    /**
     * 自定义设备验证端点配置
     *
     * @param configurer OAuth2设备验证端点配置器，用于配置设备验证端点的相关参数和行为
     */
    @Override
    public void customize(OAuth2DeviceVerificationEndpointConfigurer configurer) {
    }
}
