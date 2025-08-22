package com.gls.athena.security.oauth2.client.wechat.mp;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * 微信公众号常量
 * <p>
 * 该类用于定义微信公众号相关的常量
 * </p>
 *
 * @author george
 */
@Data
public class WechatMpProperties implements Serializable {

    /**
     * 提供商ID，默认为"wechat_mp"
     */
    private String providerId = "wechat_mp";

    /**
     * 提供商名称，默认为"微信公众号"
     */
    private String providerName = "微信公众号";

    /**
     * 授权URL，用于获取授权码
     */
    private String authorizationUri = "https://open.weixin.qq.com/connect/oauth2/authorize";

    /**
     * 获取访问令牌的URL
     */
    private String tokenUri = "https://api.weixin.qq.com/sns/oauth2/access_token";

    /**
     * 获取用户信息的URL
     */
    private String userInfoUri = "https://api.weixin.qq.com/sns/userinfo";

    /**
     * 用户名属性名称，默认为"openid"
     */
    private String userNameAttributeName = "openid";

    /**
     * 授权作用域集合，默认包含"snsapi_userinfo"
     */
    private Set<String> scopes = Set.of("snsapi_userinfo");

    /**
     * 语言设置，默认为"zh_CN"
     */
    private String lang = "zh_CN";
}


