package com.gls.athena.security.oauth2.client.wechat.work.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 企业微信用户ID响应实体类
 * 用于封装企业微信API返回的用户标识信息
 *
 * @author george
 */
@Data
public class UserIdResponse implements Serializable {

    /**
     * 企业微信用户ID
     */
    private String userid;

    /**
     * 用户在微信开放平台的唯一标识
     */
    private String openid;

    /**
     * 外部联系人用户ID
     */
    @JsonProperty("external_userid")
    private String externalUserid;

    /**
     * 错误代码
     */
    @JsonProperty("errcode")
    private Integer errCode;

    /**
     * 错误信息
     */
    @JsonProperty("errmsg")
    private String errMsg;
}
