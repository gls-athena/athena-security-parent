package com.gls.athena.security.oauth2.authorization.server.authentication;

import com.gls.athena.security.oauth2.authorization.server.config.AuthorizationServerConstants;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.Set;

/**
 * 手机模式认证转换器，用于将HTTP请求转换为手机模式的认证对象。
 * <p>
 * 该类继承了AbstractCustomAuthenticationConverter抽象基类，专门用于处理OAuth2手机模式的认证请求。
 * 它会验证授权类型是否为手机模式，并构造相应的MobileAuthenticationToken对象。
 *
 * @author george
 */
public class MobileAuthenticationConverter extends AbstractCustomAuthenticationConverter {

    /**
     * 检查授权类型是否为手机模式。
     *
     * @param grantType 授权类型
     * @return 如果是手机模式返回true，否则返回false
     */
    @Override
    protected boolean isGrantTypeSupported(String grantType) {
        // 判断传入的授权类型是否与配置中的手机模式授权类型一致
        return AuthorizationServerConstants.MOBILE.getValue().equals(grantType);
    }

    /**
     * 获取用户标识参数名称（手机模式使用username参数传递手机号）。
     *
     * @return 用户标识参数名称
     */
    @Override
    protected String getUserIdentifierParameterName() {
        // 返回手机号参数名常量
        return AuthorizationServerConstants.MOBILE_PARAMETER_NAME;
    }

    /**
     * 获取用户凭证参数名称（手机模式使用password参数传递验证码）。
     *
     * @return 用户凭证参数名称
     */
    @Override
    protected String getCredentialParameterName() {
        // 返回验证码参数名常量
        return AuthorizationServerConstants.MOBILE_CAPTCHA_PARAMETER_NAME;
    }

    /**
     * 创建手机认证Token对象。
     *
     * @param clientPrincipal      客户端主体认证信息
     * @param mobile               手机号
     * @param code                 验证码
     * @param scopes               请求的作用域集合
     * @param additionalParameters 附加参数映射
     * @return MobileAuthenticationToken对象
     */
    @Override
    protected Authentication createAuthenticationToken(
            Authentication clientPrincipal,
            String mobile,
            String code,
            Set<String> scopes,
            Map<String, Object> additionalParameters) {
        // 构造并返回一个新的MobileAuthenticationToken实例
        return new MobileAuthenticationToken(clientPrincipal, mobile, code, scopes, additionalParameters);
    }

}
