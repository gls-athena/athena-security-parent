package com.gls.athena.security.captcha.config;

import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 验证码配置
 * 用于配置验证码相关的参数，包括各种验证码类型的配置属性
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".security.captcha")
public class CaptchaProperties extends BaseProperties {
    /**
     * 验证码类型参数名
     * 用于指定请求中验证码类型的参数名称
     */
    private String typeParam = "captchaType";
    /**
     * 登录URL
     * 指定系统登录接口的URL路径
     */
    private String loginUrl = "/rest/login";
    /**
     * OAuth2 token URL
     * 指定OAuth2获取token的接口URL路径
     */
    private String oauth2TokenUrl = "/oauth2/token";
    /**
     * 邮件验证码配置
     * 包含邮件验证码相关的配置属性
     */
    @NestedConfigurationProperty
    private EmailCaptchaProperties email = new EmailCaptchaProperties();
    /**
     * 短信验证码配置
     * 包含短信验证码相关的配置属性
     */
    @NestedConfigurationProperty
    private SmsCaptchaProperties sms = new SmsCaptchaProperties();
    /**
     * 图形验证码配置
     * 包含图形验证码相关的配置属性
     */
    @NestedConfigurationProperty
    private ImageCaptchaProperties image = new ImageCaptchaProperties();
}

