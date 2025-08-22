package com.gls.athena.security.oauth2.client.feishu.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 应用访问令牌请求
 * <p>
 * 用于向飞书平台申请应用访问令牌的请求数据对象，
 * 包含应用的身份凭证信息，通过appId和appSecret进行身份验证。
 * </p>
 *
 * @author george
 */
@Data
@Accessors(chain = true)
public class FeishuAppAccessTokenRequest {
    /**
     * 应用 ID
     * <p>
     * 飞书开放平台分配给应用的唯一标识符，
     * 用于标识具体的应用程序身份。
     * </p>
     */
    @JsonProperty("app_id")
    private String appId;

    /**
     * 应用密钥
     * <p>
     * 与应用ID配对使用的密钥信息，
     * 用于验证应用身份的合法性，需严格保密。
     * </p>
     */
    @JsonProperty("app_secret")
    private String appSecret;
}

