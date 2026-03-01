package com.gls.athena.security.oauth2.authorization.server.redis.service;

import cn.hutool.core.util.StrUtil;
import com.gls.athena.security.oauth2.authorization.server.redis.converter.Oauth2AuthorizationConverter;
import com.gls.athena.security.oauth2.authorization.server.redis.domain.Oauth2Authorization;
import com.gls.athena.starter.data.redis.support.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于Redis的OAuth2授权服务实现类
 * <p>
 * 该类实现了Spring Security OAuth2的{@link OAuth2AuthorizationService}接口，
 * 使用Redis作为存储后端来管理OAuth2授权信息。
 * </p>
 * <p>
 * <b>性能优化说明：</b><br>
 * 原始实现中 {@code findByToken} 和 {@code save} 的去重逻辑均需加载全量数据（O(n)）。
 * 本版本引入两张辅助索引 Hash：
 * <ul>
 *   <li>{@code oauth2:authorization:token} — token值 → authorizationId 的反向索引，
 *       使 {@code findByToken} 从 O(n) 降为 O(1)。</li>
 *   <li>{@code oauth2:authorization:principal} — principalName:clientId → authorizationId 的索引，
 *       使 {@code save} 中的旧授权去重从 O(n) 降为 O(1)。</li>
 * </ul>
 * </p>
 *
 * @author george
 */
@Slf4j
@RequiredArgsConstructor
public class RedisOauth2AuthorizationServiceImpl implements OAuth2AuthorizationService {

    private final Oauth2AuthorizationConverter converter;
    private final RegisteredClientRepository registeredClientRepository;

    /**
     * 主存储 Hash：authorizationId → {@link Oauth2Authorization} 序列化对象
     */
    private static final String CACHE_NAME = "oauth2:authorization";

    /**
     * token反向索引 Hash：tokenValue → authorizationId
     * 覆盖所有令牌类型（state / code / access_token / id_token / refresh_token / device_code / user_code）
     */
    private static final String TOKEN_INDEX = "oauth2:authorization:token";

    /**
     * 主体索引 Hash：principalName:clientId → authorizationId
     * 用于在 save 时快速找到同一用户+客户端的旧授权记录（避免全量扫描）
     */
    private static final String PRINCIPAL_INDEX = "oauth2:authorization:principal";

    /**
     * 保存OAuth2授权信息到Redis中。
     * <p>
     * 流程：
     * <ol>
     *   <li>通过主体索引（O(1)）查找同一 principalName+clientId 的旧授权记录；</li>
     *   <li>若存在旧记录，删除其主存储条目及所有相关 token 索引；</li>
     *   <li>写入新的主存储条目，并为所有非空 token 值写入反向索引。</li>
     * </ol>
     * </p>
     *
     * @param authorization OAuth2授权对象，包含授权相关信息
     */
    @Override
    public void save(OAuth2Authorization authorization) {
        String principalKey = buildPrincipalKey(authorization.getPrincipalName(),
                authorization.getRegisteredClientId());

        // O(1) 通过主体索引查找旧授权记录，并清理其所有索引
        String oldAuthorizationId = RedisUtil.getCacheTableRow(PRINCIPAL_INDEX, principalKey, String.class);
        if (StrUtil.isNotBlank(oldAuthorizationId)) {
            Oauth2Authorization oldAuthorization = RedisUtil.getCacheTableRow(CACHE_NAME, oldAuthorizationId, Oauth2Authorization.class);
            if (oldAuthorization != null) {
                removeTokenIndexes(oldAuthorization);
            }
            RedisUtil.deleteCacheTableRow(CACHE_NAME, oldAuthorizationId);
        }

        // 写入新的主存储条目
        Oauth2Authorization domain = converter.convertToDomain(authorization);
        RedisUtil.setCacheTableRow(CACHE_NAME, domain.getId(), domain);

        // 写入主体索引
        RedisUtil.setCacheTableRow(PRINCIPAL_INDEX, principalKey, domain.getId());

        // 写入所有 token 的反向索引
        addTokenIndexes(domain);
    }

    /**
     * 从Redis中移除指定的OAuth2授权信息及其所有索引。
     *
     * @param authorization OAuth2授权对象，用于获取要删除的授权ID
     */
    @Override
    public void remove(OAuth2Authorization authorization) {
        Oauth2Authorization domain = RedisUtil.getCacheTableRow(CACHE_NAME, authorization.getId(), Oauth2Authorization.class);
        if (domain != null) {
            // 清理 token 反向索引
            removeTokenIndexes(domain);
            // 清理主体索引
            String principalKey = buildPrincipalKey(domain.getPrincipalName(), domain.getRegisteredClientId());
            RedisUtil.deleteCacheTableRow(PRINCIPAL_INDEX, principalKey);
        }
        // 删除主存储条目
        RedisUtil.deleteCacheTableRow(CACHE_NAME, authorization.getId());
    }

    /**
     * 根据授权ID从Redis中查找OAuth2授权信息（O(1)直接主键查询）。
     *
     * @param id 授权ID
     * @return 如果找到对应的授权信息则返回OAuth2Authorization对象，否则返回null
     */
    @Override
    public OAuth2Authorization findById(String id) {
        Oauth2Authorization authorization = RedisUtil.getCacheTableRow(CACHE_NAME, id, Oauth2Authorization.class);
        if (authorization != null) {
            RegisteredClient registeredClient = registeredClientRepository.findById(authorization.getRegisteredClientId());
            return converter.reverseToAuthorization(registeredClient, authorization);
        }
        return null;
    }

    /**
     * 根据令牌值和令牌类型从Redis中查找OAuth2授权信息（O(1) 索引查询）。
     * <p>
     * 通过 token 反向索引直接定位 authorizationId，再调用 {@link #findById} 完成精确查询，
     * 无需遍历全量数据。
     * </p>
     *
     * @param token     令牌值
     * @param tokenType 令牌类型，可以为null表示匹配任意类型的令牌
     * @return 如果找到匹配的授权信息则返回OAuth2Authorization对象，否则返回null
     */
    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        // O(1)：直接通过 token 值查找 authorizationId
        String authorizationId = RedisUtil.getCacheTableRow(TOKEN_INDEX, token, String.class);
        if (StrUtil.isBlank(authorizationId)) {
            return null;
        }

        OAuth2Authorization authorization = findById(authorizationId);

        // 若指定了 tokenType，需进一步核实该 token 确实属于该类型（索引无类型区分）
        if (authorization != null && tokenType != null) {
            Oauth2Authorization domain = RedisUtil.getCacheTableRow(CACHE_NAME, authorizationId, Oauth2Authorization.class);
            if (domain == null || !hasToken(token, tokenType, domain)) {
                return null;
            }
        }
        return authorization;
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 为指定授权对象的所有非空 token 值写入反向索引。
     *
     * @param authorization 授权域对象
     */
    private void addTokenIndexes(Oauth2Authorization authorization) {
        for (String tokenValue : collectTokenValues(authorization)) {
            RedisUtil.setCacheTableRow(TOKEN_INDEX, tokenValue, authorization.getId());
        }
    }

    /**
     * 清理指定授权对象的所有 token 反向索引。
     *
     * @param authorization 授权域对象
     */
    private void removeTokenIndexes(Oauth2Authorization authorization) {
        for (String tokenValue : collectTokenValues(authorization)) {
            RedisUtil.deleteCacheTableRow(TOKEN_INDEX, tokenValue);
        }
    }

    /**
     * 收集授权对象中所有非空的 token 值。
     *
     * @param authorization 授权域对象
     * @return 非空 token 值列表
     */
    private List<String> collectTokenValues(Oauth2Authorization authorization) {
        List<String> values = new ArrayList<>();
        addIfPresent(values, authorization.getState());
        addIfPresent(values, authorization.getAuthorizationCodeValue());
        addIfPresent(values, authorization.getAccessTokenValue());
        addIfPresent(values, authorization.getOidcIdTokenValue());
        addIfPresent(values, authorization.getRefreshTokenValue());
        addIfPresent(values, authorization.getDeviceCodeValue());
        addIfPresent(values, authorization.getUserCodeValue());
        return values;
    }

    /**
     * 若字符串非空则加入列表。
     */
    private void addIfPresent(List<String> list, String value) {
        if (StrUtil.isNotBlank(value)) {
            list.add(value);
        }
    }

    /**
     * 构建主体索引的 field key：{@code principalName:clientId}。
     */
    private String buildPrincipalKey(String principalName, String registeredClientId) {
        return principalName + ":" + registeredClientId;
    }

    /**
     * 检查指定的令牌是否存在于OAuth2授权对象中（用于 tokenType 精确校验）。
     *
     * @param token         要检查的令牌值
     * @param tokenType     令牌类型
     * @param authorization OAuth2授权域对象
     * @return 如果找到匹配的令牌返回true，否则返回false
     */
    private boolean hasToken(String token, OAuth2TokenType tokenType, Oauth2Authorization authorization) {
        if (OAuth2ParameterNames.STATE.equals(tokenType.getValue())) {
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

