package com.gls.athena.security.oauth2.client.config;

/**
 * OAuth2客户端常量接口
 * 该接口定义了OAuth2客户端相关的常量值，用于统一管理会话键名和提供商标识
 *
 * @author george
 */
public interface Oauth2ClientConstants {
    /**
     * 社交用户会话键名
     * 用于在会话中存储社交用户信息的键名
     */
    String SOCIAL_USER_SESSION_KEY = "socialUser";

    /**
     * 提供商标识
     * 用于标识不同的OAuth2提供商的键名
     */
    String PROVIDER_ID = "providerId";

    /**
     * 提供商名称
     * 用于存储OAuth2提供商的名称的键名
     */
    String PROVIDER_NAME = "providerName";
}

