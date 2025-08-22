package com.gls.athena.security.oauth2.client.wechat.config;

import com.gls.athena.security.oauth2.client.wechat.mini.WechatMiniLoginProvider;
import com.gls.athena.security.oauth2.client.wechat.mp.WechatMpLoginProvider;
import com.gls.athena.security.oauth2.client.wechat.open.WechatOpenLoginProvider;
import com.gls.athena.security.oauth2.client.wechat.work.WechatWorkLoginProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信OAuth2配置类，用于注册不同微信平台的登录提供者Bean。
 *
 * @author george
 */
@Configuration
public class Oauth2WechatConfig {

    /**
     * 创建微信小程序登录提供者Bean。
     * 当容器中不存在该类型的Bean时，创建并注册一个新的实例。
     *
     * @param properties OAuth2微信配置属性，包含各平台的配置信息
     * @return 微信小程序登录提供者实例
     */
    @Bean
    @ConditionalOnMissingBean(WechatMiniLoginProvider.class)
    public WechatMiniLoginProvider wechatMiniLoginProvider(Oauth2WechatProperties properties) {
        return new WechatMiniLoginProvider(properties.getMini());
    }

    /**
     * 创建企业微信登录提供者Bean。
     * 当容器中不存在该类型的Bean时，创建并注册一个新的实例。
     *
     * @param properties OAuth2微信配置属性，包含各平台的配置信息
     * @return 企业微信登录提供者实例
     */
    @Bean
    @ConditionalOnMissingBean(WechatWorkLoginProvider.class)
    public WechatWorkLoginProvider wechatWorkLoginProvider(Oauth2WechatProperties properties) {
        return new WechatWorkLoginProvider(properties.getWork());
    }

    /**
     * 创建微信公众平台登录提供者Bean。
     * 当容器中不存在该类型的Bean时，创建并注册一个新的实例。
     *
     * @param properties OAuth2微信配置属性，包含各平台的配置信息
     * @return 微信公众平台登录提供者实例
     */
    @Bean
    @ConditionalOnMissingBean(WechatMpLoginProvider.class)
    public WechatMpLoginProvider wechatMpLoginProvider(Oauth2WechatProperties properties) {
        return new WechatMpLoginProvider(properties.getMp());
    }

    /**
     * 创建微信开放平台登录提供者Bean。
     * 当容器中不存在该类型的Bean时，创建并注册一个新的实例。
     *
     * @param properties OAuth2微信配置属性，包含各平台的配置信息
     * @return 微信开放平台登录提供者实例
     */
    @Bean
    @ConditionalOnMissingBean(WechatOpenLoginProvider.class)
    public WechatOpenLoginProvider wechatOpenLoginProvider(Oauth2WechatProperties properties) {
        return new WechatOpenLoginProvider(properties.getOpen());
    }
}

