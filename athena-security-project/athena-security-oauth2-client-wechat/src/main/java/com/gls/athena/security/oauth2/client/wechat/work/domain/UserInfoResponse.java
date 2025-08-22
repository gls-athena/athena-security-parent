package com.gls.athena.security.oauth2.client.wechat.work.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 企业微信用户信息响应实体类
 * 用于封装企业微信获取用户详细信息接口的返回数据
 *
 * @author george
 */
@Data
public class UserInfoResponse implements Serializable {

    /**
     * 错误代码
     * 0表示成功，非0表示失败
     */
    @JsonProperty("errcode")
    private Integer errCode;

    /**
     * 错误信息
     * 当errcode非0时，提供具体的错误描述
     */
    @JsonProperty("errmsg")
    private String errMsg;

    /**
     * 用户ID
     * 用户在企业微信中的唯一标识
     */
    @JsonProperty("userid")
    private String userId;

    /**
     * 用户姓名
     */
    @JsonProperty("name")
    private String name;

    /**
     * 手机号码
     */
    @JsonProperty("mobile")
    private String mobile;

    /**
     * 所属部门ID列表
     * 用户所在的部门ID集合
     */
    @JsonProperty("department")
    private List<Integer> department;

    /**
     * 部门内排序值
     * 对应部门中的排序顺序
     */
    @JsonProperty("order")
    private List<Integer> order;

    /**
     * 职位信息
     */
    @JsonProperty("position")
    private String position;

    /**
     * 性别
     * "1"表示男性，"2"表示女性
     */
    @JsonProperty("gender")
    private String gender;

    /**
     * 邮箱地址
     */
    @JsonProperty("email")
    private String email;

    /**
     * 企业邮箱
     */
    @JsonProperty("biz_mail")
    private String bizMail;

    /**
     * 是否为部门领导
     * 与department字段一一对应，1表示是领导，0表示不是
     */
    @JsonProperty("is_leader_in_dept")
    private List<Integer> isLeaderInDept;

    /**
     * 直属领导ID
     */
    @JsonProperty("direct_leader")
    private String directLeader;

    /**
     * 头像URL
     */
    @JsonProperty("avatar")
    private String avatar;

    /**
     * 缩略头像URL
     */
    @JsonProperty("thumb_avatar")
    private String thumbAvatar;

    /**
     * 座机号码
     */
    @JsonProperty("telephone")
    private String telephone;

    /**
     * 别名
     */
    @JsonProperty("alias")
    private String alias;

    /**
     * 扩展属性
     */
    @JsonProperty("extattr")
    private String extAttr;

    /**
     * 用户状态
     * 1表示已激活，2表示已禁用，4表示未激活，5表示已退出
     */
    @JsonProperty("status")
    private String status;

    /**
     * 二维码链接
     * 用户个人二维码的访问地址
     */
    @JsonProperty("qr_code")
    private String qrCode;

    /**
     * 对外属性
     */
    @JsonProperty("external_profile")
    private String externalProfile;

    /**
     * 对外职务
     */
    @JsonProperty("external_position")
    private String externalPosition;

    /**
     * 地址
     */
    @JsonProperty("address")
    private String address;

    /**
     * 全局用户ID
     * 用户在服务商应用下的唯一标识
     */
    @JsonProperty("open_userid")
    private String openUserId;

    /**
     * 主部门ID
     */
    @JsonProperty("main_department")
    private String mainDepartment;
}
