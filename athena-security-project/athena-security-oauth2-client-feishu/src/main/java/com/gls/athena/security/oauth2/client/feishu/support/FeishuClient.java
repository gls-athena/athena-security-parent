package com.gls.athena.security.oauth2.client.feishu.support;

import com.gls.athena.security.oauth2.client.feishu.config.Oauth2FeishuProperties;
import com.gls.athena.security.oauth2.client.feishu.domain.*;
import com.gls.athena.starter.data.redis.support.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * 飞书助手
 *
 * @param properties 飞书应用访问令牌缓存名称
 * @author george
 */
@RequiredArgsConstructor
public class FeishuClient {

    private final Oauth2FeishuProperties properties;

    /**
     * 获取应用访问令牌。
     * 首先尝试从Redis缓存中获取，如果缓存中不存在，则调用飞书接口获取并缓存结果。
     *
     * @param request 应用访问令牌请求参数，包含应用ID和密钥等信息
     * @return 应用访问令牌响应对象，包含访问令牌和过期时间等信息；如果获取失败则返回null
     */
    public FeishuAppAccessTokenResponse getAppAccessToken(FeishuAppAccessTokenRequest request) {
        String cacheName = properties.getAppAccessTokenCacheName();
        // 从Redis缓存中获取应用访问令牌
        FeishuAppAccessTokenResponse response = RedisUtil.getCacheValue(cacheName, request.getAppId(), FeishuAppAccessTokenResponse.class);
        if (response != null) {
            return response;
        }
        // 请求飞书接口
        RestTemplate restTemplate = new RestTemplate();
        // 构造请求实体
        RequestEntity<FeishuAppAccessTokenRequest> requestEntity = RequestEntity
                .post(URI.create(properties.getAppAccessTokenUri()))
                .header("Content-Type", "application/json; charset=UTF-8")
                .body(request);
        // 发送请求并获取响应
        response = restTemplate.exchange(requestEntity, FeishuAppAccessTokenResponse.class).getBody();
        // 缓存应用访问令牌
        if (response != null) {
            RedisUtil.setCacheValue(cacheName, request.getAppId(), response, response.getExpire(), TimeUnit.SECONDS);
            return response;
        }
        // 返回空
        return null;
    }

    /**
     * 获取用户访问令牌。
     * 使用应用访问令牌调用飞书接口，换取用户的访问令牌。
     *
     * @param request        用户访问令牌请求参数，包含授权码等信息
     * @param appAccessToken 应用访问令牌，用于身份验证
     * @return 用户访问令牌响应对象，包含用户访问令牌和相关信息；如果获取失败则返回null
     */
    public FeishuUserAccessTokenResponse getUserAccessToken(FeishuUserAccessTokenRequest request, String appAccessToken) {
        // 请求飞书接口
        RestTemplate restTemplate = new RestTemplate();
        // 构造请求实体
        RequestEntity<FeishuUserAccessTokenRequest> requestEntity = RequestEntity
                .post(URI.create(properties.getTokenUri()))
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("Authorization", "Bearer " + appAccessToken)
                .body(request);
        // 发送请求并解析响应
        FeishuResponse<FeishuUserAccessTokenResponse> response = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<FeishuResponse<FeishuUserAccessTokenResponse>>() {
        }).getBody();
        if (response != null) {
            return response.getData();
        }
        return null;
    }

    /**
     * 获取用户信息。
     * 使用用户访问令牌调用飞书接口，获取用户的基本信息。
     *
     * @param userAccessToken 用户访问令牌，用于身份验证
     * @return 用户信息响应对象，包含用户名、头像等信息；如果获取失败则返回null
     */
    public FeishuUserInfoResponse getUserInfo(String userAccessToken) {
        // 请求飞书接口
        RestTemplate restTemplate = new RestTemplate();
        // 构造请求实体
        RequestEntity<?> requestEntity = RequestEntity
                .get(URI.create(properties.getUserInfoUri()))
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("Authorization", "Bearer " + userAccessToken)
                .build();
        // 发送请求并解析响应
        FeishuResponse<FeishuUserInfoResponse> response = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<FeishuResponse<FeishuUserInfoResponse>>() {
        }).getBody();
        if (response != null) {
            return response.getData();
        }
        return null;
    }

}
