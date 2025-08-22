package com.gls.athena.security.oauth2.authorization.server.redis.domain;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Set;

/**
 * OAuth2授权同意信息实体类
 * <p>
 * 该类用于存储客户端授权同意的相关信息，包括客户端ID、主体名称和授权权限集合
 * </p>
 *
 * @author lizy19
 */
@Data
public class Oauth2AuthorizationConsent implements Serializable {

    /**
     * 已注册的客户端ID
     */
    private String registeredClientId;

    /**
     * 主体名称（通常是用户名）
     */
    private String principalName;

    /**
     * 授权权限集合
     */
    private Set<GrantedAuthority> authorities;
}

