package com.gls.athena.security.oauth2.client.feishu.support;

import com.gls.athena.security.oauth2.client.feishu.config.Oauth2FeishuProperties;
import com.gls.athena.security.oauth2.client.feishu.domain.*;
import com.gls.athena.security.oauth2.client.provider.SocialLoginProvider;
import com.gls.athena.security.oauth2.client.registration.SocialRegistration;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * 飞书社交登录提供者实现类，用于处理与飞书平台的OAuth2授权、令牌获取和用户信息加载等流程。
 *
 * @author george
 */
public class FeishuLoginProvider implements SocialLoginProvider {

    private final Oauth2FeishuProperties properties;
    private final FeishuClient client;

    /**
     * 构造函数，初始化飞书登录提供者实例。
     *
     * @param properties 飞书OAuth2配置属性对象，包含客户端ID、密钥等配置信息
     */
    public FeishuLoginProvider(Oauth2FeishuProperties properties) {
        this.properties = properties;
        this.client = new FeishuClient(properties);
    }

    /**
     * 自定义OAuth2授权请求的参数。
     * 主要用于将默认的 client_id 参数名替换为飞书平台所需的 app_id。
     *
     * @param builder            OAuth2授权请求构建器，用于修改授权请求的参数
     * @param clientRegistration 客户端注册信息，包含客户端的相关配置
     * @param request            HTTP请求对象，包含当前的请求信息
     */
    @Override
    public void customizeAuthorizationRequest(OAuth2AuthorizationRequest.Builder builder, ClientRegistration clientRegistration, HttpServletRequest request) {
    }

    /**
     * 获取访问令牌响应。
     * 包括获取应用访问令牌和用户访问令牌，并将其转换为标准的OAuth2访问令牌响应。
     *
     * @param authorizationGrantRequest 授权码授权请求对象，包含客户端注册信息和授权码等
     * @return OAuth2访问令牌响应对象
     */
    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        // 获取应用访问令牌
        FeishuAppAccessTokenRequest appAccessTokenRequest = FeishuConverter.convertToAppAccessTokenRequest(authorizationGrantRequest.getClientRegistration());
        FeishuAppAccessTokenResponse appAccessTokenResponse = client.getAppAccessToken(appAccessTokenRequest);

        // 获取用户访问令牌
        FeishuUserAccessTokenRequest userAccessTokenRequest = FeishuConverter.convertToUserAccessTokenRequest(authorizationGrantRequest);
        FeishuUserAccessTokenResponse userAccessTokenResponse = client.getUserAccessToken(userAccessTokenRequest, appAccessTokenResponse.getAppAccessToken());

        // 转换为标准OAuth2访问令牌响应
        return FeishuConverter.convertToAccessTokenResponse(userAccessTokenResponse);
    }

    /**
     * 加载OAuth2用户信息。
     * 使用访问令牌从飞书平台获取用户信息并转换为标准的OAuth2用户对象。
     *
     * @param userRequest OAuth2用户请求对象，包含访问令牌和客户端注册信息
     * @return OAuth2用户对象
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        // 获取访问令牌值
        String accessToken = userRequest.getAccessToken().getTokenValue();
        // 调用飞书助手获取用户信息
        FeishuUserInfoResponse userInfoResponse = client.getUserInfo(accessToken);
        // 将飞书用户信息转换为标准OAuth2用户对象
        return FeishuConverter.convertToUser(userInfoResponse, userRequest.getClientRegistration());
    }

    /**
     * 自定义社交登录注册信息。
     * 设置飞书平台相关的配置信息，如提供者ID、名称、授权URI、令牌URI等。
     *
     * @param registration 社交登录注册信息对象
     */
    @Override
    public void customizeRegistration(SocialRegistration registration) {
        // 配置飞书平台的基础认证信息
        registration.setProviderId(properties.getProviderId());
        registration.setProviderName(properties.getProviderName());
        registration.setAuthorizationUri(properties.getAuthorizationUri());
        registration.setTokenUri(properties.getTokenUri());
        registration.setUserInfoUri(properties.getUserInfoUri());
        registration.setUserNameAttributeName(properties.getUserNameAttributeName());
    }

    /**
     * 判断当前提供者是否匹配指定的提供者ID。
     *
     * @param s 提供者ID字符串
     * @return 如果匹配返回true，否则返回false
     */
    @Override
    public boolean test(String s) {
        // 比较当前配置的提供者ID与输入的提供者ID是否相等
        return properties.getProviderId().equals(s);
    }

}
