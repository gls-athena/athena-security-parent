package com.gls.athena.security.oauth2.client.wechat.open.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 微信开放平台访问令牌请求参数类
 * 用于封装获取微信访问令牌所需的请求参数
 *
 * @author george
 */
@Data
@Accessors(chain = true)
public class AccessTokenRequest implements Serializable {

    /**
     * 应用唯一标识，在微信开放平台提交应用审核通过后获得
     */
    private String appid;

    /**
     * 应用密钥，在微信开放平台提交应用审核通过后获得
     */
    private String secret;

    /**
     * 填写第一步获取的code参数
     */
    private String code;

    /**
     * 填写为authorization_code
     */
    private String grantType = "authorization_code";
}


