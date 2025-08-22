package com.gls.athena.security.oauth2.client.wechat.open.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 微信开放平台访问令牌响应实体类
 * <p>
 * 该类用于封装微信开放平台OAuth2认证后返回的访问令牌信息，
 * 包含访问令牌、刷新令牌、用户标识等关键信息
 *
 * @author george
 */
@Data
public class AccessTokenResponse implements Serializable {

    /**
     * 访问令牌，用于调用微信开放平台API的凭证
     */
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * 访问令牌过期时间，单位为秒
     */
    @JsonProperty("expires_in")
    private Integer expiresIn;

    /**
     * 刷新令牌，用于获取新的访问令牌
     */
    @JsonProperty("refresh_token")
    private String refreshToken;

    /**
     * 用户唯一标识，针对当前应用唯一
     */
    @JsonProperty("openid")
    private String openId;

    /**
     * 用户授权的作用域，表示用户授权的权限范围
     */
    private String scope;

    /**
     * 用户在开放平台的唯一标识，针对同一个微信开放平台账号下的不同应用，
     * 同一用户的unionid是相同的
     */
    @JsonProperty("unionid")
    private String unionId;
}

