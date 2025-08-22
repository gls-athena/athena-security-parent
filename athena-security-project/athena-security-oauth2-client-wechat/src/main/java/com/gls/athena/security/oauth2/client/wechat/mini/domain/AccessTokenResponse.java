package com.gls.athena.security.oauth2.client.wechat.mini.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 微信小程序访问令牌响应实体类
 * 用于封装微信小程序获取访问令牌接口的响应数据
 *
 * @author george
 */
@Data
public class AccessTokenResponse implements Serializable {

    /**
     * 访问令牌
     * 用于调用微信小程序相关接口的凭证
     */
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * 令牌有效期
     * 表示access_token的过期时间，单位为秒
     */
    @JsonProperty("expires_in")
    private Integer expiresIn;
}