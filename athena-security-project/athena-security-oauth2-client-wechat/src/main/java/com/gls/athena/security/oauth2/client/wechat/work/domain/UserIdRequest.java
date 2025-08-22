package com.gls.athena.security.oauth2.client.wechat.work.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 企业微信用户ID请求参数类
 * 用于封装获取用户ID所需的请求参数
 *
 * @author george
 */
@Data
@Accessors(chain = true)
public class UserIdRequest implements Serializable {
    /**
     * 访问令牌
     */
    private String accessToken;
    /**
     * 授权码
     */
    private String code;

}
