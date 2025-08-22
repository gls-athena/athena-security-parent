package com.gls.athena.security.oauth2.authorization.server.authentication;

import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;

import java.util.Map;
import java.util.Set;

/**
 * 抽象自定义认证令牌基类，为不同认证模式提供通用的Token结构。
 * <p>
 * 该抽象类封装了三种认证模式（邮箱、手机、密码）的通用属性和行为，
 * 包括用户标识（用户名/邮箱/手机号）、凭证（密码/验证码）和作用域等。
 * </p>
 *
 * @author george
 */
@Getter
public abstract class AbstractCustomAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

    /**
     * 用户标识（用户名、邮箱或手机号）
     */
    private final String userIdentifier;

    /**
     * 用户凭证（密码或验证码）
     */
    private final String credential;

    /**
     * 请求的作用域集合
     */
    private final Set<String> scopes;

    /**
     * 构造函数，创建自定义认证令牌实例
     *
     * @param grantType            授权类型，表示当前使用的OAuth2授权方式
     * @param clientPrincipal      客户端主体认证信息，用于标识发起请求的客户端
     * @param userIdentifier       用户标识，可以是用户名、邮箱或手机号
     * @param credential           用户凭证，可以是密码或验证码
     * @param scopes               请求的作用域集合，指定访问权限范围
     * @param additionalParameters 附加参数映射，包含额外的请求参数
     */
    protected AbstractCustomAuthenticationToken(
            AuthorizationGrantType grantType,
            Authentication clientPrincipal,
            String userIdentifier,
            String credential,
            Set<String> scopes,
            Map<String, Object> additionalParameters) {
        super(grantType, clientPrincipal, additionalParameters);
        this.userIdentifier = userIdentifier;
        this.credential = credential;
        this.scopes = scopes;
    }
}

