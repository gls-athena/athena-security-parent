package com.gls.athena.security.oauth2.authorization.server.authentication;

import com.gls.athena.security.oauth2.authorization.server.support.DPoPProofVerifier;
import com.gls.athena.security.oauth2.authorization.server.support.Oauth2AuthenticationProviderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.*;

/**
 * 抽象自定义认证提供者基类，为不同认证模式提供通用的认证处理逻辑。
 * <p>
 * 该抽象类封装了三种认证模式（邮箱、手机、密码）的通用处理流程，
 * 包括DPoP验证、令牌生成、OpenID Connect支持等。
 * 子类只需要实现具体的用户认证逻辑和授权类型检查。
 * </p>
 *
 * @author george
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractCustomAuthenticationProvider implements AuthenticationProvider {

    protected static final String ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2";
    protected static final OAuth2TokenType ID_TOKEN_TOKEN_TYPE = new OAuth2TokenType(OidcParameterNames.ID_TOKEN);

    /**
     * OAuth2授权服务，用于保存生成的授权信息。
     */
    protected final OAuth2AuthorizationService authorizationService;

    /**
     * OAuth2令牌生成器，用于创建访问令牌、刷新令牌和ID令牌。
     */
    protected final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;

    /**
     * 会话注册表，用于获取用户的会话信息。
     */
    protected final SessionRegistry sessionRegistry;

    /**
     * 用户详情服务，用于加载用户信息。
     */
    protected final UserDetailsService userDetailsService;

    /**
     * 通用的认证处理流程模板方法。
     *
     * @param authentication 待验证的认证对象
     * @return 成功认证后的OAuth2AccessTokenAuthenticationToken
     * @throws AuthenticationException 如果认证失败
     */
    @Override
    public final Authentication authenticate(Authentication authentication) throws AuthenticationException {
        AbstractCustomAuthenticationToken customAuthentication = (AbstractCustomAuthenticationToken) authentication;

        // 获取已认证的客户端主体
        OAuth2ClientAuthenticationToken clientPrincipal = Oauth2AuthenticationProviderUtil
                .getAuthenticatedClientElseThrowInvalidClient(customAuthentication);
        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();

        if (log.isTraceEnabled()) {
            log.trace("Retrieved registered client");
        }

        // 验证DPoP证明（如果存在）
        Jwt dPoPProof = DPoPProofVerifier.verifyIfAvailable(customAuthentication);

        if (log.isTraceEnabled()) {
            log.trace("Validated token request parameters");
        }

        // 获取用户主体信息，由子类实现具体的认证逻辑
        Authentication principal = authenticateUser(customAuthentication);

        // 构建用于令牌生成的上下文
        DefaultOAuth2TokenContext.Builder tokenContextBuilder = buildTokenContextBuilder(
                registeredClient, principal, customAuthentication, dPoPProof);

        // 初始化授权构建器
        OAuth2Authorization.Builder authorizationBuilder = buildAuthorizationBuilder(
                registeredClient, principal, customAuthentication);

        // 生成访问令牌
        OAuth2AccessToken accessToken = getAccessToken(tokenContextBuilder, authorizationBuilder);

        // 生成刷新令牌（仅限支持刷新令牌的客户端）
        OAuth2RefreshToken refreshToken = getRefreshToken(registeredClient, tokenContextBuilder, authorizationBuilder);

        // 生成ID令牌（OpenID Connect）
        OidcIdToken idToken = getOidcIdToken(customAuthentication, principal, tokenContextBuilder, authorizationBuilder);

        // 保存授权信息
        OAuth2Authorization authorization = authorizationBuilder.build();
        this.authorizationService.save(authorization);

        if (log.isTraceEnabled()) {
            log.trace("Saved authorization");
        }

        // 构造附加参数（如ID Token）
        Map<String, Object> additionalParameters = Collections.emptyMap();
        if (idToken != null) {
            additionalParameters = new HashMap<>();
            additionalParameters.put(OidcParameterNames.ID_TOKEN, idToken.getTokenValue());
        }

        if (log.isTraceEnabled()) {
            log.trace("Authenticated token request");
        }

        // 返回包含访问令牌、刷新令牌和附加参数的认证结果
        return new OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, accessToken, refreshToken,
                additionalParameters);
    }

    /**
     * 构建令牌上下文构建器。
     *
     * @param registeredClient     已注册的客户端
     * @param principal            用户认证主体
     * @param customAuthentication 自定义认证令牌
     * @param dPoPProof            DPoP证明（可选）
     * @return 令牌上下文构建器
     */
    private DefaultOAuth2TokenContext.Builder buildTokenContextBuilder(
            RegisteredClient registeredClient,
            Authentication principal,
            AbstractCustomAuthenticationToken customAuthentication,
            Jwt dPoPProof) {
        DefaultOAuth2TokenContext.Builder builder = DefaultOAuth2TokenContext.builder()
                .registeredClient(registeredClient)
                .principal(principal)
                .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                .authorizedScopes(customAuthentication.getScopes())
                .authorizationGrantType(customAuthentication.getGrantType())
                .authorizationGrant(customAuthentication);
        if (dPoPProof != null) {
            builder.put(OAuth2TokenContext.DPOP_PROOF_KEY, dPoPProof);
        }
        return builder;
    }

    /**
     * 构建授权信息构建器。
     *
     * @param registeredClient     已注册的客户端
     * @param principal            用户认证主体
     * @param customAuthentication 自定义认证令牌
     * @return 授权信息构建器
     */
    private OAuth2Authorization.Builder buildAuthorizationBuilder(
            RegisteredClient registeredClient,
            Authentication principal,
            AbstractCustomAuthenticationToken customAuthentication) {
        return OAuth2Authorization.withRegisteredClient(registeredClient)
                .principalName(principal.getName())
                .authorizationGrantType(customAuthentication.getGrantType())
                .authorizedScopes(customAuthentication.getScopes())
                .attribute(Principal.class.getName(), principal);
    }

    /**
     * 生成OIDC ID令牌。
     *
     * @param customAuthentication 自定义认证令牌
     * @param principal            用户认证主体
     * @param tokenContextBuilder  令牌上下文构建器
     * @param authorizationBuilder 授权信息构建器
     * @return 生成的OIDC ID令牌，若不支持则返回null
     */
    private OidcIdToken getOidcIdToken(
            AbstractCustomAuthenticationToken customAuthentication,
            Authentication principal,
            DefaultOAuth2TokenContext.Builder tokenContextBuilder,
            OAuth2Authorization.Builder authorizationBuilder) {

        if (customAuthentication.getScopes().contains(OidcScopes.OPENID)) {
            SessionInformation sessionInformation = getSessionInformation(principal);
            if (sessionInformation != null) {
                try {
                    sessionInformation = new SessionInformation(sessionInformation.getPrincipal(),
                            createHash(sessionInformation.getSessionId()), sessionInformation.getLastRequest());
                } catch (NoSuchAlgorithmException ex) {
                    OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                            "Failed to compute hash for Session ID.", ERROR_URI);
                    throw new OAuth2AuthenticationException(error);
                }
                tokenContextBuilder.put(SessionInformation.class, sessionInformation);
            }

            OAuth2TokenContext tokenContext = tokenContextBuilder
                    .tokenType(ID_TOKEN_TOKEN_TYPE)
                    .authorization(authorizationBuilder.build())
                    .build();

            OAuth2Token generatedIdToken = this.tokenGenerator.generate(tokenContext);
            if (!(generatedIdToken instanceof Jwt)) {
                OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                        "The token generator failed to generate the ID token.", ERROR_URI);
                throw new OAuth2AuthenticationException(error);
            }

            if (log.isTraceEnabled()) {
                log.trace("Generated id token");
            }

            OidcIdToken idToken = new OidcIdToken(generatedIdToken.getTokenValue(), generatedIdToken.getIssuedAt(),
                    generatedIdToken.getExpiresAt(), ((Jwt) generatedIdToken).getClaims());
            authorizationBuilder.token(idToken,
                    (metadata) -> metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, idToken.getClaims()));
            return idToken;
        }
        return null;
    }

    /**
     * 获取刷新令牌。
     *
     * @param registeredClient     已注册的客户端
     * @param tokenContextBuilder  令牌上下文构建器
     * @param authorizationBuilder 授权信息构建器
     * @return 刷新令牌，若不支持或未生成则返回null
     */
    private OAuth2RefreshToken getRefreshToken(
            RegisteredClient registeredClient,
            DefaultOAuth2TokenContext.Builder tokenContextBuilder,
            OAuth2Authorization.Builder authorizationBuilder) {

        if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN)) {
            OAuth2TokenContext tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();
            OAuth2Token generatedRefreshToken = this.tokenGenerator.generate(tokenContext);

            if (generatedRefreshToken != null) {
                if (!(generatedRefreshToken instanceof OAuth2RefreshToken refreshToken)) {
                    OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                            "The token generator failed to generate a valid refresh token.", ERROR_URI);
                    throw new OAuth2AuthenticationException(error);
                }

                if (log.isTraceEnabled()) {
                    log.trace("Generated refresh token");
                }

                authorizationBuilder.refreshToken(refreshToken);
                return refreshToken;
            }
        }
        return null;
    }

    /**
     * 获取访问令牌。
     *
     * @param tokenContextBuilder  令牌上下文构建器
     * @param authorizationBuilder 授权信息构建器
     * @return 生成的访问令牌
     */
    private OAuth2AccessToken getAccessToken(
            DefaultOAuth2TokenContext.Builder tokenContextBuilder,
            OAuth2Authorization.Builder authorizationBuilder) {

        OAuth2TokenContext tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build();
        OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContext);

        if (generatedAccessToken == null) {
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                    "The token generator failed to generate the access token.", ERROR_URI);
            throw new OAuth2AuthenticationException(error);
        }

        if (log.isTraceEnabled()) {
            log.trace("Generated access token");
        }

        return Oauth2AuthenticationProviderUtil.accessToken(authorizationBuilder,
                generatedAccessToken, tokenContext);
    }

    /**
     * 获取用户的会话信息。
     *
     * @param principal 用户认证主体
     * @return 最近一次会话信息，若无则返回null
     */
    private SessionInformation getSessionInformation(Authentication principal) {
        SessionInformation sessionInformation = null;
        if (this.sessionRegistry != null) {
            List<SessionInformation> sessions = this.sessionRegistry.getAllSessions(principal.getPrincipal(), false);
            if (!CollectionUtils.isEmpty(sessions)) {
                sessionInformation = sessions.getFirst();
                if (sessions.size() > 1) {
                    sessions = new ArrayList<>(sessions);
                    sessions.sort(Comparator.comparing(SessionInformation::getLastRequest));
                    sessionInformation = sessions.getLast();
                }
            }
        }
        return sessionInformation;
    }

    /**
     * 使用SHA-256算法对字符串进行哈希处理。
     *
     * @param value 待哈希的字符串
     * @return 哈希后的Base64 URL安全编码字符串
     * @throws NoSuchAlgorithmException 若不支持SHA-256算法
     */
    private String createHash(String value) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(value.getBytes(StandardCharsets.US_ASCII));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    }

    /**
     * 验证用户凭据并返回认证主体。
     * 子类需要实现此方法以提供具体的用户认证逻辑。
     *
     * @param customAuthentication 自定义认证令牌
     * @return 认证主体
     * @throws OAuth2AuthenticationException 如果认证失败
     */
    protected abstract Authentication authenticateUser(AbstractCustomAuthenticationToken customAuthentication)
            throws OAuth2AuthenticationException;

    /**
     * 验证用户凭据的通用逻辑。
     *
     * @param identifier 用户标识（用户名/邮箱/手机号）
     * @param credential 用户凭证（密码/验证码）
     * @return 认证主体
     * @throws OAuth2AuthenticationException 如果认证失败
     */
    protected final Authentication validateUserCredentials(String identifier, String credential)
            throws OAuth2AuthenticationException {

        // 验证凭证
        if (!validateCredential(identifier, credential)) {
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT,
                    "Invalid credentials.", ERROR_URI);
            throw new OAuth2AuthenticationException(error);
        }

        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(identifier);
        } catch (UsernameNotFoundException ex) {
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT,
                    "Invalid credentials.", ERROR_URI);
            throw new OAuth2AuthenticationException(error);
        }

        if (!userDetails.isAccountNonLocked() || !userDetails.isEnabled() ||
                !userDetails.isAccountNonExpired() || !userDetails.isCredentialsNonExpired()) {
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT,
                    "User account status is not valid.", ERROR_URI));
        }

        return new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
    }

    /**
     * 验证用户凭证。
     * 子类需要实现此方法以提供具体的凭证验证逻辑。
     *
     * @param identifier 用户标识
     * @param credential 用户凭证
     * @return 验证结果
     */
    protected abstract boolean validateCredential(String identifier, String credential);
}
