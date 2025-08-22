package com.gls.athena.security.oauth2.client.provider;

import com.gls.athena.security.oauth2.client.registration.SocialRegistration;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.function.Predicate;

/**
 * 社交登录提供者接口，用于定义社交登录相关的核心操作。
 * 实现类需要提供授权请求构建、令牌获取、用户信息加载等功能。
 *
 * @author george
 */
public interface SocialLoginProvider extends Predicate<String> {

    /**
     * 自定义OAuth2授权请求构建器。
     * 允许实现类根据客户端注册信息和HTTP请求自定义授权请求的构建过程。
     *
     * @param builder            OAuth2授权请求构建器，用于构建授权请求
     * @param clientRegistration 客户端注册信息，包含客户端ID、密钥、授权URI等配置
     * @param request            HTTP请求对象，可用于获取请求参数或会话信息
     */
    void customizeAuthorizationRequest(OAuth2AuthorizationRequest.Builder builder, ClientRegistration clientRegistration, HttpServletRequest request);

    /**
     * 获取访问令牌响应。
     * 通过授权码授予请求向授权服务器请求访问令牌。
     *
     * @param authorizationGrantRequest OAuth2授权码授予请求，包含授权码和重定向URI等信息
     * @return OAuth2访问令牌响应，包含访问令牌、刷新令牌及过期时间等信息
     */
    OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest);

    /**
     * 加载OAuth2用户信息。
     * 根据用户请求信息从资源服务器获取用户的基本信息。
     *
     * @param userRequest OAuth2用户请求信息，包含访问令牌和客户端注册信息
     * @return OAuth2用户对象，包含用户的属性信息
     */
    OAuth2User loadUser(OAuth2UserRequest userRequest);

    /**
     * 加载OpenID Connect用户信息。
     * 默认实现返回null，子类可重写以支持OIDC用户信息加载。
     *
     * @param userRequest OpenID Connect用户请求信息，包含ID令牌和用户信息端点配置
     * @return OpenID Connect用户对象，若不支持则返回null
     */
    default OidcUser loadOidcUser(OidcUserRequest userRequest) {
        return null;
    }

    /**
     * 获取客户端注册信息构建器。
     * 根据注册ID创建并自定义客户端注册信息构建器。
     *
     * @param registrationId 客户端注册ID，用于标识特定的客户端配置
     * @return 客户端注册信息构建器，可用于进一步构建ClientRegistration对象
     */
    default ClientRegistration.Builder getClientRegistrationBuilder(String registrationId) {
        SocialRegistration registration = new SocialRegistration(registrationId);
        customizeRegistration(registration);
        return registration.toBuilder();
    }

    /**
     * 自定义客户端注册信息。
     * 允许实现类对SocialRegistration对象进行自定义配置。
     *
     * @param registration 客户端注册信息对象，包含客户端的基本配置信息
     */
    void customizeRegistration(SocialRegistration registration);

}
