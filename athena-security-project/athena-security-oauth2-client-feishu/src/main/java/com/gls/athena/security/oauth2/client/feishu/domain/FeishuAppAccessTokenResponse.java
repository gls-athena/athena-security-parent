package com.gls.athena.security.oauth2.client.feishu.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应用访问令牌响应
 * <p>
 * 该类用于封装飞书开放平台返回的应用访问令牌信息，包括应用访问令牌、租户访问令牌及相关过期时间。
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FeishuAppAccessTokenResponse extends FeishuResponse<String> {
    /**
     * 应用访问令牌
     * <p>
     * 用于应用维度的身份认证，可以调用飞书开放平台提供的应用级接口。
     */
    @JsonProperty("app_access_token")
    private String appAccessToken;
    /**
     * 过期时间
     * <p>
     * 表示令牌的有效期，单位为秒。
     */
    private Integer expire;
    /**
     * 租户访问令牌
     * <p>
     * 用于租户维度的身份认证，可以调用飞书开放平台提供的租户级接口。
     */
    @JsonProperty("tenant_access_token")
    private String tenantAccessToken;
}

