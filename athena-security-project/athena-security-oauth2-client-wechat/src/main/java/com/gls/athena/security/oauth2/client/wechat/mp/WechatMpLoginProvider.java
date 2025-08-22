package com.gls.athena.security.oauth2.client.wechat.mp;

import com.gls.athena.security.oauth2.client.provider.SocialLoginProvider;
import com.gls.athena.security.oauth2.client.registration.SocialRegistration;
import com.gls.athena.security.oauth2.client.wechat.mp.domain.AccessTokenRequest;
import com.gls.athena.security.oauth2.client.wechat.mp.domain.AccessTokenResponse;
import com.gls.athena.security.oauth2.client.wechat.mp.domain.UserInfoRequest;
import com.gls.athena.security.oauth2.client.wechat.mp.domain.UserInfoResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

/**
 * 微信公众平台社交登录提供者实现类
 * <p>
 * 该类实现了 {@link SocialLoginProvider} 接口，用于处理微信公众号的 OAuth2 授权流程，
 * 包括授权请求的定制、访问令牌的获取、用户信息的加载以及客户端注册信息的自定义配置。
 * </p>
 *
 * @author george
 */
public class WechatMpLoginProvider implements SocialLoginProvider {

    private final WechatMpProperties properties;
    private final WechatMpClient client;

    /**
     * 构造函数，初始化微信公众号登录提供者
     *
     * @param properties 微信公众号相关配置属性对象
     */
    public WechatMpLoginProvider(WechatMpProperties properties) {
        this.properties = properties;
        this.client = new WechatMpClient(properties);
    }

    /**
     * 自定义OAuth2授权请求的配置方法
     * <p>
     * 该方法用于修改微信OAuth2授权请求的URI格式，将标准的OAuth2参数转换为微信特定的参数格式。
     * 微信要求使用 appid 替代 client_id，并在 URI 片段中添加 #wechat_redirect。
     *
     * @param builder            OAuth2授权请求构建器，用于构建和修改授权请求
     * @param clientRegistration 客户端注册信息，包含客户端的相关配置
     * @param request            HTTP请求对象，包含当前的请求信息
     */
    @Override
    public void customizeAuthorizationRequest(OAuth2AuthorizationRequest.Builder builder, ClientRegistration clientRegistration, HttpServletRequest request) {
        // https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect
        // 自定义授权请求URI，将标准OAuth2参数转换为微信所需的参数格式
        builder.authorizationRequestUri(uriBuilder -> {
            String uri = uriBuilder.build().getQuery();
            uri = uri.replace("client_id", "appid");
            return uriBuilder.replaceQuery(uri).fragment("wechat_redirect").build();
        });
    }

    /**
     * 获取访问令牌响应
     * <p>
     * 根据授权码请求，调用微信客户端获取访问令牌，并将其转换为标准的 OAuth2 访问令牌响应对象。
     *
     * @param authorizationGrantRequest 包含授权码的授权请求对象
     * @return OAuth2标准的访问令牌响应对象
     */
    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        AccessTokenRequest request = WechatMpConverter.convertToAccessTokenRequest(authorizationGrantRequest);
        // 调用微信小程序客户端获取访问令牌
        AccessTokenResponse response = client.getAccessToken(request);
        return WechatMpConverter.convertToAccessTokenResponse(authorizationGrantRequest, response);
    }

    /**
     * 加载OAuth2用户信息
     * <p>
     * 使用访问令牌请求用户信息，并将其转换为标准的 OAuth2 用户对象。
     *
     * @param userRequest 包含访问令牌的用户请求对象
     * @return OAuth2标准的用户对象
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        UserInfoRequest request = WechatMpConverter.convertToUserinfoRequest(userRequest);
        UserInfoResponse response = client.getUserinfo(request);
        return WechatMpConverter.convertToUser(response, userRequest.getClientRegistration());
    }

    /**
     * 自定义社交登录客户端注册信息
     * <p>
     * 将微信公众号相关的配置信息填充到客户端注册对象中，包括授权地址、令牌地址、用户信息地址等。
     *
     * @param registration 社交登录客户端注册对象
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

        registration.setMetadata(Map.of("lang", properties.getLang()));

    }

    /**
     * 判断是否支持指定的提供商ID
     * <p>
     * 用于判断当前提供者是否与传入的提供商ID匹配。
     *
     * @param s 提供商ID字符串
     * @return 如果匹配返回 true，否则返回 false
     */
    @Override
    public boolean test(String s) {
        return properties.getProviderId().equals(s);
    }
}
