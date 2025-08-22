package com.gls.athena.security.captcha.config;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 邮箱验证码配置属性类
 * 用于配置邮箱验证码相关的参数，包括验证码参数名、过期时间、发送间隔、邮箱参数名、验证码接口地址等
 *
 * @author george
 */
@Data
public class EmailCaptchaProperties implements Serializable {

    /**
     * 验证码参数名称，默认为"emailCaptcha"
     * 用于在请求参数中识别验证码字段
     */
    private String captchaParam = "emailCaptcha";

    /**
     * 验证码过期时间，单位毫秒，默认60000毫秒(1分钟)
     * 超过此时间后验证码失效
     */
    private int captchaExpire = 60000;

    /**
     * 验证码发送间隔时间，单位毫秒，默认60000毫秒(1分钟)
     * 用于控制同一邮箱发送验证码的最小时间间隔
     */
    private long captchaInterval = 60000;

    /**
     * 验证码长度，默认为8
     * 用于生成验证码的长度
     */
    private int captchaLength = 8;

    /**
     * 邮箱参数名称，默认为"email"
     * 用于在请求参数中识别邮箱字段
     */
    private String emailParam = "email";

    /**
     * 验证码接口地址，默认为"/captcha/email"
     * 用于接收发送验证码请求的URL路径
     */
    private String captchaUrl = "/captcha/email";

    /**
     * 验证码发送邮件模板ID，默认为"EMAIL_12345678"
     * 用于指定发送验证码的邮件模板ID
     */
    private String captchaTemplateId = "EMAIL_12345678";

    /**
     * 需要验证验证码的URL列表
     * 存储需要进行验证码校验的接口地址集合
     */
    private List<String> captchaCheckUrls = new ArrayList<>();
}

