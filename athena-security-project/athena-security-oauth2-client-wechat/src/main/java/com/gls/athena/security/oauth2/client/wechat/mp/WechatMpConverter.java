package com.gls.athena.security.oauth2.client.wechat.mp;

import cn.hutool.core.bean.BeanUtil;
import com.gls.athena.security.oauth2.client.wechat.mp.domain.AccessTokenRequest;
import com.gls.athena.security.oauth2.client.wechat.mp.domain.AccessTokenResponse;
import com.gls.athena.security.oauth2.client.wechat.mp.domain.UserInfoRequest;
import com.gls.athena.security.oauth2.client.wechat.mp.domain.UserInfoResponse;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

/**
 * 微信公众号 OAuth2 转换器，用于在 Spring Security OAuth2 客户端流程中进行参数转换。
 * 包括授权码换取访问令牌、获取用户信息等操作的请求和响应转换。
 *
 * @author george
 */
@UtilityClass
public class WechatMpConverter {

    /**
     * 将 Spring Security 的授权码授权请求转换为微信公众号的访问令牌请求对象。
     *
     * @param authorizationGrantRequest Spring Security 的授权码授权请求对象
     * @return 微信公众号的访问令牌请求对象
     */
    public AccessTokenRequest convertToAccessTokenRequest(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        return new AccessTokenRequest()
                .setAppid(authorizationGrantRequest.getClientRegistration().getClientId())
                .setSecret(authorizationGrantRequest.getClientRegistration().getClientSecret())
                .setCode(authorizationGrantRequest.getAuthorizationExchange().getAuthorizationResponse().getCode());
    }

    /**
     * 将微信公众号的访问令牌响应转换为 Spring Security 的访问令牌响应对象。
     *
     * @param authorizationGrantRequest 授权请求对象，用于获取客户端注册信息和作用域
     * @param response                  微信公众号的访问令牌响应对象
     * @return Spring Security 的访问令牌响应对象，包含令牌、类型、过期时间、刷新令牌及额外参数
     */
    public OAuth2AccessTokenResponse convertToAccessTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest,
                                                                  AccessTokenResponse response) {
        return OAuth2AccessTokenResponse.withToken(response.getAccessToken())
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .expiresIn(response.getExpiresIn())
                .scopes(authorizationGrantRequest.getClientRegistration().getScopes())
                .refreshToken(response.getRefreshToken())
                // 添加额外参数：openid、scope 和 unionid
                .additionalParameters(
                        Map.of(
                                "openid", response.getOpenId(),
                                "scope", response.getScope(),
                                "unionid", response.getUnionId()
                        )
                )
                .build();
    }

    /**
     * 将 Spring Security 的用户信息请求转换为微信公众号的用户信息请求对象。
     *
     * @param userRequest Spring Security 的用户信息请求对象，包含访问令牌和客户端注册信息
     * @return 微信公众号的用户信息请求对象
     */
    public UserInfoRequest convertToUserinfoRequest(OAuth2UserRequest userRequest) {
        return new UserInfoRequest()
                .setAccessToken(userRequest.getAccessToken().getTokenValue())
                .setOpenId(userRequest.getAdditionalParameters().get("openid").toString())
                .setLang(userRequest.getClientRegistration().getProviderDetails().getConfigurationMetadata()
                        .getOrDefault("lang", "zh_CN").toString());
    }

    /**
     * 将微信公众号的用户信息响应转换为 Spring Security 的 OAuth2 用户对象。
     *
     * @param response           微信公众号的用户信息响应对象
     * @param clientRegistration 客户端注册信息，用于获取用户名属性名
     * @return Spring Security 的 OAuth2 用户对象
     */
    public OAuth2User convertToUser(UserInfoResponse response, ClientRegistration clientRegistration) {
        return new DefaultOAuth2User(
                AuthorityUtils.createAuthorityList("WECHAT_MP_USER"),
                BeanUtil.beanToMap(response),
                clientRegistration.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName());
    }
}
