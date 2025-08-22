package com.gls.athena.security.oauth2.client.delegate;

import com.gls.athena.security.oauth2.client.provider.SocialLoginProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 委托OAuth2访问令牌响应客户端实现类
 * 该类实现了OAuth2访问令牌响应客户端接口，用于处理OAuth2授权码模式下的令牌获取请求
 * 它首先尝试通过社交登录提供者获取令牌响应，如果失败则委托给默认的REST客户端处理
 *
 * @author george
 */
@Component
@RequiredArgsConstructor
public class DelegateOauth2AccessTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    private final RestClientAuthorizationCodeTokenResponseClient delegate = new RestClientAuthorizationCodeTokenResponseClient();

    private final SocialLoginProviderManager providerManager;

    /**
     * 获取OAuth2访问令牌响应
     * 首先尝试通过社交登录提供者获取令牌响应，如果返回null则委托给默认客户端处理
     *
     * @param authorizationGrantRequest OAuth2授权码授权请求对象，包含获取访问令牌所需的信息
     * @return OAuth2访问令牌响应对象，包含访问令牌及相关信息
     */
    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        // 尝试通过社交登录提供者获取令牌响应，如果为空则使用默认客户端获取
        return Optional.ofNullable(providerManager.getTokenResponse(authorizationGrantRequest))
                .orElseGet(() -> delegate.getTokenResponse(authorizationGrantRequest));
    }

}
