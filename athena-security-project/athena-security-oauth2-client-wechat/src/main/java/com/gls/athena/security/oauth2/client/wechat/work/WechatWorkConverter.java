package com.gls.athena.security.oauth2.client.wechat.work;

import cn.hutool.core.bean.BeanUtil;
import com.gls.athena.security.oauth2.client.wechat.work.domain.*;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * 微信企业版 OAuth2 转换器，用于将 Spring Security OAuth2 的请求和响应对象转换为微信企业版所需的格式。
 *
 * @author george
 */
@UtilityClass
public class WechatWorkConverter {

    /**
     * 将授权码请求转换为获取访问令牌的请求对象。
     *
     * @param authorizationGrantRequest OAuth2 授权码授权请求对象
     * @return 微信企业版访问令牌请求对象
     */
    public AccessTokenRequest convertToAccessTokenRequest(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        return new AccessTokenRequest()
                .setCorpId(authorizationGrantRequest.getClientRegistration().getClientId())
                .setCorpSecret(authorizationGrantRequest.getClientRegistration().getClientSecret());
    }

    /**
     * 将授权码请求和访问令牌响应转换为获取用户 ID 的请求对象。
     *
     * @param authorizationGrantRequest OAuth2 授权码授权请求对象
     * @param accessTokenResponse       微信企业版访问令牌响应对象
     * @return 微信企业版用户 ID 请求对象
     */
    public UserIdRequest convertToUserIdRequest(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest, AccessTokenResponse accessTokenResponse) {
        return new UserIdRequest()
                .setAccessToken(accessTokenResponse.getAccessToken())
                .setCode(authorizationGrantRequest.getAuthorizationExchange().getAuthorizationResponse().getCode());
    }

    /**
     * 将微信企业版的访问令牌响应和用户 ID 响应转换为标准的 OAuth2 访问令牌响应。
     *
     * @param authorizationGrantRequest OAuth2 授权码授权请求对象
     * @param accessTokenResponse       微信企业版访问令牌响应对象
     * @param userIdResponse            微信企业版用户 ID 响应对象
     * @return 标准 OAuth2 访问令牌响应对象
     */
    public OAuth2AccessTokenResponse convertToAccessTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest,
                                                                  AccessTokenResponse accessTokenResponse,
                                                                  UserIdResponse userIdResponse) {
        return OAuth2AccessTokenResponse.withToken(accessTokenResponse.getAccessToken())
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .expiresIn(accessTokenResponse.getExpiresIn())
                .scopes(authorizationGrantRequest.getClientRegistration().getScopes())
                .additionalParameters(BeanUtil.beanToMap(userIdResponse))
                .build();
    }

    /**
     * 将 OAuth2 用户请求转换为获取用户信息的请求对象。
     *
     * @param userRequest OAuth2 用户请求对象
     * @return 微信企业版用户信息请求对象
     */
    public UserInfoRequest convertToUserInfoRequest(OAuth2UserRequest userRequest) {
        return new UserInfoRequest()
                .setAccessToken(userRequest.getAccessToken().getTokenValue())
                .setUserId(userRequest.getAdditionalParameters().get("userid").toString());
    }

    /**
     * 将微信企业版用户信息响应和客户端注册信息转换为标准的 OAuth2 用户对象。
     *
     * @param userInfoResponse   微信企业版用户信息响应对象
     * @param clientRegistration 客户端注册信息
     * @return 标准 OAuth2 用户对象
     */
    public OAuth2User convertToUser(UserInfoResponse userInfoResponse, ClientRegistration clientRegistration) {
        return new DefaultOAuth2User(
                AuthorityUtils.createAuthorityList("WECHAT_WORK_USER"),
                BeanUtil.beanToMap(userInfoResponse),
                clientRegistration.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName());
    }
}
