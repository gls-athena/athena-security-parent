package com.gls.athena.security.oauth2.authorization.server.redis.service;

import com.gls.athena.security.oauth2.authorization.server.redis.converter.Oauth2AuthorizationConsentConverter;
import com.gls.athena.security.oauth2.authorization.server.redis.domain.Oauth2AuthorizationConsent;
import com.gls.athena.starter.data.redis.support.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;

/**
 * 基于Redis的OAuth2授权同意信息管理服务实现类。
 * <p>
 * 该类实现了{@link OAuth2AuthorizationConsentService}接口，用于在Redis中存储、删除和查询OAuth2授权同意信息。
 * 使用{@link Oauth2AuthorizationConsentConverter}进行实体与领域模型之间的转换。
 * </p>
 *
 * @author george
 */
@RequiredArgsConstructor
public class RedisOauth2AuthorizationConsentServiceImpl implements OAuth2AuthorizationConsentService {

    private final Oauth2AuthorizationConsentConverter converter;

    private final String cacheName = "oauth2:authorization:consent";

    /**
     * 保存OAuth2授权同意信息到Redis缓存中。
     *
     * @param authorizationConsent OAuth2授权同意信息对象，包含客户端ID和主体名称等信息
     */
    @Override
    public void save(OAuth2AuthorizationConsent authorizationConsent) {
        // 将OAuth2授权同意信息转换为持久化实体对象
        Oauth2AuthorizationConsent consent = converter.convert(authorizationConsent);
        if (consent == null) {
            return;
        }
        // 构造Redis缓存中的行键
        String key = getRowId(consent.getRegisteredClientId(), consent.getPrincipalName());
        // 存储到Redis缓存表中
        RedisUtil.setCacheTableRow(cacheName, key, consent);
    }

    /**
     * 根据客户端ID和主体名称构造Redis缓存中的行键。
     *
     * @param registeredClientId 客户端ID
     * @param principalName      主体名称（通常是用户名）
     * @return 行键字符串，格式为"客户端ID:主体名称"
     */
    private String getRowId(String registeredClientId, String principalName) {
        return registeredClientId + ":" + principalName;
    }

    /**
     * 从Redis缓存中删除指定的OAuth2授权同意信息。
     *
     * @param authorizationConsent OAuth2授权同意信息对象，用于获取客户端ID和主体名称
     */
    @Override
    public void remove(OAuth2AuthorizationConsent authorizationConsent) {
        // 构造Redis缓存中的行键
        String key = getRowId(authorizationConsent.getRegisteredClientId(), authorizationConsent.getPrincipalName());
        // 从Redis缓存表中删除对应记录
        RedisUtil.deleteCacheTableRow(cacheName, key);
    }

    /**
     * 根据客户端ID和主体名称从Redis缓存中查找OAuth2授权同意信息。
     *
     * @param registeredClientId 客户端ID
     * @param principalName      主体名称（通常是用户名）
     * @return 对应的OAuth2授权同意信息对象，如果未找到则返回null
     */
    @Override
    public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
        // 构造Redis缓存中的行键
        String key = getRowId(registeredClientId, principalName);
        // 从Redis缓存表中获取实体对象
        Oauth2AuthorizationConsent consent = RedisUtil.getCacheTableRow(cacheName, key, Oauth2AuthorizationConsent.class);
        // 将实体对象反向转换为OAuth2授权同意信息对象
        return converter.reverse(consent);
    }
}
