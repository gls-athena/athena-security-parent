package com.gls.athena.security.oauth2.client.wechat.work;

import com.gls.athena.security.oauth2.client.wechat.support.RestTemplateUtil;
import com.gls.athena.security.oauth2.client.wechat.work.domain.*;
import com.gls.athena.starter.data.redis.support.RedisUtil;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * 企业微信客户端
 * 用于与企业微信API进行交互，获取访问令牌、用户ID和用户信息
 *
 * @author george
 */
public record WechatWorkClient(WechatWorkProperties properties) {

    /**
     * 获取企业微信访问令牌
     * 通过企业ID和应用密钥获取访问令牌，用于后续API调用的身份验证
     *
     * @param request 包含企业ID和应用密钥的请求参数
     * @return 访问令牌响应对象，包含令牌和过期时间等信息
     */
    public AccessTokenResponse getAccessToken(AccessTokenRequest request) {
        // 尝试从Redis缓存中获取访问令牌
        String cacheName = properties.getAccessTokenCacheName();
        AccessTokenResponse response = RedisUtil.getCacheValue(cacheName, request.getCorpId(), AccessTokenResponse.class);
        if (response != null) {
            return response;
        }

        // 构建获取访问令牌的API请求URL
        URI uri = UriComponentsBuilder.fromUriString(properties.getTokenUri())
                .queryParam("corpid", request.getCorpId())
                .queryParam("corpsecret", request.getCorpSecret())
                .build()
                .toUri();

        // 发起HTTP请求获取访问令牌
        response = RestTemplateUtil.get(uri, AccessTokenResponse.class);
        if (response != null) {
            // 将获取到的访问令牌存入Redis缓存
            RedisUtil.setCacheValue(cacheName, request.getCorpId(), response, response.getExpiresIn(), TimeUnit.SECONDS);
            return response;
        }
        return null;
    }

    /**
     * 根据授权码获取用户ID
     * 通过访问令牌和授权码获取企业微信用户的唯一标识
     *
     * @param request 包含访问令牌和授权码的请求参数
     * @return 用户ID响应对象，包含用户在企业微信中的唯一标识
     */
    public UserIdResponse getUserId(UserIdRequest request) {
        // 构建获取用户ID的API请求URL
        URI uri = UriComponentsBuilder.fromUriString(properties.getUserIdUri())
                .queryParam("access_token", request.getAccessToken())
                .queryParam("code", request.getCode())
                .build()
                .toUri();

        // 发起HTTP请求获取用户ID
        return RestTemplateUtil.get(uri, UserIdResponse.class);
    }

    /**
     * 获取用户详细信息
     * 通过访问令牌和用户ID获取企业微信用户的详细信息
     *
     * @param request 包含访问令牌和用户ID的请求参数
     * @return 用户信息响应对象，包含用户的姓名、部门等详细信息
     */
    public UserInfoResponse getUserInfo(UserInfoRequest request) {
        // 构建获取用户信息的API请求URL
        URI uri = UriComponentsBuilder.fromUriString(properties.getUserInfoUri())
                .queryParam("access_token", request.getAccessToken())
                .queryParam("userid", request.getUserId())
                .build()
                .toUri();

        // 发起HTTP请求获取用户详细信息
        return RestTemplateUtil.get(uri, UserInfoResponse.class);
    }
}
