package com.gls.athena.security.oauth2.authorization.server.authentication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

/**
 * 手机认证提供者，用于处理基于手机号和验证码的OAuth2认证请求。
 * <p>
 * 该类继承了AbstractCustomAuthenticationProvider抽象基类，
 * 专门负责验证手机号和验证码，并生成相应的访问令牌。
 * </p>
 *
 * @author george
 */
@Slf4j
public class MobileAuthenticationProvider extends AbstractCustomAuthenticationProvider {

    /**
     * 构造函数，创建手机认证提供者实例
     *
     * @param authorizationService OAuth2授权服务
     * @param tokenGenerator       OAuth2令牌生成器
     * @param sessionRegistry      会话注册表
     * @param userDetailsService   用户详情服务
     */
    public MobileAuthenticationProvider(
            OAuth2AuthorizationService authorizationService,
            OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator,
            SessionRegistry sessionRegistry,
            UserDetailsService userDetailsService) {
        super(authorizationService, tokenGenerator, sessionRegistry, userDetailsService);
    }

    /**
     * 验证手机认证令牌并返回认证主体。
     *
     * @param customAuthentication 手机认证令牌
     * @return 认证主体
     * @throws OAuth2AuthenticationException 如果认证失败
     */
    @Override
    protected Authentication authenticateUser(AbstractCustomAuthenticationToken customAuthentication)
            throws OAuth2AuthenticationException {
        MobileAuthenticationToken mobileAuthentication = (MobileAuthenticationToken) customAuthentication;
        return validateUserCredentials(mobileAuthentication.getMobile(), mobileAuthentication.getCode());
    }

    /**
     * 验证手机验证码。
     *
     * @param mobile 手机号
     * @param code   验证码
     * @return 验证结果
     */
    @Override
    protected boolean validateCredential(String mobile, String code) {
        // 这里应该实现实际的验证码验证逻辑
        // 例如调用短信服务验证验证码是否正确
        // 暂时返回true用于演示
        return true;
    }

    /**
     * 判断当前提供者是否支持指定类型的认证。
     *
     * @param authentication 认证类型
     * @return 如果支持返回true，否则false
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return MobileAuthenticationToken.class.isAssignableFrom(authentication);
    }

}