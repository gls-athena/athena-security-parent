package com.gls.athena.security.oauth2.authorization.server.redis.service;

import com.gls.athena.security.oauth2.authorization.server.redis.converter.Oauth2AuthorizationConverter;
import com.gls.athena.security.oauth2.authorization.server.redis.domain.Oauth2Authorization;
import com.gls.athena.starter.data.redis.support.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.util.List;

/**
 * 基于Redis的OAuth2授权服务实现类
 * <p>
 * 该类实现了Spring Security OAuth2的{@link OAuth2AuthorizationService}接口，
 * 使用Redis作为存储后端来管理OAuth2授权信息。
 * </p>
 *
 * @author george
 */
@RequiredArgsConstructor
public class RedisOauth2AuthorizationServiceImpl implements OAuth2AuthorizationService {

    private final Oauth2AuthorizationConverter converter;

    private final RegisteredClientRepository registeredClientRepository;

    private final String cacheName = "oauth2:authorization";

    /**
     * 保存OAuth2授权信息到Redis中
     *
     * @param authorization OAuth2授权对象，包含授权相关信息
     */
    @Override
    public void save(OAuth2Authorization authorization) {
        // 获取Redis中已存在的授权信息列表
        List<Oauth2Authorization> authorizations = RedisUtil.getCacheTableRows(cacheName, Oauth2Authorization.class);

        // 查找并删除相同用户和客户端的旧授权信息
        authorizations.stream()
                .filter(auth -> auth.getPrincipalName().equals(authorization.getPrincipalName())
                        && auth.getRegisteredClientId().equals(authorization.getRegisteredClientId()))
                .forEach(auth -> {
                    // 如果已存在相同的授权信息，则先删除旧的授权信息
                    RedisUtil.deleteCacheTableRow(cacheName, auth.getId());
                });

        // 转换并保存新的授权信息
        Oauth2Authorization existingAuthorization = converter.convertToDomain(authorization);
        RedisUtil.setCacheTableRow(cacheName, existingAuthorization.getId(), existingAuthorization);
    }

    /**
     * 从Redis中移除指定的OAuth2授权信息
     *
     * @param authorization OAuth2授权对象，用于获取要删除的授权ID
     */
    @Override
    public void remove(OAuth2Authorization authorization) {
        RedisUtil.deleteCacheTableRow(cacheName, authorization.getId());
    }

    /**
     * 根据授权ID从Redis中查找OAuth2授权信息
     *
     * @param id 授权ID
     * @return 如果找到对应的授权信息则返回OAuth2Authorization对象，否则返回null
     */
    @Override
    public OAuth2Authorization findById(String id) {
        // 从Redis缓存中获取授权信息
        Oauth2Authorization authorization = RedisUtil.getCacheTableRow(cacheName, id, Oauth2Authorization.class);
        if (authorization != null) {
            // 获取注册客户端信息并转换为OAuth2Authorization对象
            RegisteredClient registeredClient = registeredClientRepository.findById(authorization.getRegisteredClientId());
            return converter.reverseToAuthorization(registeredClient, authorization);
        }
        return null;
    }

    /**
     * 根据令牌值和令牌类型从Redis中查找OAuth2授权信息
     *
     * @param token     令牌值
     * @param tokenType 令牌类型，可以为null表示匹配任意类型的令牌
     * @return 如果找到匹配的授权信息则返回OAuth2Authorization对象，否则返回null
     */
    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        // 从Redis缓存中获取所有授权信息
        List<Oauth2Authorization> authorizations = RedisUtil.getCacheTableRows(cacheName, Oauth2Authorization.class);
        return authorizations.stream()
                // 过滤出包含指定令牌的授权信息
                .filter(authorization -> hasToken(token, tokenType, authorization))
                .findFirst()
                // 将持久化授权信息转换为OAuth2Authorization对象
                .map(authorization -> {
                    RegisteredClient registeredClient = registeredClientRepository.findById(authorization.getRegisteredClientId());
                    return converter.reverseToAuthorization(registeredClient, authorization);
                })
                .orElse(null);
    }

    /**
     * 检查指定的令牌是否存在于OAuth2授权对象中
     *
     * @param token         要检查的令牌值
     * @param tokenType     令牌类型，如果为null则检查所有类型的令牌
     * @param authorization OAuth2授权对象，包含各种令牌信息
     * @return 如果找到匹配的令牌返回true，否则返回false
     */
    private boolean hasToken(String token, OAuth2TokenType tokenType, Oauth2Authorization authorization) {
        // 当令牌类型为null时，检查所有可能的令牌类型
        if (tokenType == null) {
            return token.equals(authorization.getState())
                    || token.equals(authorization.getAuthorizationCodeValue())
                    || token.equals(authorization.getAccessTokenValue())
                    || token.equals(authorization.getOidcIdTokenValue())
                    || token.equals(authorization.getRefreshTokenValue())
                    || token.equals(authorization.getDeviceCodeValue())
                    || token.equals(authorization.getUserCodeValue());
        }
        // 根据具体的令牌类型检查对应的令牌值
        else if (OAuth2ParameterNames.STATE.equals(tokenType.getValue())) {
            return token.equals(authorization.getState());
        } else if (OAuth2ParameterNames.CODE.equals(tokenType.getValue())) {
            return token.equals(authorization.getAuthorizationCodeValue());
        } else if (OAuth2ParameterNames.ACCESS_TOKEN.equals(tokenType.getValue())) {
            return token.equals(authorization.getAccessTokenValue());
        } else if (OidcParameterNames.ID_TOKEN.equals(tokenType.getValue())) {
            return token.equals(authorization.getOidcIdTokenValue());
        } else if (OAuth2ParameterNames.REFRESH_TOKEN.equals(tokenType.getValue())) {
            return token.equals(authorization.getRefreshTokenValue());
        } else if (OAuth2ParameterNames.DEVICE_CODE.equals(tokenType.getValue())) {
            return token.equals(authorization.getDeviceCodeValue());
        } else if (OAuth2ParameterNames.USER_CODE.equals(tokenType.getValue())) {
            return token.equals(authorization.getUserCodeValue());
        }
        return false;
    }

}
