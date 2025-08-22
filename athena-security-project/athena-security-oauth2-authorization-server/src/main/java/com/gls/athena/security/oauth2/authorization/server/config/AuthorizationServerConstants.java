package com.gls.athena.security.oauth2.authorization.server.config;

import org.springframework.security.oauth2.core.AuthorizationGrantType;

/**
 * 授权服务器常量定义接口
 * 用于定义系统支持的各种授权类型常量
 *
 * @author george
 */
public interface AuthorizationServerConstants {

    /**
     * 密码授权类型
     * 用于用户名密码认证的授权流程
     */
    AuthorizationGrantType PASSWORD = new AuthorizationGrantType("password");

    /**
     * 手机号授权类型
     * 用于手机号码认证的授权流程
     */
    AuthorizationGrantType MOBILE = new AuthorizationGrantType("mobile");

    /**
     * 邮箱授权类型
     * 用于邮箱认证的授权流程
     */
    AuthorizationGrantType EMAIL = new AuthorizationGrantType("email");

    /**
     * 邮箱参数名称
     * 用于邮箱认证的参数名称
     */
    String EMAIL_PARAMETER_NAME = "email";

    /**
     * 验证码参数名称
     * 用于验证码认证的参数名称
     */
    String EMAIL_CAPTCHA_PARAMETER_NAME = "emailCaptcha";

    /**
     * 手机号参数名称
     * 用于手机号认证的参数名称
     */
    String MOBILE_PARAMETER_NAME = "mobile";

    /**
     * 手机号验证码参数名称
     * 用于手机号认证的参数名称
     */
    String MOBILE_CAPTCHA_PARAMETER_NAME = "smsCaptcha";
}


