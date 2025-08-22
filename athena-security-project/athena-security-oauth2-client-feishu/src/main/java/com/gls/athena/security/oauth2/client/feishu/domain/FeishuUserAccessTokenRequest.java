package com.gls.athena.security.oauth2.client.feishu.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 飞书用户访问令牌请求
 * 用于封装飞书OAuth2用户访问令牌的请求参数
 *
 * @author george
 */
@Data
@Accessors(chain = true)
public class FeishuUserAccessTokenRequest {
    /**
     * 授权类型，固定值：authorization_code
     * 用于指定OAuth2授权模式，此处使用授权码模式
     */
    @JsonProperty("grant_type")
    private String grantType = "authorization_code";
    /**
     * 登录预授权码 调用登录预授权码 获取code
     * 通过飞书授权服务器返回的授权码，用于换取访问令牌
     */
    private String code;
}

