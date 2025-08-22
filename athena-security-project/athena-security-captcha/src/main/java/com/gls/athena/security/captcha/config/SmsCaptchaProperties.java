package com.gls.athena.security.captcha.config;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 短信验证码上下文
 * 用于配置短信验证码相关的参数和属性
 *
 * @author george
 */
@Data
public class SmsCaptchaProperties implements Serializable {
    /**
     * 验证码参数名称
     * 用于指定HTTP请求中验证码参数的名称
     */
    private String captchaParam = "smsCaptcha";
    /**
     * 验证码长度
     * 指定生成的短信验证码的位数长度
     */
    private int captchaLength = 6;
    /**
     * 验证码过期时间
     * 指定验证码的有效期，单位为毫秒
     */
    private int captchaExpire = 60000;
    /**
     * 验证码发送间隔
     * 控制同一手机号发送验证码的最小时间间隔，单位为毫秒
     */
    private long captchaInterval = 60000;
    /**
     * 手机号参数名称
     * 用于指定HTTP请求中手机号参数的名称
     */
    private String mobileParam = "mobile";
    /**
     * 验证码发送模板ID
     * 指定短信服务提供商的验证码模板标识
     */
    private String captchaTemplateId = "SMS_12345678";
    /**
     * 验证码发送URL
     * 指定发送验证码的接口路径
     */
    private String captchaSendUrl = "/captcha/sms";
    /**
     * 验证码校验URL
     * 指定需要进行验证码校验的接口路径列表
     */
    private List<String> captchaCheckUrls = new ArrayList<>();

}

