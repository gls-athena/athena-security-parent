package com.gls.athena.security.oauth2.client.wechat.work.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 企业微信用户信息请求参数类
 * <p>
 * 用于封装获取企业微信用户信息所需的请求参数，包括访问令牌和用户ID
 * </p>
 *
 * @author george
 */
@Data
@Accessors(chain = true)
public class UserInfoRequest implements Serializable {
    /**
     * 访问令牌
     */
    private String accessToken;
    /**
     * 用户ID
     */
    private String userId;

}

