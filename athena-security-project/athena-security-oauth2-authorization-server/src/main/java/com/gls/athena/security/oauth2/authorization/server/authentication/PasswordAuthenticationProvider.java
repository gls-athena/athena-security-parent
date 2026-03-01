package com.gls.athena.security.oauth2.authorization.server.authentication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

/**
 * 密码认证提供者，用于处理基于用户名和密码的OAuth2认证请求。
 * <p>
 * 该类继承了AbstractCustomAuthenticationProvider抽象基类，
 * 专门负责验证用户名和密码，并生成相应的访问令牌。
 * </p>
 * <p>
 * 优化说明：重写 {@link #authenticateUser} 方法，在单次 {@code loadUserByUsername} 调用中
 * 同时完成密码校验和账号状态检查，避免父类 {@code validateUserCredentials} 中的重复数据库查询。
 * </p>
 *
 * @author george
 */
@Slf4j
public class PasswordAuthenticationProvider extends AbstractCustomAuthenticationProvider {

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
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 验证密码认证令牌并返回认证主体。
     * <p>
     * 只进行一次 {@code loadUserByUsername} 调用，同时完成密码比对和账号状态校验，
     * 避免父类 {@code validateUserCredentials} 中的第二次重复查询。
     * </p>
     *
     * @param customAuthentication 密码认证令牌
     * @return 认证主体
     * @throws OAuth2AuthenticationException 如果认证失败
     */
    @Override
    protected Authentication authenticateUser(AbstractCustomAuthenticationToken customAuthentication)
            throws OAuth2AuthenticationException {
        PasswordAuthenticationToken passwordAuthentication = (PasswordAuthenticationToken) customAuthentication;
        String username = passwordAuthentication.getUsername();
        String password = passwordAuthentication.getPassword();

        log.debug("Authenticating user: {}", username);

        // 单次加载用户，同时用于密码验证和账号状态检查
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException ex) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT, "Invalid credentials.", ERROR_URI));
        }

        // 验证密码
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT, "Invalid credentials.", ERROR_URI));
        }

        // 验证账号状态
        if (!userDetails.isAccountNonLocked() || !userDetails.isEnabled()
                || !userDetails.isAccountNonExpired() || !userDetails.isCredentialsNonExpired()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT, "User account status is not valid.", ERROR_URI));
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    /**
     * 密码认证中不使用此方法（逻辑已内联到 {@link #authenticateUser} 以避免重复查询）。
     * 此实现仅作为抽象方法的占位符，实际路径不会被调用。
     *
     * @param identifier 用户标识
     * @param credential 用户凭证
     * @return 始终返回 false（不应被调用）
     */
    @Override
    protected boolean validateCredential(String identifier, String credential) {
        // 该方法在 PasswordAuthenticationProvider 中不会被调用，
        // 因为 authenticateUser() 已完整实现了认证逻辑（单次 loadUserByUsername）。
        return false;
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
