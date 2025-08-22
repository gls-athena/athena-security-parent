package com.gls.athena.security.oauth2.client.wechat.work;

import com.gls.athena.security.oauth2.client.provider.SocialLoginProvider;
import com.gls.athena.security.oauth2.client.registration.SocialRegistration;
import com.gls.athena.security.oauth2.client.wechat.work.domain.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信企业登录提供者实现类，用于处理微信企业登录相关的授权、获取访问令牌、加载用户信息等操作。
 *
 * @author george
 */
public class WechatWorkLoginProvider implements SocialLoginProvider {

    private final WechatWorkProperties properties;
    private final WechatWorkClient client;

    /**
     * 构造函数，初始化微信企业登录提供者。
     *
     * @param properties 微信企业登录配置属性
     */
    public WechatWorkLoginProvider(WechatWorkProperties properties) {
        this.properties = properties;
        this.client = new WechatWorkClient(properties);
    }

    /**
     * 自定义授权请求参数，根据微信企业登录要求设置特定的参数。
     *
     * @param builder            OAuth2 授权请求构建器
     * @param clientRegistration 客户端注册信息
     * @param request            HTTP 请求对象
     */
    @Override
    public void customizeAuthorizationRequest(OAuth2AuthorizationRequest.Builder builder, ClientRegistration clientRegistration, HttpServletRequest request) {
        // 构建微信企业登录所需的授权请求参数
        builder.parameters(parameters -> {
            Map<String, Object> map = new HashMap<>();
            map.put("login_type", properties.getLoginType());
            map.put("appid", parameters.get(OAuth2ParameterNames.CLIENT_ID));
            map.put("agentid", properties.getAgentId());
            map.put("redirect_uri", parameters.get(OAuth2ParameterNames.REDIRECT_URI));
            map.put("state", parameters.get(OAuth2ParameterNames.STATE));
            map.put("lang", properties.getLang());

            parameters.clear();
            parameters.putAll(map);
        });

    }

    /**
     * 获取访问令牌响应，包括通过授权码换取 access_token 和 user_ticket 换取 userid。
     *
     * @param authorizationGrantRequest OAuth2 授权码授权请求
     * @return OAuth2 访问令牌响应
     */
    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        // 将授权码请求转换为访问令牌请求并获取访问令牌
        AccessTokenRequest accessTokenRequest = WechatWorkConverter.convertToAccessTokenRequest(authorizationGrantRequest);
        AccessTokenResponse accessTokenResponse = client.getAccessToken(accessTokenRequest);

        // 基于授权码请求和访问令牌响应获取用户ID
        UserIdRequest userIdRequest = WechatWorkConverter.convertToUserIdRequest(authorizationGrantRequest, accessTokenResponse);
        UserIdResponse userIdResponse = client.getUserId(userIdRequest);

        // 转换并返回最终的访问令牌响应
        return WechatWorkConverter.convertToAccessTokenResponse(authorizationGrantRequest, accessTokenResponse, userIdResponse);
    }

    /**
     * 加载 OAuth2 用户信息。
     *
     * @param userRequest OAuth2 用户请求对象
     * @return OAuth2 用户对象
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        // 转换用户信息请求
        UserInfoRequest userInfoRequest = WechatWorkConverter.convertToUserInfoRequest(userRequest);
        // 获取用户信息响应
        UserInfoResponse userInfoResponse = client.getUserInfo(userInfoRequest);
        // 转换并返回用户对象
        return WechatWorkConverter.convertToUser(userInfoResponse, userRequest.getClientRegistration());
    }

    /**
     * 自定义社交登录注册信息，填充微信企业登录相关配置。
     *
     * @param registration 社交登录注册信息对象
     */
    @Override
    public void customizeRegistration(SocialRegistration registration) {
        registration.setProviderId(properties.getProviderId());
        registration.setProviderName(properties.getProviderName());
        registration.setAuthorizationUri(properties.getAuthorizationUri());
        registration.setTokenUri(properties.getTokenUri());
        registration.setUserInfoUri(properties.getUserInfoUri());
        registration.setUserNameAttributeName(properties.getUserNameAttributeName());
        registration.setScopes(properties.getScopes());
    }

    /**
     * 判断当前提供者是否匹配指定的 providerId。
     *
     * @param s 提供者标识符
     * @return 如果匹配返回 true，否则返回 false
     */
    @Override
    public boolean test(String s) {
        return properties.getProviderId().equals(s);
    }
}
