package com.gls.athena.security.oauth2.client.wechat.work;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * 微信企业号常量配置类
 * <p>
 * 该类用于封装与微信企业号OAuth2认证相关的各种配置信息，包括授权地址、令牌获取地址、用户信息接口等。
 * </p>
 *
 * @author george
 */
@Data
public class WechatWorkProperties implements Serializable {

    /**
     * 提供商ID，默认值为"wechat_work"
     */
    private String providerId = "wechat_work";

    /**
     * 提供商名称，默认值为"微信企业号"
     */
    private String providerName = "微信企业号";

    /**
     * 授权URI，用于获取授权码
     * 微信企业号登录授权地址
     */
    private String authorizationUri = "https://login.work.weixin.qq.com/wwlogin/sso/login";

    /**
     * 令牌URI，用于获取访问令牌
     * 微信企业号获取access_token的接口地址
     */
    private String tokenUri = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";

    /**
     * 用户ID URI，用于获取用户ID信息
     * 微信企业号获取用户信息的接口地址
     */
    private String userIdUri = "https://qyapi.weixin.qq.com/cgi-bin/auth/getuserinfo";

    /**
     * 用户信息URI，用于获取详细用户信息
     * 微信企业号获取用户详细信息的接口地址
     */
    private String userInfoUri = "https://qyapi.weixin.qq.com/cgi-bin/user/get";

    /**
     * 用户名属性名称，默认值为"UserId"
     * OAuth2响应中表示用户唯一标识的属性名
     */
    private String userNameAttributeName = "userid";

    /**
     * 登录方式，默认值为"CorpApp"
     * 登录方式，目前支持"CorpApp"和"ServiceApp"两种方式
     */
    private String loginType = "CorpApp";

    /**
     * 应用id
     * 企业微信应用id
     */
    private String agentId;

    /**
     * 语言类型。zh：中文；en：英文。
     */
    private String lang = "zh_CN";

    /**
     * 授权范围，默认值为"snsapi_base"和"snsapi_privateinfo"
     * 定义OAuth2授权请求中需要的权限范围
     */
    private Set<String> scopes = Set.of("snsapi_base", "snsapi_privateinfo");

    /**
     * 缓存名称，默认值为"wechat_work:access_token"
     * 用于缓存微信企业号访问令牌
     */
    private String accessTokenCacheName = "wechat_work:access_token";
}
