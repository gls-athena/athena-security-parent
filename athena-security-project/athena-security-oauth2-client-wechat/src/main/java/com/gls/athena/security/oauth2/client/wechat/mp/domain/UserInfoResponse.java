package com.gls.athena.security.oauth2.client.wechat.mp.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 微信公众号用户信息响应实体类
 * 用于封装微信公众号获取用户基本信息接口的返回数据
 *
 * @author george
 */
@Data
public class UserInfoResponse implements Serializable {
    /**
     * 用户的唯一标识
     */
    private String openid;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户性别，1为男性，2为女性
     */
    private String sex;

    /**
     * 用户个人资料填写的省份
     */
    private String province;

    /**
     * 用户个人资料填写的城市
     */
    private String city;

    /**
     * 国家，如中国为CN
     */
    private String country;

    /**
     * 用户头像URL，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像）
     */
    @JsonProperty("headimgurl")
    private String headImgUrl;

    /**
     * 用户特权信息，json数组，如微信沃卡用户为chinaunicom
     */
    private String privilege;

    /**
     * 只有在用户将公众号绑定到微信开放平台账号后，才会出现该字段
     */
    private String unionid;
}


