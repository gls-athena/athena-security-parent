package com.gls.athena.security.oauth2.client.wechat.mini.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 微信小程序登录凭证校验请求参数类
 * 用于封装调用微信小程序 code2Session 接口所需的请求参数
 *
 * @author george
 */
@Data
@Accessors(chain = true)
public class Code2SessionRequest implements Serializable {
    /**
     * 小程序 appId
     */
    private String appId;
    /**
     * 小程序 appSecret
     */
    private String secret;
    /**
     * 登录时获取的 code，可通过 wx.login 获取
     */
    private String jsCode;
    /**
     * 授权类型，此处只需填写 authorization_code
     */
    private String grantType = "authorization_code";
}