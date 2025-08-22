package com.gls.athena.security.oauth2.client.wechat.mini.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 微信小程序访问令牌请求实体类
 * 用于封装获取微信小程序访问令牌所需的请求参数
 *
 * @author george
 */
@Data
@Accessors(chain = true)
public class AccessTokenRequest implements Serializable {

    /**
     * 应用ID，微信小程序的唯一标识
     */
    private String appId;

    /**
     * 应用密钥，用于接口调用的身份验证
     */
    private String secret;

    /**
     * 授权类型，固定值为"client_credential"
     */
    private String grantType = "client_credential";
}