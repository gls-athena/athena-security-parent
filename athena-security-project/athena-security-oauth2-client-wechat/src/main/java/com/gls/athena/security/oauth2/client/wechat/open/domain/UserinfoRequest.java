package com.gls.athena.security.oauth2.client.wechat.open.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 微信开放平台用户信息请求参数封装类
 * <p>
 * 用于封装获取微信用户基本信息所需的请求参数，包括访问令牌、用户唯一标识和语言偏好等信息
 * </p>
 *
 * @author george
 */
@Data
@Accessors(chain = true)
public class UserinfoRequest implements Serializable {

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

