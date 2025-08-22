package com.gls.athena.security.oauth2.client.wechat.open;

import com.gls.athena.security.oauth2.client.provider.SocialLoginProvider;
import com.gls.athena.security.oauth2.client.registration.SocialRegistration;
import com.gls.athena.security.oauth2.client.wechat.open.domain.AccessTokenRequest;
import com.gls.athena.security.oauth2.client.wechat.open.domain.AccessTokenResponse;
import com.gls.athena.security.oauth2.client.wechat.open.domain.UserinfoRequest;
import com.gls.athena.security.oauth2.client.wechat.open.domain.UserinfoResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

/**
 * 微信开放平台登录提供者实现类
 * 实现了SocialLoginProvider接口，用于处理微信扫码登录的OAuth2流程
 *
 * @author george
 */
public class WechatOpenLoginProvider implements SocialLoginProvider {

    private final WechatOpenProperties properties;
    private final WechatOpenClient client;

    /**
     * 构造函数，初始化微信开放平台登录提供者
     *
     * @param properties 微信开放平台的配置属性
     */
    public WechatOpenLoginProvider(WechatOpenProperties properties) {
        this.properties = properties;
        this.client = new WechatOpenClient(properties);
    }

    /**
     * 自定义OAuth2授权请求的URI构建过程，专门用于微信扫码登录场景
     * 该方法会修改标准的OAuth2授权URI，将其转换为微信扫码登录所需的格式
     *
     * @param builder            OAuth2授权请求构建器，用于构建和修改授权请求
     * @param clientRegistration 客户端注册信息，包含应用的认证配置
     * @param request            HTTP请求对象，包含当前的请求信息
     */
    @Override
    public void customizeAuthorizationRequest(OAuth2AuthorizationRequest.Builder builder, ClientRegistration clientRegistration, HttpServletRequest request) {
        // 自定义授权请求URI的构建逻辑，将标准OAuth2参数转换为微信扫码登录所需参数
        // 微信扫码登录需要将client_id参数替换为appid，并在URI末尾添加#wechat_redirect片段
        builder.authorizationRequestUri(uriBuilder -> {
            String uri = uriBuilder.build().getQuery();
            uri = uri.replace("client_id", "appid");
            return uriBuilder.replaceQuery(uri).fragment("wechat_redirect").build();
        });
    }

    /**
     * 获取OAuth2访问令牌响应
     *
     * @param authorizationGrantRequest 授权码授权请求对象，包含获取访问令牌所需的信息
     * @return OAuth2访问令牌响应对象
     */
    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        // 将授权码授权请求转换为访问令牌请求
        AccessTokenRequest accessTokenRequest = WechatOpenConverter.convertToAccessTokenRequest(authorizationGrantRequest);
        // 通过客户端获取访问令牌响应
        AccessTokenResponse accessTokenResponse = client.getAccessToken(accessTokenRequest);
        // 将访问令牌响应转换为OAuth2标准格式的令牌响应
        return WechatOpenConverter.convertToAccessTokenResponse(authorizationGrantRequest, accessTokenResponse);
    }

    /**
     * 加载OAuth2用户信息
     *
     * @param userRequest OAuth2用户请求对象，包含认证相关信息
     * @return OAuth2User 用户信息对象
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        // 转换用户信息请求
        UserinfoRequest userinfoRequest = WechatOpenConverter.convertToUserinfoRequest(userRequest);
        // 获取用户信息响应
        UserinfoResponse userinfoResponse = client.getUserinfo(userinfoRequest);
        // 转换并返回用户对象
        return WechatOpenConverter.convertToUser(userinfoResponse, userRequest.getClientRegistration());
    }

    /**
     * 自定义社交登录注册配置
     *
     * @param registration 社交注册配置对象，用于设置各种OAuth2相关的配置参数
     */
    @Override
    public void customizeRegistration(SocialRegistration registration) {
        // 设置基础认证配置信息
        registration.setProviderId(properties.getProviderId());
        registration.setProviderName(properties.getProviderName());
        registration.setAuthorizationUri(properties.getAuthorizationUri());
        registration.setTokenUri(properties.getTokenUri());
        registration.setUserInfoUri(properties.getUserInfoUri());
        registration.setUserNameAttributeName(properties.getUserNameAttributeName());
        registration.setScopes(properties.getScopes());

        // 设置元数据信息
        registration.setMetadata(Map.of("lang", properties.getLang()));
    }

    /**
     * 测试指定的提供者ID是否与当前属性中的提供者ID相等
     *
     * @param s 要测试的提供者ID字符串
     * @return 如果指定的提供者ID与当前属性中的提供者ID相等则返回true，否则返回false
     */
    @Override
    public boolean test(String s) {
        return properties.getProviderId().equals(s);
    }

}
