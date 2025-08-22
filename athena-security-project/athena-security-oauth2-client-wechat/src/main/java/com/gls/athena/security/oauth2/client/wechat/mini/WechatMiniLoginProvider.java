package com.gls.athena.security.oauth2.client.wechat.mini;

import com.gls.athena.security.oauth2.client.provider.SocialLoginProvider;
import com.gls.athena.security.oauth2.client.registration.SocialRegistration;
import com.gls.athena.security.oauth2.client.wechat.mini.domain.AccessTokenRequest;
import com.gls.athena.security.oauth2.client.wechat.mini.domain.AccessTokenResponse;
import com.gls.athena.security.oauth2.client.wechat.mini.domain.Code2SessionRequest;
import com.gls.athena.security.oauth2.client.wechat.mini.domain.Code2SessionResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信小程序登录提供者实现类
 * 实现了SocialLoginProvider接口，用于处理微信小程序的OAuth2登录流程
 *
 * @author george
 */
public class WechatMiniLoginProvider implements SocialLoginProvider {

    private final WechatMiniProperties properties;
    private final WechatMiniClient client;

    /**
     * 构造函数，初始化微信小程序登录提供者
     *
     * @param properties 微信小程序配置属性对象
     */
    public WechatMiniLoginProvider(WechatMiniProperties properties) {
        this.properties = properties;
        this.client = new WechatMiniClient(properties);
    }

    /**
     * 自定义OAuth2授权请求参数
     * 该方法用于处理小程序OAuth2授权请求的参数定制，主要提取请求中的code和state参数
     *
     * @param builder            OAuth2授权请求构建器，用于构建和修改授权请求参数
     * @param clientRegistration 客户端注册信息，包含客户端的相关配置
     * @param request            HTTP请求对象，用于获取请求参数
     */
    @Override
    public void customizeAuthorizationRequest(OAuth2AuthorizationRequest.Builder builder, ClientRegistration clientRegistration, HttpServletRequest request) {
        // 小程序 OAuth2 授权请求参数处理
        builder.parameters(parameters -> {
            // 构建新的参数映射，只保留code和state参数
            Map<String, Object> result = new HashMap<>(2);
            result.put("code", request.getParameter("code"));
            result.put("state", parameters.get("state"));
            parameters.clear();
            parameters.putAll(result);
        });
    }

    /**
     * 获取访问令牌响应
     * 通过授权码获取访问令牌，并调用微信接口获取会话信息，最终转换为标准的OAuth2访问令牌响应
     *
     * @param authorizationGrantRequest OAuth2授权码授予请求，包含获取令牌所需的信息
     * @return OAuth2访问令牌响应对象
     */
    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        // 将OAuth2授权码请求转换为访问令牌请求
        AccessTokenRequest accessTokenRequest = WechatMiniConverter.convertToAccessTokenRequest(authorizationGrantRequest);
        // 调用微信客户端获取访问令牌
        AccessTokenResponse accessTokenResponse = client.getAccessToken(accessTokenRequest);
        // 将OAuth2授权码请求转换为微信code2Session请求
        Code2SessionRequest code2SessionRequest = WechatMiniConverter.convertToCode2SessionRequest(authorizationGrantRequest);
        // 调用微信客户端获取会话信息
        Code2SessionResponse code2SessionResponse = client.code2Session(code2SessionRequest);
        // 将微信响应转换为标准的OAuth2访问令牌响应
        return WechatMiniConverter.convertToAccessTokenResponse(authorizationGrantRequest, accessTokenResponse, code2SessionResponse);
    }

    /**
     * 加载OAuth2用户信息
     * 根据用户请求中的附加参数和客户端注册信息，转换并返回OAuth2用户对象
     *
     * @param userRequest OAuth2用户请求，包含用户相关信息和客户端配置
     * @return OAuth2用户对象
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        // 调用转换器将请求参数和客户端注册信息转换为OAuth2用户对象
        return WechatMiniConverter.convertToUser(userRequest.getAdditionalParameters(), userRequest.getClientRegistration());
    }

    /**
     * 自定义社交注册信息
     * 设置微信小程序相关的注册信息，如提供商ID、名称、授权URI等
     *
     * @param registration 社交注册信息对象，用于设置相关属性
     */
    @Override
    public void customizeRegistration(SocialRegistration registration) {
        // 设置社交注册的基本信息
        registration.setProviderId(properties.getProviderId());
        registration.setProviderName(properties.getProviderName());
        registration.setAuthorizationUri(properties.getAuthorizationUri());
        registration.setTokenUri(properties.getTokenUri());
        registration.setUserNameAttributeName(properties.getUserNameAttributeName());
    }

    /**
     * 测试是否匹配指定的提供商ID
     * 判断当前提供者的ID是否与传入的字符串相等
     *
     * @param s 要比较地提供商ID字符串
     * @return 如果匹配返回true，否则返回false
     */
    @Override
    public boolean test(String s) {
        return properties.getProviderId().equals(s);
    }
}
