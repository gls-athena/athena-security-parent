package com.gls.athena.security.oauth2.client.wechat.mini;

import com.gls.athena.security.oauth2.client.wechat.mini.domain.AccessTokenRequest;
import com.gls.athena.security.oauth2.client.wechat.mini.domain.AccessTokenResponse;
import com.gls.athena.security.oauth2.client.wechat.mini.domain.Code2SessionRequest;
import com.gls.athena.security.oauth2.client.wechat.mini.domain.Code2SessionResponse;
import com.gls.athena.security.oauth2.client.wechat.support.RestTemplateUtil;
import com.gls.athena.starter.data.redis.support.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * 微信小程序客户端
 * 提供微信小程序相关的API调用功能
 *
 * @author george
 */
@RequiredArgsConstructor
public class WechatMiniClient {

    private final WechatMiniProperties properties;

    /**
     * 获取微信小程序访问令牌
     * 通过appid、secret等参数向微信服务器请求访问令牌，并将结果缓存以提升性能
     *
     * @param request 访问令牌请求参数对象，包含appid、secret和授权类型
     * @return AccessTokenResponse 访问令牌响应对象，包含access_token等信息；若请求失败则返回null
     */
    public AccessTokenResponse getAccessToken(AccessTokenRequest request) {
        // 从缓存中获取访问令牌，如果存在则直接返回
        String cacheName = properties.getAccessTokenCacheName();
        AccessTokenResponse response = RedisUtil.getCacheValue(cacheName, request.getAppId(), AccessTokenResponse.class);
        if (response != null) {
            return response;
        }
        // 构建请求URI，将请求参数添加到查询字符串中
        URI uri = UriComponentsBuilder.fromUriString(properties.getTokenUri())
                .queryParam("appid", request.getAppId())
                .queryParam("secret", request.getSecret())
                .queryParam("grant_type", request.getGrantType())
                .build()
                .toUri();
        // 发送GET请求并返回响应结果
        response = RestTemplateUtil.get(uri, AccessTokenResponse.class);
        // 如果获取到响应数据，则将其缓存并返回
        if (response != null) {
            RedisUtil.setCacheValue(cacheName, request.getAppId(), response, response.getExpiresIn(), TimeUnit.SECONDS);
            return response;
        }
        return null;
    }

    /**
     * 微信小程序登录凭证校验
     * 通过js_code向微信服务器换取用户的session_key和openid
     *
     * @param request 登录凭证校验请求参数对象，包含appid、secret、js_code和授权类型
     * @return Code2SessionResponse 登录凭证校验响应对象，包含openid、session_key等用户信息
     */
    public Code2SessionResponse code2Session(Code2SessionRequest request) {
        // 构建请求URI，将请求参数添加到查询字符串中
        URI uri = UriComponentsBuilder.fromUriString(properties.getCode2SessionUri())
                .queryParam("appid", request.getAppId())
                .queryParam("secret", request.getSecret())
                .queryParam("js_code", request.getJsCode())
                .queryParam("grant_type", request.getGrantType())
                .build()
                .toUri();
        // 发送GET请求并返回响应结果
        return RestTemplateUtil.get(uri, Code2SessionResponse.class);
    }
}
