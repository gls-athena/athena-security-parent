package com.gls.athena.security.oauth2.client.wechat;

import com.gls.athena.security.oauth2.client.wechat.config.Oauth2WechatProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author george
 */
@Configuration
@ComponentScan
@EnableConfigurationProperties(Oauth2WechatProperties.class)
public class Oauth2WechatAutoConfig {

}