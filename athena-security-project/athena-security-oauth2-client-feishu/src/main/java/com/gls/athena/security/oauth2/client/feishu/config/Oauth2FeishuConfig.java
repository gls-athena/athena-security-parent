package com.gls.athena.security.oauth2.client.feishu.config;

import com.gls.athena.security.oauth2.client.feishu.support.FeishuLoginProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OAuth2飞书客户端配置类
 * 用于配置飞书登录相关的Bean组件
 *
 * @author george
 */
@Configuration
public class Oauth2FeishuConfig {

    /**
     * 创建飞书登录提供者Bean
     * 当容器中不存在FeishuLoginProvider类型的Bean时，创建一个新的实例
     *
     * @param properties 飞书OAuth2配置属性
     * @return FeishuLoginProvider 飞书登录提供者实例
     */
    @Bean
    @ConditionalOnMissingBean(FeishuLoginProvider.class)
    public FeishuLoginProvider feishuLoginProvider(Oauth2FeishuProperties properties) {
        return new FeishuLoginProvider(properties);
    }
}


