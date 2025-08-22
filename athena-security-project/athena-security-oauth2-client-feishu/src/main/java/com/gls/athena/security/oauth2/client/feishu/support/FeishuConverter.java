package com.gls.athena.security.oauth2.client.feishu.support;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.gls.athena.security.oauth2.client.feishu.domain.FeishuAppAccessTokenRequest;
import com.gls.athena.security.oauth2.client.feishu.domain.FeishuUserAccessTokenRequest;
import com.gls.athena.security.oauth2.client.feishu.domain.FeishuUserAccessTokenResponse;
import com.gls.athena.security.oauth2.client.feishu.domain.FeishuUserInfoResponse;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

/**
 * 飞书 OAuth2 相关对象转换器，用于将 Spring Security OAuth2 对象与飞书 API 对象之间进行转换。
 *
 * @author george
 */
@UtilityClass
public class FeishuConverter {

    /**
     * 将 ClientRegistration 转换为获取飞书应用访问令牌的请求对象。
     *
     * @param clientRegistration 客户端注册信息，包含 appId 和 appSecret
     * @return 飞书应用访问令牌请求对象
     */
    public FeishuAppAccessTokenRequest convertToAppAccessTokenRequest(ClientRegistration clientRegistration) {
        return new FeishuAppAccessTokenRequest()
                .setAppId(clientRegistration.getClientId())
                .setAppSecret(clientRegistration.getClientSecret());
    }

    /**
     * 将授权码授权请求转换为获取飞书用户访问令牌的请求对象。
     *
     * @param authorizationGrantRequest 授权码授权请求对象，包含授权码和授权类型
     * @return 飞书用户访问令牌请求对象
     */
    public FeishuUserAccessTokenRequest convertToUserAccessTokenRequest(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        return new FeishuUserAccessTokenRequest()
                .setCode(authorizationGrantRequest.getAuthorizationExchange().getAuthorizationResponse().getCode())
                .setGrantType(authorizationGrantRequest.getGrantType().getValue());
    }

    /**
     * 将飞书用户访问令牌响应转换为 Spring Security 的 OAuth2 访问令牌响应对象。
     *
     * @param userAccessTokenResponse 飞书用户访问令牌响应对象
     * @return Spring Security 的 OAuth2 访问令牌响应对象
     */
    public OAuth2AccessTokenResponse convertToAccessTokenResponse(FeishuUserAccessTokenResponse userAccessTokenResponse) {
        return OAuth2AccessTokenResponse.withToken(userAccessTokenResponse.getAccessToken())
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .expiresIn(userAccessTokenResponse.getExpiresIn())
                .refreshToken(userAccessTokenResponse.getRefreshToken())
                .scopes(convertToScopes(userAccessTokenResponse.getScope()))
                .additionalParameters(convertToAdditionalParameters(userAccessTokenResponse))
                .build();
    }

    /**
     * 将字符串形式的 scope 转换为 Set<String> 格式。
     *
     * @param scope 以空格分隔的权限范围字符串
     * @return 权限范围集合
     */
    private Set<String> convertToScopes(String scope) {
        // 如果 scope 为空，则返回空集合；否则按空格分割并构造 HashSet
        return StrUtil.isBlank(scope) ? Collections.emptySet() : new HashSet<>(StrUtil.split(scope, ' '));
    }

    /**
     * 构造额外参数映射表，当前仅包含 refreshExpiresIn 字段。
     *
     * @param userAccessTokenResponse 飞书用户访问令牌响应对象
     * @return 包含额外参数的映射表
     */
    private Map<String, Object> convertToAdditionalParameters(FeishuUserAccessTokenResponse userAccessTokenResponse) {
        Map<String, Object> additionalParameters = new HashMap<>();
        additionalParameters.put("refreshExpiresIn", userAccessTokenResponse.getRefreshExpiresIn());
        return additionalParameters;
    }

    /**
     * 将飞书用户信息响应转换为 Spring Security 的 OAuth2User 对象。
     *
     * @param userInfoResponse   飞书用户信息响应对象
     * @param clientRegistration 客户端注册信息，用于获取用户名属性名
     * @return Spring Security 的 OAuth2User 对象
     */
    public OAuth2User convertToUser(FeishuUserInfoResponse userInfoResponse, ClientRegistration clientRegistration) {
        return new DefaultOAuth2User(
                AuthorityUtils.createAuthorityList("FEISHU_USER"),
                BeanUtil.beanToMap(userInfoResponse),
                clientRegistration.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName());
    }
}
