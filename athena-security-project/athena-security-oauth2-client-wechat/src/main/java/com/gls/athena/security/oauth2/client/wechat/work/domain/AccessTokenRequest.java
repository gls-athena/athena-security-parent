package com.gls.athena.security.oauth2.client.wechat.work.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 企业微信访问令牌请求实体类
 * <p>
 * 用于封装获取企业微信访问令牌所需的请求参数
 * </p>
 *
 * @author george
 */
@Data
@Accessors(chain = true)
public class AccessTokenRequest implements Serializable {

    /**
     * 企业ID
     * <p>
     * 企业微信中的企业唯一标识
     * </p>
     */
    private String corpId;

    /**
     * 企业密钥
     * <p>
     * 企业微信中用于身份验证的密钥
     * </p>
     */
    private String corpSecret;
}


