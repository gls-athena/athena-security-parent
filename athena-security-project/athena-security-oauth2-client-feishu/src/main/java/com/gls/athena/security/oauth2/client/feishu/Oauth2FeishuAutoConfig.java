package com.gls.athena.security.oauth2.client.feishu;

import com.gls.athena.security.oauth2.client.feishu.config.Oauth2FeishuProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 飞书OAuth2客户端自动配置类
 * <p>
 * 该配置类用于启用飞书OAuth2客户端的相关配置，包括自动扫描组件和启用配置属性绑定
 * </p>
 *
 * @author george
 */
@Configuration
@ComponentScan
@EnableConfigurationProperties(Oauth2FeishuProperties.class)
public class Oauth2FeishuAutoConfig {
}

