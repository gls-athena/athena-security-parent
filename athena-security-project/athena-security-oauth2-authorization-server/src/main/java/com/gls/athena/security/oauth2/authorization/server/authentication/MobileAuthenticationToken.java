package com.gls.athena.security.oauth2.authorization.server.authentication;

import com.gls.athena.security.oauth2.authorization.server.config.AuthorizationServerConstants;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.Set;

/**
 * 手机认证令牌类，用于处理OAuth2手机授权模式的认证请求
 * 该类继承了AbstractCustomAuthenticationToken，提供了手机号、验证码和作用域的封装
 *
 * @author george
 */
public class MobileAuthenticationToken extends AbstractCustomAuthenticationToken {

    /**
     * 构造函数，创建手机认证令牌实例
     *
     * @param clientPrincipal      客户端主体认证信息
     * @param mobile               手机号
     * @param code                 验证码
     * @param scopes               请求的作用域集合
     * @param additionalParameters 附加参数映射
     */
    public MobileAuthenticationToken(Authentication clientPrincipal, String mobile, String code, Set<String> scopes,
                                     Map<String, Object> additionalParameters) {
        super(AuthorizationServerConstants.MOBILE, clientPrincipal, mobile, code, scopes, additionalParameters);
    }

    /**
     * 获取手机号
     * 为保持向后兼容性而保留的方法
     *
     * @return 手机号
     */
    public String getMobile() {
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