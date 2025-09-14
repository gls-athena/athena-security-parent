package com.gls.athena.security.oauth2.client.wechat.mp;

import com.gls.athena.security.oauth2.client.wechat.mp.domain.AccessTokenRequest;
import com.gls.athena.security.oauth2.client.wechat.mp.domain.AccessTokenResponse;
import com.gls.athena.security.oauth2.client.wechat.mp.domain.UserInfoRequest;
import com.gls.athena.security.oauth2.client.wechat.mp.domain.UserInfoResponse;
import com.gls.athena.security.oauth2.client.wechat.support.RestTemplateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * 微信公众号客户端
 * 用于处理微信公众号OAuth2认证相关的API调用
 *
 * @author george
 */
@RequiredArgsConstructor
public class WechatMpClient {

    private final WechatMpProperties properties;

    /**
     * 获取访问令牌
     * 通过微信公众号OAuth2接口获取用户的访问令牌
     *
     * @param request 访问令牌请求参数，包含appid、secret、code和grant_type
     * @return AccessTokenResponse 访问令牌响应对象
     */
    public AccessTokenResponse getAccessToken(AccessTokenRequest request) {
        // 构建获取访问令牌的请求URL
        URI uri = UriComponentsBuilder.fromUriString(properties.getTokenUri())
                .queryParam("appid", request.getAppid())
                .queryParam("secret", request.getSecret())
                .queryParam("code", request.getCode())
                .queryParam("grant_type", request.getGrantType())
                .build()
                .toUri();
        return RestTemplateUtil.get(uri, AccessTokenResponse.class);
    }

    /**
     * 获取用户信息
     * 通过微信公众号OAuth2接口获取用户基本信息
     *
     * @param request 用户信息请求参数，包含access_token、openid和lang
     * @return UserInfoResponse 用户信息响应对象
     */
    public UserInfoResponse getUserinfo(UserInfoRequest request) {
        // 构建获取用户信息的请求URL
        URI uri = UriComponentsBuilder.fromUriString(properties.getUserInfoUri())
                .queryParam("access_token", request.getAccessToken())
                .queryParam("openid", request.getOpenId())
                .queryParam("lang", request.getLang())
                .build()
                .toUri();
        return RestTemplateUtil.get(uri, UserInfoResponse.class);
    }

}
