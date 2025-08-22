package com.gls.athena.security.oauth2.client.wechat.mp.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 微信用户信息请求参数类
 * <p>
 * 用于封装调用微信公众平台用户信息接口所需的请求参数
 * </p>
 *
 * @author george
 */
@Data
@Accessors(chain = true)
public class UserInfoRequest implements Serializable {
    /**
     * 访问令牌，用于接口调用的身份验证
     */
    private String accessToken;

    /**
     * 用户唯一标识，微信用户的openid
     */
    private String openId;

    /**
     * 国家地区语言版本，zh_CN 简体，zh_TW 繁体，en 英语，默认为zh-CN
     */
    private String lang;
}


