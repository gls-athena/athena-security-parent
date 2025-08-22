package com.gls.athena.security.oauth2.client.wechat.mini;

import cn.hutool.core.bean.BeanUtil;
import com.gls.athena.security.oauth2.client.wechat.mini.domain.AccessTokenRequest;
import com.gls.athena.security.oauth2.client.wechat.mini.domain.AccessTokenResponse;
import com.gls.athena.security.oauth2.client.wechat.mini.domain.Code2SessionRequest;
import com.gls.athena.security.oauth2.client.wechat.mini.domain.Code2SessionResponse;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

/**
 * 微信小程序 OAuth2 认证相关数据转换器。
 * 提供将 Spring Security OAuth2 请求/响应对象与微信小程序特定对象之间的相互转换功能。
 *
 * @author george
 */
@UtilityClass
public class WechatMiniConverter {

    /**
     * 将 OAuth2 授权码请求转换为微信小程序获取 access_token 的请求对象。
     *
     * @param request OAuth2 授权码授权请求对象
     * @return 微信小程序的 AccessTokenRequest 对象
     */
    public AccessTokenRequest convertToAccessTokenRequest(OAuth2AuthorizationCodeGrantRequest request) {
        return new AccessTokenRequest()
                .setAppId(request.getClientRegistration().getClientId())
                .setSecret(request.getClientRegistration().getClientSecret());
    }

    /**
     * 将 OAuth2 授权码请求转换为微信小程序 code2Session 请求对象。
     *
     * @param request OAuth2 授权码授权请求对象
     * @return 微信小程序的 Code2SessionRequest 对象
     */
    public Code2SessionRequest convertToCode2SessionRequest(OAuth2AuthorizationCodeGrantRequest request) {
        return new Code2SessionRequest()
                .setAppId(request.getClientRegistration().getClientId())
                .setSecret(request.getClientRegistration().getClientSecret())
                .setJsCode(request.getAuthorizationExchange().getAuthorizationResponse().getCode());
    }

    /**
     * 将微信小程序返回的 AccessTokenResponse 和 Code2SessionResponse 转换为标准的 OAuth2AccessTokenResponse。
     *
     * @param authorizationGrantRequest 授权请求对象
     * @param accessTokenResponse       微信小程序获取 access_token 的响应对象
     * @param code2SessionResponse      微信小程序 code2Session 响应对象
     * @return 标准的 OAuth2AccessTokenResponse 对象
     */
    public OAuth2AccessTokenResponse convertToAccessTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest,
                                                                  AccessTokenResponse accessTokenResponse,
                                                                  Code2SessionResponse code2SessionResponse) {
        // 构建标准的 OAuth2AccessTokenResponse 对象，包含 token 信息、token 类型、过期时间、作用域和附加参数
        return OAuth2AccessTokenResponse.withToken(accessTokenResponse.getAccessToken())
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .expiresIn(accessTokenResponse.getExpiresIn())
                .scopes(authorizationGrantRequest.getClientRegistration().getScopes())
                .additionalParameters(BeanUtil.beanToMap(code2SessionResponse))
                .build();
    }

    /**
     * 将附加参数和客户端注册信息转换为 OAuth2User 对象。
     *
     * @param additionalParameters 附加参数（如 openid、session_key 等）
     * @param clientRegistration   客户端注册信息
     * @return 构造好的 OAuth2User 对象
     */
    public OAuth2User convertToUser(Map<String, Object> additionalParameters, ClientRegistration clientRegistration) {
        // 使用默认 OAuth2User 实现，设置用户权限、属性和用户名属性名
        return new DefaultOAuth2User(
                AuthorityUtils.createAuthorityList("WECHAT_MINI_USER"),
                additionalParameters,
                clientRegistration.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName()
        );
    }

}
