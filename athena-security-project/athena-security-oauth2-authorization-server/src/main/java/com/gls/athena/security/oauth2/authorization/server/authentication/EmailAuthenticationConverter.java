package com.gls.athena.security.oauth2.authorization.server.authentication;

import com.gls.athena.security.oauth2.authorization.server.config.AuthorizationServerConstants;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.Set;

/**
 * 邮箱模式认证转换器，用于将HTTP请求转换为邮箱模式的认证对象。
 * <p>
 * 该类继承了AbstractCustomAuthenticationConverter抽象基类，专门用于处理OAuth2邮箱模式的认证请求。
 * 它会验证授权类型是否为邮箱模式，并构造相应的EmailAuthenticationToken对象。
 *
 * @author george
 */
public class EmailAuthenticationConverter extends AbstractCustomAuthenticationConverter {

    /**
     * 检查授权类型是否为邮箱模式。
     *
     * @param grantType 授权类型
     * @return 如果是邮箱模式返回true，否则返回false
     */
    @Override
    protected boolean isGrantTypeSupported(String grantType) {
        // 判断传入的授权类型是否与配置中的邮箱模式匹配
        return AuthorizationServerConstants.EMAIL.getValue().equals(grantType);
    }

    /**
     * 获取用户标识参数名称（邮箱模式使用username参数传递邮箱）。
     *
     * @return 用户标识参数名称
     */
    @Override
    protected String getUserIdentifierParameterName() {
        // 返回OAuth2标准中表示用户名的参数名，此处用于传递邮箱地址
        return AuthorizationServerConstants.EMAIL_PARAMETER_NAME;
    }

    /**
     * 获取用户凭证参数名称（邮箱模式使用password参数传递验证码）。
     *
     * @return 用户凭证参数名称
     */
    @Override
    protected String getCredentialParameterName() {
        // 返回OAuth2标准中表示密码的参数名，此处用于传递验证码
        return AuthorizationServerConstants.EMAIL_CAPTCHA_PARAMETER_NAME;
    }

    /**
     * 创建邮箱认证Token对象。
     *
     * @param clientPrincipal      客户端主体认证信息
     * @param email                邮箱地址
     * @param code                 验证码
     * @param scopes               请求的作用域集合
     * @param additionalParameters 附加参数映射
     * @return EmailAuthenticationToken对象
     */
    @Override
    protected Authentication createAuthenticationToken(
            Authentication clientPrincipal,
            String email,
            String code,
            Set<String> scopes,
            Map<String, Object> additionalParameters) {
        // 构造并返回一个包含客户端信息、邮箱、验证码及作用域的认证Token
        return new EmailAuthenticationToken(clientPrincipal, email, code, scopes, additionalParameters);
    }

}
