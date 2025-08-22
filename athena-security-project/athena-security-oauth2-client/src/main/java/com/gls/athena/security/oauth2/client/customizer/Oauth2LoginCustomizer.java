package com.gls.athena.security.oauth2.client.customizer;

import com.gls.athena.security.common.config.SecurityCommonProperties;
import com.gls.athena.security.oauth2.client.delegate.DelegateOauth2AccessTokenResponseClient;
import com.gls.athena.security.oauth2.client.delegate.DelegateOauth2AuthorizationRequestResolver;
import com.gls.athena.security.oauth2.client.delegate.DelegateOauth2UserService;
import jakarta.annotation.Resource;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.stereotype.Component;

/**
 * OAuth2登录配置定制器，用于自定义Spring Security的OAuth2登录流程。
 * <p>
 * 该类实现了{@link Customizer}接口，允许对{@link OAuth2LoginConfigurer}进行细粒度配置，
 * 包括登录页面、授权请求解析器、令牌端点客户端、用户信息服务等。
 * </p>
 *
 * @author george
 */
@Component
public class Oauth2LoginCustomizer implements Customizer<OAuth2LoginConfigurer<HttpSecurity>> {
    /**
     * 登录核心配置属性
     */
    @Resource
    private SecurityCommonProperties securityCommonProperties;
    /**
     * 注入授权请求解析器
     */
    @Resource
    private DelegateOauth2AuthorizationRequestResolver oauth2AuthorizationRequestResolver;
    /**
     * 注入访问令牌响应客户端
     * 用于处理OAuth2授权码授予请求的访问令牌响应
     */
    @Resource
    private DelegateOauth2AccessTokenResponseClient oauth2AccessTokenResponseClient;
    /**
     * 委托OAuth2用户服务，用于加载用户信息。
     */
    @Resource
    private DelegateOauth2UserService oauth2UserService;

    /**
     * 对OAuth2登录配置进行自定义设置。
     * <p>
     * 通过此方法可以设置登录页面、授权端点、重定向端点、令牌端点以及用户信息端点的相关配置。
     * </p>
     *
     * @param configurer OAuth2登录配置器，用于配置OAuth2登录相关功能
     */
    @Override
    public void customize(OAuth2LoginConfigurer<HttpSecurity> configurer) {
        // 设置自定义登录页面
        configurer.loginPage(securityCommonProperties.getLoginPage());

        // 配置授权端点
        configurer.authorizationEndpoint(this::customizeAuthorizationEndpoint);

        // 配置重定向端点
        configurer.redirectionEndpoint(this::customizeRedirectionEndpoint);

        // 配置令牌端点
        configurer.tokenEndpoint(this::customizeTokenEndpoint);

        // 配置用户信息端点
        configurer.userInfoEndpoint(this::customizeUserInfoEndpoint);
    }

    /**
     * 自定义授权端点配置。
     * <p>
     * 此方法用于设置自定义的授权请求解析器，以支持更灵活的授权请求处理逻辑。
     * </p>
     *
     * @param config 授权端点配置对象
     */
    private void customizeAuthorizationEndpoint(OAuth2LoginConfigurer<HttpSecurity>.AuthorizationEndpointConfig config) {
        // 自定义授权请求解析器
        config.authorizationRequestResolver(oauth2AuthorizationRequestResolver);
    }

    /**
     * 自定义重定向端点配置
     * <p>
     * 当前方法为空实现，可根据需要扩展重定向端点的行为。
     * </p>
     *
     * @param config 重定向端点配置对象，用于配置OAuth2登录的重定向相关参数
     */
    private void customizeRedirectionEndpoint(OAuth2LoginConfigurer<HttpSecurity>.RedirectionEndpointConfig config) {
    }

    /**
     * 自定义令牌端点配置。
     * <p>
     * 使用自定义的访问令牌响应客户端来处理从授权服务器获取访问令牌的响应。
     * </p>
     *
     * @param config 令牌端点配置对象
     */
    private void customizeTokenEndpoint(OAuth2LoginConfigurer<HttpSecurity>.TokenEndpointConfig config) {
        // 设置自定义的访问令牌响应客户端
        config.accessTokenResponseClient(oauth2AccessTokenResponseClient);
    }

    /**
     * 自定义用户信息端点配置。
     * <p>
     * 配置OAuth2用户服务与OpenID Connect用户服务，用于加载认证后的用户信息。
     * </p>
     *
     * @param config 用户信息端点配置对象
     */
    private void customizeUserInfoEndpoint(OAuth2LoginConfigurer<HttpSecurity>.UserInfoEndpointConfig config) {
        // 配置OAuth2用户服务，用于加载用户信息
        config.userService(oauth2UserService::loadUser);
        // 配置OpenID Connect用户服务，用于加载OIDC用户信息
        config.oidcUserService(oauth2UserService::loadOidcUser);
    }

}
