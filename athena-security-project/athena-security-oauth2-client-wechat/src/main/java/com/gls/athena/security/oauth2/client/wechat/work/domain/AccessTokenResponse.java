package com.gls.athena.security.oauth2.client.wechat.work.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 企业微信访问令牌响应实体类
 * 用于封装企业微信OAuth2认证返回的访问令牌信息
 *
 * @author george
 */
@Data
public class AccessTokenResponse implements Serializable {

    /**
     * 访问令牌，用于访问企业微信API的凭证
     */
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * 令牌过期时间，单位为秒
     */
    @JsonProperty("expires_in")
    private Integer expiresIn;

    /**
     * 错误代码，0表示成功，非0表示失败
     */
    @JsonProperty("errcode")
    private Integer errCode;

    /**
     * 错误信息，当errcode非0时提供具体的错误描述
     */
    @JsonProperty("errmsg")
    private String errMsg;
}


