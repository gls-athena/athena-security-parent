package com.gls.athena.security.oauth2.authorization.server.authentication;

import com.gls.athena.security.oauth2.authorization.server.config.AuthorizationServerConstants;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.Set;

/**
 * 密码认证令牌类，用于处理OAuth2密码授权模式的认证请求
 * 该类继承了AbstractCustomAuthenticationToken，提供了用户名、密码和作用域的封装
 *
 * @author george
 */
public class PasswordAuthenticationToken extends AbstractCustomAuthenticationToken {

    /**
     * 构造函数，创建密码认证令牌实例
     *
     * @param clientPrincipal      客户端主体认证信息
     * @param username             用户名
     * @param password             密码
     * @param scopes               请求的作用域集合
     * @param additionalParameters 附加参数映射
     */
    public PasswordAuthenticationToken(Authentication clientPrincipal, String username, String password, Set<String> scopes,
                                       Map<String, Object> additionalParameters) {
        super(AuthorizationServerConstants.PASSWORD, clientPrincipal, username, password, scopes, additionalParameters);
    }

    /**
     * 获取用户名
     * 为保持向后兼容性而保留的方法
     *
     * @return 用户名
     */
    public String getUsername() {
        return getUserIdentifier();
    }

    /**
     * 获取密码
     * 为保持向后兼容性而保留的方法
     *
     * @return 密码
     */
    public String getPassword() {
        return getCredential();
    }
}
