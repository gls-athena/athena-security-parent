package com.gls.athena.security.oauth2.authorization.server.authentication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

/**
 * 密码认证提供者，用于处理基于用户名和密码的OAuth2认证请求。
 * <p>
 * 该类继承了AbstractCustomAuthenticationProvider抽象基类，
 * 专门负责验证用户名和密码，并生成相应的访问令牌。
 * </p>
 *
 * @author george
 */
@Slf4j
public class PasswordAuthenticationProvider extends AbstractCustomAuthenticationProvider {

    private final UserDetailsService userDetailsService;

    private final PasswordEncoder passwordEncoder;

    /**
     * 构造函数，创建密码认证提供者实例
     *
     * @param authorizationService OAuth2授权服务
     * @param tokenGenerator       OAuth2令牌生成器
     * @param sessionRegistry      会话注册表
     * @param userDetailsService   用户详情服务
     * @param passwordEncoder      密码编码器
     */
    public PasswordAuthenticationProvider(
            OAuth2AuthorizationService authorizationService,
            OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator,
            SessionRegistry sessionRegistry,
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        super(authorizationService, tokenGenerator, sessionRegistry, userDetailsService);
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 验证密码认证令牌并返回认证主体。
     *
     * @param customAuthentication 密码认证令牌
     * @return 认证主体
     * @throws OAuth2AuthenticationException 如果认证失败
     */
    @Override
    protected Authentication authenticateUser(AbstractCustomAuthenticationToken customAuthentication)
            throws OAuth2AuthenticationException {
        PasswordAuthenticationToken passwordAuthentication = (PasswordAuthenticationToken) customAuthentication;
        return validateUserCredentials(passwordAuthentication.getUsername(), passwordAuthentication.getPassword());
    }

    /**
     * 验证用户密码。
     *
     * @param username 用户名
     * @param password 密码
     * @return 验证结果
     */
    @Override
    protected boolean validateCredential(String username, String password) {
        log.debug("Validating credentials for user: {}", username);
        // 使用密码编码器验证密码
        return passwordEncoder.matches(password, userDetailsService.loadUserByUsername(username).getPassword());
    }

    /**
     * 判断当前提供者是否支持指定类型的认证。
     *
     * @param authentication 认证类型
     * @return 如果支持返回true，否则false
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return PasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
