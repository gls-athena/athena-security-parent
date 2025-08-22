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
 * 邮箱认证提供者，用于处理基于邮箱和验证码的OAuth2认证请求。
 * <p>
 * 该类继承了AbstractCustomAuthenticationProvider抽象基类，
 * 专门负责验证邮箱和验证码，并生成相应的访问令牌。
 * </p>
 *
 * @author george
 */
@Slf4j
public class EmailAuthenticationProvider extends AbstractCustomAuthenticationProvider {

    /**
     * 构造函数，创建邮箱认证提供者实例
     *
     * @param authorizationService OAuth2授权服务
     * @param tokenGenerator       OAuth2令牌生成器
     * @param sessionRegistry      会话注册表
     * @param userDetailsService   用户详情服务
     */
    public EmailAuthenticationProvider(
            OAuth2AuthorizationService authorizationService,
            OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator,
            SessionRegistry sessionRegistry,
            UserDetailsService userDetailsService) {
        super(authorizationService, tokenGenerator, sessionRegistry, userDetailsService);
    }

    /**
     * 验证邮箱认证令牌并返回认证主体。
     *
     * @param customAuthentication 邮箱认证令牌
     * @return 认证主体
     * @throws OAuth2AuthenticationException 如果认证失败
     */
    @Override
    protected Authentication authenticateUser(AbstractCustomAuthenticationToken customAuthentication)
            throws OAuth2AuthenticationException {
        EmailAuthenticationToken emailAuthentication = (EmailAuthenticationToken) customAuthentication;
        return validateUserCredentials(emailAuthentication.getEmail(), emailAuthentication.getCode());
    }

    /**
     * 验证邮箱验证码。
     *
     * @param email 邮箱地址
     * @param code  验证码
     * @return 验证结果
     */
    @Override
    protected boolean validateCredential(String email, String code) {
        // 这里应该实现实际的验证码验证逻辑
        // 例如调用邮件服务验证验证码是否正确
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
        return EmailAuthenticationToken.class.isAssignableFrom(authentication);
    }

}