package com.gls.athena.security.oauth2.client.wechat.config;

import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import com.gls.athena.security.oauth2.client.wechat.mini.WechatMiniProperties;
import com.gls.athena.security.oauth2.client.wechat.mp.WechatMpProperties;
import com.gls.athena.security.oauth2.client.wechat.open.WechatOpenProperties;
import com.gls.athena.security.oauth2.client.wechat.work.WechatWorkProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 微信OAuth2客户端配置类
 * 用于管理微信各种平台的OAuth2配置信息，包括小程序、公众号、企业号和开放平台
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".security.oauth2.client.wechat")
public class Oauth2WechatProperties extends BaseProperties {
    /**
     * 微信小程序配置
     */
    @NestedConfigurationProperty
    private WechatMiniProperties mini = new WechatMiniProperties();
    /**
     * 微信公众号配置
     */
    @NestedConfigurationProperty
    private WechatMpProperties mp = new WechatMpProperties();
    /**
     * 微信企业号配置
     */
    @NestedConfigurationProperty
    private WechatWorkProperties work = new WechatWorkProperties();
    /**
     * 微信开放平台配置
     */
    @NestedConfigurationProperty
    private WechatOpenProperties open = new WechatOpenProperties();
}

