package com.gls.athena.security.oauth2.client.wechat.open;

import cn.hutool.core.bean.BeanUtil;
import com.gls.athena.security.oauth2.client.wechat.open.domain.AccessTokenRequest;
import com.gls.athena.security.oauth2.client.wechat.open.domain.AccessTokenResponse;
import com.gls.athena.security.oauth2.client.wechat.open.domain.UserinfoRequest;
import com.gls.athena.security.oauth2.client.wechat.open.domain.UserinfoResponse;
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
 * 微信开放平台OAuth2相关对象转换器
 * <p>
 * 用于将Spring Security OAuth2标准对象与微信开放平台特定对象之间进行相互转换。
 * 包括访问令牌请求/响应、用户信息请求/响应等对象的转换。
 *
 * @author george
 */
@UtilityClass
public class WechatOpenConverter {

    /**
     * 将OAuth2授权码授权请求转换为访问令牌请求
     *
     * @param authorizationGrantRequest OAuth2授权码授权请求对象，包含客户端注册信息和授权交换信息
     * @return AccessTokenRequest 访问令牌请求对象，包含appid、secret和授权码
     */
    public AccessTokenRequest convertToAccessTokenRequest(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        // 从授权码授权请求中提取客户端信息和授权码，构建访问令牌请求
        return new AccessTokenRequest()
                .setAppid(authorizationGrantRequest.getClientRegistration().getClientId())
                .setSecret(authorizationGrantRequest.getClientRegistration().getClientSecret())
                .setCode(authorizationGrantRequest.getAuthorizationExchange().getAuthorizationResponse().getCode());
    }

    /**
     * 将微信开放平台访问令牌响应转换为OAuth2标准访问令牌响应
     *
     * @param authorizationGrantRequest OAuth2授权码授权请求对象，包含客户端注册信息和授权交换信息
     * @param accessTokenResponse       微信开放平台访问令牌响应对象
     * @return OAuth2AccessTokenResponse OAuth2标准访问令牌响应对象
     */
    public OAuth2AccessTokenResponse convertToAccessTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest,
                                                                  AccessTokenResponse accessTokenResponse) {
        // 构建OAuth2标准访问令牌响应对象
        return OAuth2AccessTokenResponse.withToken(accessTokenResponse.getAccessToken())
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .expiresIn(accessTokenResponse.getExpiresIn())
                .scopes(authorizationGrantRequest.getClientRegistration().getScopes())
                .refreshToken(accessTokenResponse.getRefreshToken())
                // 添加微信特有的额外参数
                .additionalParameters(
                        Map.of(
                                "openid", accessTokenResponse.getOpenId(),
                                "scope", accessTokenResponse.getScope(),
                                "unionid", accessTokenResponse.getUnionId()
                        )
                )
                .build();
    }

    /**
     * 将OAuth2用户请求转换为微信开放平台用户信息请求
     *
     * @param userRequest OAuth2用户请求对象，包含访问令牌和附加参数
     * @return UserinfoRequest 微信开放平台用户信息请求对象
     */
    public UserinfoRequest convertToUserinfoRequest(OAuth2UserRequest userRequest) {
        // 构造微信用户信息请求对象，设置访问令牌、用户OpenID和语言参数
        return new UserinfoRequest()
                .setAccessToken(userRequest.getAccessToken().getTokenValue())
                .setOpenId(userRequest.getAdditionalParameters().get("openid").toString())
                .setLang(userRequest.getClientRegistration().getProviderDetails().getConfigurationMetadata()
                        .getOrDefault("lang", "zh_CN").toString());
    }

    /**
     * 将微信开放平台用户信息响应转换为OAuth2标准用户对象
     *
     * @param userinfoResponse   微信开放平台用户信息响应对象
     * @param clientRegistration 客户端注册信息，用于获取用户属性名称
     * @return OAuth2User OAuth2标准用户对象
     */
    public OAuth2User convertToUser(UserinfoResponse userinfoResponse, ClientRegistration clientRegistration) {
        // 创建默认OAuth2用户对象，包含用户权限、属性映射和用户名属性名
        return new DefaultOAuth2User(
                AuthorityUtils.createAuthorityList("WECHAT_OPEN_USER"),
                BeanUtil.beanToMap(userinfoResponse),
                clientRegistration.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName()
        );
    }

}
