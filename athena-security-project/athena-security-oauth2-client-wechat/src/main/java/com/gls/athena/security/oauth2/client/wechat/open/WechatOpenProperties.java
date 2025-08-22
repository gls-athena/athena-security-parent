package com.gls.athena.security.oauth2.client.wechat.open;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * 微信开放平台常量
 * <p>
 * 该类用于定义与微信开放平台相关的常量
 * </p>
 *
 * @author george
 */
@Data
public class WechatOpenProperties implements Serializable {

    /**
     * 提供商ID
     */
    private String providerId = "wechat_open";

    /**
     * 提供商名称
     */
    private String providerName = "微信开放平台";

    /**
     * 授权URI
     */
    private String authorizationUri = "https://open.weixin.qq.com/connect/qrconnect";

    /**
     * 获取访问令牌URI
     */
    private String tokenUri = "https://api.weixin.qq.com/sns/oauth2/access_token";

    /**
     * 获取用户信息URI
     */
    private String userInfoUri = "https://api.weixin.qq.com/sns/userinfo";

    /**
     * 用户名属性名称
     */
    private String userNameAttributeName = "openid";

    /**
     * 授权范围
     */
    private Set<String> scopes = Set.of("snsapi_login");

    /**
     * 语言设置
     */
    private String lang = "zh_CN";
}


