package com.gls.athena.security.oauth2.client.feishu.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 飞书用户访问令牌响应
 * 用于封装飞书OAuth2认证后返回的用户访问令牌信息
 *
 * @author george
 */
@Data
public class FeishuUserAccessTokenResponse {
    /**
     * 访问令牌
     * 用于访问受保护资源的令牌
     */
    @JsonProperty("access_token")
    private String accessToken;
    /**
     * 刷新令牌
     * 用于获取新的访问令牌的令牌
     */
    @JsonProperty("refresh_token")
    private String refreshToken;
    /**
     * 令牌类型
     * 表示令牌的类型，通常是Bearer
     */
    @JsonProperty("token_type")
    private String tokenType;
    /**
     * 令牌有效期
     * 访问令牌的有效期，单位为秒
     */
    @JsonProperty("expires_in")
    private Integer expiresIn;
    /**
     * 刷新令牌有效期
     * 刷新令牌的有效期，单位为秒
     */
    @JsonProperty("refresh_expires_in")
    private Integer refreshExpiresIn;
    /**
     * 用户授予的权限范围
     * 表示用户授权给应用的权限范围
     */
    private String scope;
}

