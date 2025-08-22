package com.gls.athena.security.oauth2.client.wechat.mini.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 微信小程序登录凭证校验响应数据类
 * 用于封装微信小程序调用 code2Session 接口返回的用户会话信息
 * 包含用户标识、会话密钥等信息，支持JSON序列化
 *
 * @author george
 */
@Data
public class Code2SessionResponse implements Serializable {
    /**
     * 用户唯一标识
     * 同一小程序中唯一标识一个用户
     */
    private String openid;
    /**
     * 会话密钥
     * 用于解密用户敏感数据，需要妥善保管
     */
    @JsonProperty("session_key")
    private String sessionKey;
    /**
     * 用户在开放平台的唯一标识符，若当前小程序已绑定到微信开放平台帐号下会返回，详见 UnionID 机制说明。
     * 用于识别同一微信开放平台账号下不同应用的同一用户
     */
    private String unionid;
    /**
     * 错误码，请求失败时返回
     * 0表示成功，其他值表示不同的错误类型
     */
    @JsonProperty("errcode")
    private String errCode;
    /**
     * 错误信息，请求失败时返回
     * 描述具体的错误原因
     */
    @JsonProperty("errmsg")
    private String errMsg;
}

