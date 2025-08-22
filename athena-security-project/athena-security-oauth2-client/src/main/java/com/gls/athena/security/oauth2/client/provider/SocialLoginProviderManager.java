package com.gls.athena.security.oauth2.client.provider;

import cn.hutool.core.map.MapUtil;
import com.gls.athena.security.oauth2.client.config.Oauth2ClientConstants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 社交登录提供者管理器，用于根据客户端注册信息选择合适的社交登录提供者，
 * 并执行自定义授权请求、获取访问令牌和加载用户信息等操作。
 *
 * @author george
 */
@Component
@RequiredArgsConstructor
public class SocialLoginProviderManager {

    private final List<SocialLoginProvider> providers;

    /**
     * 自定义OAuth2授权请求构建过程。
     * 根据客户端注册信息查找对应的社交登录提供者，并委托其完成自定义逻辑。
     *
     * @param builder            OAuth2授权请求构建器
     * @param clientRegistration 客户端注册信息
     * @param request            HTTP请求对象
     */
    public void customizeAuthorizationRequest(OAuth2AuthorizationRequest.Builder builder, ClientRegistration clientRegistration, HttpServletRequest request) {
        SocialLoginProvider provider = getProviderByClientRegistration(clientRegistration);
        if (provider == null) {
            return;
        }
        provider.customizeAuthorizationRequest(builder, clientRegistration, request);
    }

    /**
     * 获取访问令牌响应。
     * 根据客户端注册信息查找对应的社交登录提供者，并委托其获取访问令牌。
     *
     * @param authorizationGrantRequest 授权码授权请求对象
     * @return 访问令牌响应对象，如果未找到匹配的提供者则返回null
     */
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        SocialLoginProvider provider = getProviderByClientRegistration(authorizationGrantRequest.getClientRegistration());
        if (provider == null) {
            return null;
        }
        return provider.getTokenResponse(authorizationGrantRequest);
    }

    /**
     * 加载OAuth2用户信息。
     * 根据客户端注册信息查找对应的社交登录提供者，并委托其加载用户信息。
     *
     * @param userRequest OAuth2用户请求对象
     * @return OAuth2用户对象，如果未找到匹配的提供者则返回null
     */
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        SocialLoginProvider provider = getProviderByClientRegistration(userRequest.getClientRegistration());
        if (provider == null) {
            return null;
        }
        return provider.loadUser(userRequest);
    }

    /**
     * 加载OIDC用户信息。
     * 根据客户端注册信息查找对应的社交登录提供者，并委托其加载OIDC用户信息。
     *
     * @param userRequest OIDC用户请求对象
     * @return OIDC用户对象，如果未找到匹配的提供者则返回null
     */
    public OidcUser loadOidcUser(OidcUserRequest userRequest) {
        SocialLoginProvider provider = getProviderByClientRegistration(userRequest.getClientRegistration());
        if (provider == null) {
            return null;
        }
        return provider.loadOidcUser(userRequest);
    }

    /**
     * 根据提供者ID查找匹配的社交登录提供者实例。
     *
     * @param providerId 提供者ID
     * @return 匹配的社交登录提供者Optional包装对象
     */
    private SocialLoginProvider getProviderById(String providerId) {
        return providers.stream()
                .filter(provider -> provider.test(providerId))
                .findFirst()
                .orElse(null);
    }

    /**
     * 从客户端注册信息中提取提供者ID。
     *
     * @param clientRegistration 客户端注册信息
     * @return 提供者ID的Optional包装对象
     */
    private SocialLoginProvider getProviderByClientRegistration(ClientRegistration clientRegistration) {
        return Optional.ofNullable(clientRegistration)
                .map(ClientRegistration::getProviderDetails)
                .map(ClientRegistration.ProviderDetails::getConfigurationMetadata)
                .map(metadata -> MapUtil.getStr(metadata, Oauth2ClientConstants.PROVIDER_ID))
                .map(this::getProviderById)
                .orElse(null);
    }

    /**
     * 获取客户端注册构建器
     *
     * @param registrationId 注册ID，用于标识特定的客户端注册信息
     * @param providerId     提供商ID，用于查找对应的提供商配置
     * @return 返回对应提供商的客户端注册构建器，如果找不到对应的提供商则返回null
     */
    public ClientRegistration.Builder getClientRegistrationBuilder(String registrationId, String providerId) {
        SocialLoginProvider provider = getProviderById(providerId);
        if (provider == null) {
            return null;
        }
        return provider.getClientRegistrationBuilder(registrationId);
    }

}
