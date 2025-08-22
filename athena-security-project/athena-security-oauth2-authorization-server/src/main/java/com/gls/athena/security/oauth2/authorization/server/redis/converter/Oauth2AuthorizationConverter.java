package com.gls.athena.security.oauth2.authorization.server.redis.converter;

import cn.hutool.core.util.StrUtil;
import com.gls.athena.security.oauth2.authorization.server.redis.domain.Oauth2Authorization;
import org.mapstruct.*;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.util.Map;

/**
 * OAuth2 授权信息转换器接口，用于在 Spring Security 的 OAuth2Authorization 对象与自定义的 Oauth2Authorization 实体之间进行转换。
 * 使用 MapStruct 进行映射配置，忽略空值属性映射。
 *
 * @author lizy19
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface Oauth2AuthorizationConverter {

    /**
     * 将 Spring Security 的 OAuth2Authorization 转换为自定义的 Oauth2Authorization 域对象。
     * 包括基础授权信息、授权码、访问令牌、ID 令牌、刷新令牌、用户码和设备码等信息的复制。
     *
     * @param authorization Spring Security 提供的 OAuth2 授权对象
     * @return 转换后的自定义 Oauth2Authorization 域对象
     */
    default Oauth2Authorization convertToDomain(OAuth2Authorization authorization) {
        Oauth2Authorization oauth2Authorization = new Oauth2Authorization();
        copyAuthorization(authorization, oauth2Authorization);

        // 复制各种令牌信息到目标对象中
        copyState(authorization.getAttribute(OAuth2ParameterNames.STATE), oauth2Authorization);
        copyAuthorizationCode(authorization.getToken(OAuth2AuthorizationCode.class), oauth2Authorization);
        copyAccessToken(authorization.getAccessToken(), oauth2Authorization);
        copyOidcToken(authorization.getToken(OidcIdToken.class), oauth2Authorization);
        copyRefreshToken(authorization.getRefreshToken(), oauth2Authorization);
        copyUserCode(authorization.getToken(OAuth2UserCode.class), oauth2Authorization);
        copyDeviceCode(authorization.getToken(OAuth2DeviceCode.class), oauth2Authorization);

        return oauth2Authorization;
    }

    /**
     * 拷贝基础授权信息（如客户端ID、主体名称、授权类型、作用域、属性等）。
     *
     * @param authorization       Spring Security 的 OAuth2 授权对象
     * @param oauth2Authorization 自定义的 Oauth2Authorization 域对象
     */
    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "registeredClientId", source = "registeredClientId"),
            @Mapping(target = "principalName", source = "principalName"),
            @Mapping(target = "authorizationGrantType", source = "authorizationGrantType.value"),
            @Mapping(target = "authorizedScopes", source = "authorizedScopes"),
            @Mapping(target = "attributes", source = "attributes"),
    })
    void copyAuthorization(OAuth2Authorization authorization, @MappingTarget Oauth2Authorization oauth2Authorization);

    /**
     * 拷贝授权状态（state）字段。
     *
     * @param state               授权状态字符串
     * @param oauth2Authorization 自定义的 Oauth2Authorization 域对象
     */
    @Mapping(target = "state", source = "state")
    void copyState(String state, @MappingTarget Oauth2Authorization oauth2Authorization);

    /**
     * 拷贝授权码相关信息（值、签发时间、过期时间、元数据）。
     *
     * @param token               OAuth2 授权码令牌对象
     * @param oauth2Authorization 自定义的 Oauth2Authorization 域对象
     */
    @Mappings({
            @Mapping(target = "authorizationCodeValue", source = "token.tokenValue"),
            @Mapping(target = "authorizationCodeIssuedAt", source = "token.issuedAt"),
            @Mapping(target = "authorizationCodeExpiresAt", source = "token.expiresAt"),
            @Mapping(target = "authorizationCodeMetadata", source = "metadata"),
    })
    void copyAuthorizationCode(OAuth2Authorization.Token<OAuth2AuthorizationCode> token, @MappingTarget Oauth2Authorization oauth2Authorization);

    /**
     * 拷贝访问令牌相关信息（值、类型、签发时间、过期时间、作用域、元数据）。
     *
     * @param accessToken         OAuth2 访问令牌对象
     * @param oauth2Authorization 自定义的 Oauth2Authorization 域对象
     */
    @Mappings({
            @Mapping(target = "accessTokenValue", source = "token.tokenValue"),
            @Mapping(target = "accessTokenIssuedAt", source = "token.issuedAt"),
            @Mapping(target = "accessTokenExpiresAt", source = "token.expiresAt"),
            @Mapping(target = "accessTokenType", source = "token.tokenType.value"),
            @Mapping(target = "accessTokenScopes", source = "token.scopes"),
            @Mapping(target = "accessTokenMetadata", source = "metadata"),
    })
    void copyAccessToken(OAuth2Authorization.Token<OAuth2AccessToken> accessToken, @MappingTarget Oauth2Authorization oauth2Authorization);

    /**
     * 拷贝 OIDC ID 令牌相关信息（值、签发时间、过期时间、元数据）。
     *
     * @param token               OIDC ID 令牌对象
     * @param oauth2Authorization 自定义的 Oauth2Authorization 域对象
     */
    @Mappings({
            @Mapping(target = "oidcIdTokenValue", source = "token.tokenValue"),
            @Mapping(target = "oidcIdTokenIssuedAt", source = "token.issuedAt"),
            @Mapping(target = "oidcIdTokenExpiresAt", source = "token.expiresAt"),
            @Mapping(target = "oidcIdTokenMetadata", source = "metadata"),
    })
    void copyOidcToken(OAuth2Authorization.Token<OidcIdToken> token, @MappingTarget Oauth2Authorization oauth2Authorization);

    /**
     * 拷贝刷新令牌相关信息（值、签发时间、过期时间、元数据）。
     *
     * @param refreshToken        OAuth2 刷新令牌对象
     * @param oauth2Authorization 自定义的 Oauth2Authorization 域对象
     */
    @Mappings({
            @Mapping(target = "refreshTokenValue", source = "token.tokenValue"),
            @Mapping(target = "refreshTokenIssuedAt", source = "token.issuedAt"),
            @Mapping(target = "refreshTokenExpiresAt", source = "token.expiresAt"),
            @Mapping(target = "refreshTokenMetadata", source = "metadata"),
    })
    void copyRefreshToken(OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken, @MappingTarget Oauth2Authorization oauth2Authorization);

    /**
     * 拷贝用户码相关信息（值、签发时间、过期时间、元数据）。
     *
     * @param token               OAuth2 用户码对象
     * @param oauth2Authorization 自定义的 Oauth2Authorization 域对象
     */
    @Mappings({
            @Mapping(target = "userCodeValue", source = "token.tokenValue"),
            @Mapping(target = "userCodeIssuedAt", source = "token.issuedAt"),
            @Mapping(target = "userCodeExpiresAt", source = "token.expiresAt"),
            @Mapping(target = "userCodeMetadata", source = "metadata"),
    })
    void copyUserCode(OAuth2Authorization.Token<OAuth2UserCode> token, @MappingTarget Oauth2Authorization oauth2Authorization);

    /**
     * 拷贝设备码相关信息（值、签发时间、过期时间、元数据）。
     *
     * @param token               OAuth2 设备码对象
     * @param oauth2Authorization 自定义的 Oauth2Authorization 域对象
     */
    @Mappings({
            @Mapping(target = "deviceCodeValue", source = "token.tokenValue"),
            @Mapping(target = "deviceCodeIssuedAt", source = "token.issuedAt"),
            @Mapping(target = "deviceCodeExpiresAt", source = "token.expiresAt"),
            @Mapping(target = "deviceCodeMetadata", source = "metadata"),
    })
    void copyDeviceCode(OAuth2Authorization.Token<OAuth2DeviceCode> token, @MappingTarget Oauth2Authorization oauth2Authorization);

    /**
     * 将自定义的 Oauth2Authorization 域对象反向转换为 Spring Security 的 OAuth2Authorization 对象。
     *
     * @param registeredClient 已注册客户端信息
     * @param authorization    自定义的 Oauth2Authorization 域对象
     * @return Spring Security 的 OAuth2Authorization 对象
     */
    default OAuth2Authorization reverseToAuthorization(RegisteredClient registeredClient, Oauth2Authorization authorization) {
        // 构建基础的 OAuth2Authorization 对象
        OAuth2Authorization.Builder builder = OAuth2Authorization.withRegisteredClient(registeredClient)
                .id(authorization.getId())
                .principalName(authorization.getPrincipalName())
                .authorizationGrantType(new AuthorizationGrantType(authorization.getAuthorizationGrantType()))
                .authorizedScopes(authorization.getAuthorizedScopes())
                .attributes(attributes -> attributes.putAll(authorization.getAttributes()));

        // 设置各种令牌和状态信息
        setState(authorization, builder);
        setAuthorizationCode(authorization, builder);
        setAccessToken(authorization, builder);
        setOidcIdToken(authorization, builder);
        setRefreshToken(authorization, builder);
        setUserCode(authorization, builder);
        setDeviceCode(authorization, builder);

        return builder.build();
    }

    /**
     * 设置设备码（Device Code）相关信息到构建器中。
     *
     * @param authorization 授权域对象
     * @param builder       OAuth2Authorization 构建器
     */
    private void setDeviceCode(Oauth2Authorization authorization, OAuth2Authorization.Builder builder) {
        if (StrUtil.isNotBlank(authorization.getDeviceCodeValue())) {
            Map<String, Object> deviceCodeMetadata = authorization.getDeviceCodeMetadata();
            OAuth2DeviceCode deviceCode = new OAuth2DeviceCode(authorization.getDeviceCodeValue(),
                    authorization.getDeviceCodeIssuedAt(),
                    authorization.getDeviceCodeExpiresAt());
            builder.token(deviceCode, metadata -> metadata.putAll(deviceCodeMetadata));
        }
    }

    /**
     * 设置用户码（User Code）相关信息到构建器中。
     *
     * @param authorization 授权域对象
     * @param builder       OAuth2Authorization 构建器
     */
    private void setUserCode(Oauth2Authorization authorization, OAuth2Authorization.Builder builder) {
        if (StrUtil.isNotBlank(authorization.getUserCodeValue())) {
            Map<String, Object> userCodeMetadata = authorization.getUserCodeMetadata();
            OAuth2UserCode userCode = new OAuth2UserCode(authorization.getUserCodeValue(),
                    authorization.getUserCodeIssuedAt(),
                    authorization.getUserCodeExpiresAt());
            builder.token(userCode, metadata -> metadata.putAll(userCodeMetadata));
        }
    }

    /**
     * 设置刷新令牌（Refresh Token）相关信息到构建器中。
     *
     * @param authorization 授权域对象
     * @param builder       OAuth2Authorization 构建器
     */
    private void setRefreshToken(Oauth2Authorization authorization, OAuth2Authorization.Builder builder) {
        if (StrUtil.isNotBlank(authorization.getRefreshTokenValue())) {
            Map<String, Object> refreshTokenMetadata = authorization.getRefreshTokenMetadata();
            OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(authorization.getRefreshTokenValue(),
                    authorization.getRefreshTokenIssuedAt(),
                    authorization.getRefreshTokenExpiresAt());
            builder.token(refreshToken, metadata -> metadata.putAll(refreshTokenMetadata));
        }
    }

    /**
     * 设置 OIDC ID 令牌相关信息到构建器中。
     *
     * @param authorization 授权域对象
     * @param builder       OAuth2Authorization 构建器
     */
    private void setOidcIdToken(Oauth2Authorization authorization, OAuth2Authorization.Builder builder) {
        if (StrUtil.isNotBlank(authorization.getOidcIdTokenValue())) {
            Map<String, Object> oidcIdTokenMetadata = authorization.getOidcIdTokenMetadata();
            Map<String, Object> claims = (Map<String, Object>) oidcIdTokenMetadata.get(OAuth2Authorization.Token.CLAIMS_METADATA_NAME);
            OidcIdToken oidcIdToken = new OidcIdToken(authorization.getOidcIdTokenValue(),
                    authorization.getOidcIdTokenIssuedAt(),
                    authorization.getOidcIdTokenExpiresAt(),
                    claims);
            builder.token(oidcIdToken, metadata -> metadata.putAll(oidcIdTokenMetadata));
        }
    }

    /**
     * 设置访问令牌（Access Token）相关信息到构建器中。
     *
     * @param authorization 授权域对象
     * @param builder       OAuth2Authorization 构建器
     */
    private void setAccessToken(Oauth2Authorization authorization, OAuth2Authorization.Builder builder) {
        // 添加访问令牌
        String accessTokenValue = authorization.getAccessTokenValue();
        if (StrUtil.isNotBlank(accessTokenValue)) {
            OAuth2AccessToken.TokenType tokenType;
            if (OAuth2AccessToken.TokenType.BEARER.getValue().equals(authorization.getAccessTokenType())) {
                tokenType = OAuth2AccessToken.TokenType.BEARER;
            } else if (OAuth2AccessToken.TokenType.DPOP.getValue().equals(authorization.getAccessTokenType())) {
                tokenType = OAuth2AccessToken.TokenType.DPOP;
            } else {
                tokenType = OAuth2AccessToken.TokenType.BEARER;
            }
            OAuth2AccessToken accessToken = new OAuth2AccessToken(tokenType,
                    accessTokenValue,
                    authorization.getAccessTokenIssuedAt(),
                    authorization.getAccessTokenExpiresAt(),
                    authorization.getAccessTokenScopes());
            builder.token(accessToken, metadata -> metadata.putAll(authorization.getAccessTokenMetadata()));
        }
    }

    /**
     * 设置授权码（Authorization Code）相关信息到构建器中。
     *
     * @param authorization 授权域对象
     * @param builder       OAuth2Authorization 构建器
     */
    private void setAuthorizationCode(Oauth2Authorization authorization, OAuth2Authorization.Builder builder) {
        // 添加授权码
        String authorizationCodeValue = authorization.getAuthorizationCodeValue();
        if (StrUtil.isNotBlank(authorizationCodeValue)) {
            OAuth2AuthorizationCode authorizationCode = new OAuth2AuthorizationCode(authorizationCodeValue,
                    authorization.getAuthorizationCodeIssuedAt(),
                    authorization.getAuthorizationCodeExpiresAt());
            builder.token(authorizationCode, metadata -> metadata.putAll(authorization.getAuthorizationCodeMetadata()));
        }
    }

    /**
     * 设置授权状态（State）相关信息到构建器中。
     *
     * @param authorization 授权域对象
     * @param builder       OAuth2Authorization 构建器
     */
    private void setState(Oauth2Authorization authorization, OAuth2Authorization.Builder builder) {
        // 添加授权状态
        String state = authorization.getState();
        if (StrUtil.isNotBlank(state)) {
            builder.attribute(OAuth2ParameterNames.STATE, state);
        }
    }
}
