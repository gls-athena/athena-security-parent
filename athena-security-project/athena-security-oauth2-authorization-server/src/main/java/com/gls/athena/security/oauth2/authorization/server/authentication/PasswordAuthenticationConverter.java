package com.gls.athena.security.oauth2.authorization.server.authentication;

import com.gls.athena.security.oauth2.authorization.server.config.AuthorizationServerConstants;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

import java.util.Map;
import java.util.Set;

/**
 * 密码模式认证转换器，用于将HTTP请求转换为密码模式的认证对象。
 * <p>
 * 该类继承了AbstractCustomAuthenticationConverter抽象基类，专门用于处理OAuth2密码模式（Resource Owner Password Credentials）的认证请求。
 * 它会验证授权类型是否为密码模式，并构造相应的PasswordAuthenticationToken对象。
 *
 * @author george
 */
public class PasswordAuthenticationConverter extends AbstractCustomAuthenticationConverter {

    /**
     * 检查授权类型是否为密码模式。
     *
     * @param grantType 授权类型
     * @return 如果是密码模式返回true，否则返回false
     */
    @Override
    protected boolean isGrantTypeSupported(String grantType) {
        return AuthorizationServerConstants.PASSWORD.getValue().equals(grantType);
    }

    /**
     * 获取用户标识参数名称（密码模式使用username参数）。
     *
     * @return 用户标识参数名称
     */
    @Override
    protected String getUserIdentifierParameterName() {
        return OAuth2ParameterNames.USERNAME;
    }

    /**
     * 获取用户凭证参数名称（密码模式使用password参数）。
     *
     * @return 用户凭证参数名称
     */
    @Override
    protected String getCredentialParameterName() {
        return OAuth2ParameterNames.PASSWORD;
    }

    /**
     * 创建密码认证Token对象。
     *
     * @param clientPrincipal      客户端主体认证信息
     * @param username             用户名
     * @param password             密码
     * @param scopes               请求的作用域集合
     * @param additionalParameters 附加参数映射
     * @return PasswordAuthenticationToken对象
     */
    @Override
    protected Authentication createAuthenticationToken(
            Authentication clientPrincipal,
            String username,
            String password,
            Set<String> scopes,
            Map<String, Object> additionalParameters) {
        return new PasswordAuthenticationToken(clientPrincipal, username, password, scopes, additionalParameters);
    }
}

