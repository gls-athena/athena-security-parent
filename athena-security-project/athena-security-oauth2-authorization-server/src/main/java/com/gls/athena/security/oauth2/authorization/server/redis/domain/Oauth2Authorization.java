package com.gls.athena.security.oauth2.authorization.server.redis.domain;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

/**
 * OAuth2授权信息实体类
 * 用于存储和管理OAuth2授权过程中的各种令牌和相关信息
 *
 * @author lizy19
 */
@Data
public class Oauth2Authorization implements Serializable {

    /**
     * 授权记录的唯一标识符
     */
    private String id;

    /**
     * 注册客户端的ID
     */
    private String registeredClientId;

    /**
     * 授权主体名称（通常是用户名）
     */
    private String principalName;

    /**
     * 授权许可类型
     */
    private String authorizationGrantType;

    /**
     * 已授权的作用域集合
     */
    private Set<String> authorizedScopes;

    /**
     * 授权属性映射
     */
    private Map<String, Object> attributes;

    /**
     * 授权状态标识
     */
    private String state;

    /**
     * 授权码值
     */
    private String authorizationCodeValue;

    /**
     * 授权码签发时间
     */
    private Instant authorizationCodeIssuedAt;

    /**
     * 授权码过期时间
     */
    private Instant authorizationCodeExpiresAt;

    /**
     * 授权码元数据
     */
    private Map<String, Object> authorizationCodeMetadata;

    /**
     * 访问令牌值
     */
    private String accessTokenValue;

    /**
     * 访问令牌签发时间
     */
    private Instant accessTokenIssuedAt;

    /**
     * 访问令牌过期时间
     */
    private Instant accessTokenExpiresAt;

    /**
     * 访问令牌元数据
     */
    private Map<String, Object> accessTokenMetadata;

    /**
     * 访问令牌类型
     */
    private String accessTokenType;

    /**
     * 访问令牌作用域集合
     */
    private Set<String> accessTokenScopes;

    /**
     * OpenID Connect ID令牌值
     */
    private String oidcIdTokenValue;

    /**
     * OpenID Connect ID令牌签发时间
     */
    private Instant oidcIdTokenIssuedAt;

    /**
     * OpenID Connect ID令牌过期时间
     */
    private Instant oidcIdTokenExpiresAt;

    /**
     * OpenID Connect ID令牌元数据
     */
    private Map<String, Object> oidcIdTokenMetadata;

    /**
     * 刷新令牌值
     */
    private String refreshTokenValue;

    /**
     * 刷新令牌签发时间
     */
    private Instant refreshTokenIssuedAt;

    /**
     * 刷新令牌过期时间
     */
    private Instant refreshTokenExpiresAt;

    /**
     * 刷新令牌元数据
     */
    private Map<String, Object> refreshTokenMetadata;

    /**
     * 用户码值
     */
    private String userCodeValue;

    /**
     * 用户码签发时间
     */
    private Instant userCodeIssuedAt;

    /**
     * 用户码过期时间
     */
    private Instant userCodeExpiresAt;

    /**
     * 用户码元数据
     */
    private Map<String, Object> userCodeMetadata;

    /**
     * 设备码值
     */
    private String deviceCodeValue;

    /**
     * 设备码签发时间
     */
    private Instant deviceCodeIssuedAt;

    /**
     * 设备码过期时间
     */
    private Instant deviceCodeExpiresAt;

    /**
     * 设备码元数据
     */
    private Map<String, Object> deviceCodeMetadata;
}

