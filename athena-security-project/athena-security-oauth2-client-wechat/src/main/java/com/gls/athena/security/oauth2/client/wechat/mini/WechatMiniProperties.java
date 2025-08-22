package com.gls.athena.security.oauth2.client.wechat.mini;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * 微信小程序配置类
 * 用于配置微信小程序OAuth2认证相关的各种URL和属性信息
 *
 * @author george
 */
@Data
public class WechatMiniProperties implements Serializable {

    /**
     * 提供商ID，默认为"wechat_mini"
     */
    private String providerId = "wechat_mini";

    /**
     * 提供商名称，默认为"微信小程序"
     */
    private String providerName = "微信小程序";

    /**
     * 授权URI，默认为"/login/oauth2/code/wechat_mini"
     */
    private String authorizationUri = "/login/oauth2/code/wechat_mini";

    /**
     * 获取访问令牌的URI，指向微信API获取token接口
     */
    private String tokenUri = "https://api.weixin.qq.com/cgi-bin/token";

    /**
     * 获取用户信息的URI，指向微信小程序登录凭证校验接口
     */
    private String code2SessionUri = "https://api.weixin.qq.com/sns/jscode2session";

    /**
     * 用户名属性名称，默认使用"openId"作为唯一标识
     */
    private String userNameAttributeName = "openId";
    /**
     * 获取用户信息的权限范围
     */
    private Set<String> scopes = Set.of("snsapi_userinfo", "snsapi_base");

    /**
     * 缓存中存储的access_token的key，默认使用"wechat_mini:access_token"
     */
    private String accessTokenCacheName = "wechat_mini:access_token";

}

