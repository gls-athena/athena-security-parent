package com.gls.athena.security.oauth2.authorization.server.authentication;

import com.gls.athena.security.oauth2.authorization.server.config.AuthorizationServerConstants;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.Set;

/**
 * 邮箱认证令牌类，用于处理OAuth2邮箱授权模式的认证请求
 * 该类继承了AbstractCustomAuthenticationToken，提供了邮箱、验证码和作用域的封装
 *
 * @author george
 */
public class EmailAuthenticationToken extends AbstractCustomAuthenticationToken {

    /**
     * 构造函数，创建邮箱认证令牌实例
     *
     * @param clientPrincipal      客户端主体认证信息
     * @param email                邮箱地址
     * @param code                 验证码
     * @param scopes               请求的作用域集合
     * @param additionalParameters 附加参数映射
     */
    public EmailAuthenticationToken(Authentication clientPrincipal, String email, String code, Set<String> scopes,
                                    Map<String, Object> additionalParameters) {
        super(AuthorizationServerConstants.EMAIL, clientPrincipal, email, code, scopes, additionalParameters);
    }

    /**
     * 获取邮箱地址
     * 为保持向后兼容性而保留的方法
     *
     * @return 邮箱地址
     */
    public String getEmail() {
        return getUserIdentifier();
    }

    /**
     * 获取验证码
     * 为保持向后兼容性而保留的方法
     *
     * @return 验证码
     */
    public String getCode() {
        return getCredential();
    }
}