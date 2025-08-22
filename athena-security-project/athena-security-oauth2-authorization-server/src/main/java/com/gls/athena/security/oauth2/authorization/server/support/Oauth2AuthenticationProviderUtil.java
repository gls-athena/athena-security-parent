package com.gls.athena.security.oauth2.authorization.server.support;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * OAuth2认证提供者工具类，提供客户端认证验证和访问令牌创建的辅助方法
 *
 * @author george
 */
@UtilityClass
public class Oauth2AuthenticationProviderUtil {

    /**
     * 获取已认证的客户端令牌，如果客户端未认证则抛出无效客户端异常
     *
     * @param authentication 包含客户端认证信息的认证对象
     * @return 已认证的OAuth2客户端认证令牌
     * @throws OAuth2AuthenticationException 当客户端未认证或认证无效时抛出INVALID_CLIENT错误
     */
    public OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(Authentication authentication) {
        OAuth2ClientAuthenticationToken clientPrincipal = null;
        // 检查认证主体是否为OAuth2客户端认证令牌类型
        if (OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
            clientPrincipal = (OAuth2ClientAuthenticationToken) authentication.getPrincipal();
        }
        // 验证客户端是否已认证，已认证则返回令牌，否则抛出异常
        if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
            return clientPrincipal;
        }
        throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
    }

    /**
     * 创建并返回OAuth2访问令牌
     *
     * @param builder            授权构建器，用于构建OAuth2授权对象
     * @param token              OAuth2令牌对象，可以是任意继承自OAuth2Token的类型
     * @param accessTokenContext 访问令牌上下文，包含令牌相关的上下文信息
     * @return 构建好的OAuth2访问令牌
     */
    public <T extends OAuth2Token> OAuth2AccessToken accessToken(OAuth2Authorization.Builder builder, T token,
                                                                 OAuth2TokenContext accessTokenContext) {

        // 确定令牌类型：如果token包含cnf声明且其中包含jkt，则为DPoP类型，否则为Bearer类型
        OAuth2AccessToken.TokenType tokenType = OAuth2AccessToken.TokenType.BEARER;
        if (token instanceof ClaimAccessor claimAccessor) {
            Map<String, Object> cnfClaims = claimAccessor.getClaimAsMap("cnf");
            if (!CollectionUtils.isEmpty(cnfClaims) && cnfClaims.containsKey("jkt")) {
                tokenType = OAuth2AccessToken.TokenType.DPOP;
            }
        }

        // 创建OAuth2访问令牌对象
        OAuth2AccessToken accessToken = new OAuth2AccessToken(tokenType, token.getTokenValue(), token.getIssuedAt(),
                token.getExpiresAt(), accessTokenContext.getAuthorizedScopes());

        // 获取访问令牌格式配置
        OAuth2TokenFormat accessTokenFormat = accessTokenContext.getRegisteredClient()
                .getTokenSettings()
                .getAccessTokenFormat();

        // 将令牌添加到构建器中，并设置相关元数据
        builder.token(accessToken, (metadata) -> {
            if (token instanceof ClaimAccessor claimAccessor) {
                metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, claimAccessor.getClaims());
            }
            metadata.put(OAuth2Authorization.Token.INVALIDATED_METADATA_NAME, false);
            metadata.put(OAuth2TokenFormat.class.getName(), accessTokenFormat.getValue());
        });

        return accessToken;
    }

}
