package com.gls.athena.security.oauth2.client.wechat.mp.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 微信公众号AccessToken请求参数类
 * 用于封装微信公众号OAuth2授权获取AccessToken的请求参数
 *
 * @author george
 */
@Data
@Accessors(chain = true)
public class AccessTokenRequest implements Serializable {

    /**
     * 应用ID，微信公众号的唯一标识
     */
    private String appid;

    /**
     * 应用密钥，微信公众号的密钥
     */
    private String secret;

    /**
     * 授权码，用户授权后微信返回的code
     */
    private String code;

    /**
     * 授权类型，固定值为"authorization_code"
     */
    private String grantType = "authorization_code";
}


