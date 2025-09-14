package com.gls.athena.security.oauth2.client.wechat.open;

import com.gls.athena.security.oauth2.client.wechat.open.domain.AccessTokenRequest;
import com.gls.athena.security.oauth2.client.wechat.open.domain.AccessTokenResponse;
import com.gls.athena.security.oauth2.client.wechat.open.domain.UserinfoRequest;
import com.gls.athena.security.oauth2.client.wechat.open.domain.UserinfoResponse;
import com.gls.athena.security.oauth2.client.wechat.support.RestTemplateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * 微信开放平台客户端，用于与微信OAuth2接口交互，获取访问令牌和用户信息。
 *
 * @author george
 */
@RequiredArgsConstructor
public class WechatOpenClient {

    private final WechatOpenProperties properties;

    /**
     * 获取访问令牌
     *
     * @param request 包含获取令牌所需参数的请求对象，包括appid、secret、code和grant_type
     * @return AccessTokenResponse 访问令牌响应对象，包含访问令牌及相关信息
     */
    public AccessTokenResponse getAccessToken(AccessTokenRequest request) {
        // 构建包含查询参数的URI
        URI uri = UriComponentsBuilder.fromUriString(properties.getTokenUri())
                .queryParam("appid", request.getAppid())
                .queryParam("secret", request.getSecret())
                .queryParam("code", request.getCode())
                .queryParam("grant_type", request.getGrantType())
                .build()
                .toUri();

        // 发起GET请求并返回AccessTokenResponse对象
        return RestTemplateUtil.get(uri, AccessTokenResponse.class);
    }

    /**
     * 获取用户信息
     *
     * @param request 包含访问令牌、用户openid和语言信息的请求对象
     * @return 用户信息响应对象
     */
    public UserinfoResponse getUserinfo(UserinfoRequest request) {
        // 构建包含查询参数的URI
        URI uri = UriComponentsBuilder.fromUriString(properties.getUserInfoUri())
                .queryParam("access_token", request.getAccessToken())
                .queryParam("openid", request.getOpenId())
                .queryParam("lang", request.getLang())
                .build()
                .toUri();

        // 发起GET请求并返回UserinfoResponse对象
        return RestTemplateUtil.get(uri, UserinfoResponse.class);
    }

}
