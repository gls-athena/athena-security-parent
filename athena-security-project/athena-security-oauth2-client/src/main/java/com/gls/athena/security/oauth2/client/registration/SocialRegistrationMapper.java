package com.gls.athena.security.oauth2.client.registration;

import com.gls.athena.security.oauth2.client.config.Oauth2ClientConstants;
import com.gls.athena.security.oauth2.client.provider.SocialLoginProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * 社交登录属性映射器，用于将配置的OAuth2客户端注册信息转换为ClientRegistration对象。
 * 该类支持从Spring Boot标准配置、通用OAuth2提供者以及自定义社交登录提供者中构建ClientRegistration。
 *
 * @author george
 */
@RequiredArgsConstructor
public class SocialRegistrationMapper {

    private final OAuth2ClientProperties properties;

    private final SocialLoginProviderManager providerManager;

    /**
     * 获取所有客户端注册信息的映射表。
     *
     * @return 以注册ID为键，ClientRegistration为值的映射表
     */
    public Map<String, ClientRegistration> getClientRegistrations() {
        // 将配置属性中的注册信息转换为ClientRegistration对象映射
        return this.properties.getRegistration().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry ->
                        getClientRegistration(entry.getKey(), entry.getValue())));
    }

    /**
     * 根据给定的注册ID和注册配置创建ClientRegistration对象。
     *
     * @param registrationId 客户端注册ID
     * @param registration   OAuth2客户端注册配置
     * @return 构建完成的ClientRegistration对象
     */
    private ClientRegistration getClientRegistration(String registrationId, OAuth2ClientProperties.Registration registration) {
        // 获取提供者ID
        String providerId = getProviderId(registrationId, registration.getProvider());

        // 根据提供者获取ClientRegistration构建器
        ClientRegistration.Builder builder = getBuilderByProvider(registrationId, providerId);

        // 使用属性映射器将注册配置映射到构建器
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        map.from(registration::getClientId).to(builder::clientId);
        map.from(registration::getClientSecret).to(builder::clientSecret);
        map.from(registration::getClientAuthenticationMethod)
                .as(ClientAuthenticationMethod::new)
                .to(builder::clientAuthenticationMethod);
        map.from(registration::getAuthorizationGrantType)
                .as(AuthorizationGrantType::new)
                .to(builder::authorizationGrantType);
        map.from(registration::getRedirectUri).to(builder::redirectUri);
        map.from(registration::getScope).as(StringUtils::toStringArray).to(builder::scope);
        map.from(registration::getClientName).to(builder::clientName);

        // 构建并返回ClientRegistration对象
        return builder.build();
    }

    /**
     * 根据提供者ID获取ClientRegistration.Builder实例。
     * 按照优先级依次尝试从通用OAuth2提供者、社交登录提供者以及配置属性中获取构建器。
     *
     * @param registrationId 客户端注册ID
     * @param providerId     提供者ID
     * @return ClientRegistration.Builder实例，如果无法构建则返回null
     */
    private ClientRegistration.Builder getBuilderByProvider(String registrationId, String providerId) {
        // 获取指定提供者的配置属性
        OAuth2ClientProperties.Provider provider = this.properties.getProvider().get(providerId);

        // 按优先级顺序尝试获取Builder实例
        ClientRegistration.Builder builder = getBuilderFromCommon(registrationId, providerId, provider);
        if (builder == null) {
            builder = getBuilderFromSocial(registrationId, providerId, provider);
        }
        if (builder == null) {
            builder = getBuilderFromProperties(registrationId, providerId, provider);
        }

        // 如果所有途径都无法获取Builder，则抛出异常
        if (builder == null) {
            throw new IllegalArgumentException("No provider found for " + providerId);
        }
        return builder;
    }

    /**
     * 从配置属性中获取ClientRegistration.Builder实例。
     * 如果提供者配置包含issuerUri，则从发行者位置获取；否则直接根据属性构建。
     *
     * @param registrationId 客户端注册ID
     * @param providerId     提供者ID
     * @param provider       OAuth2提供者配置
     * @return ClientRegistration.Builder实例，如果提供者为空则返回null
     */
    private ClientRegistration.Builder getBuilderFromProperties(String registrationId, String providerId, OAuth2ClientProperties.Provider provider) {
        if (provider == null) {
            return null;
        }
        // 如果提供者配置了issuerUri，则从发行者位置获取构建器
        if (provider.getIssuerUri() != null) {
            // 从发行者位置获取构建器
            ClientRegistration.Builder builder = ClientRegistrations.fromIssuerLocation(provider.getIssuerUri())
                    .registrationId(registrationId);
            return copyProviderToBuilder(builder, provider);
        }
        // 否则直接根据提供者属性构建构建器
        // 从提供者属性获取构建器
        ClientRegistration.Builder builder = ClientRegistration.withRegistrationId(registrationId);
        return copyProviderToBuilder(builder, provider);
    }

    /**
     * 从Spring Security内置的通用OAuth2提供者中获取ClientRegistration.Builder实例。
     *
     * @param registrationId 客户端注册ID
     * @param providerId     提供者ID（转换为大写进行匹配）
     * @param provider       OAuth2提供者配置
     * @return ClientRegistration.Builder实例，如果匹配不到通用提供者则返回null
     */
    private ClientRegistration.Builder getBuilderFromCommon(String registrationId, String providerId, OAuth2ClientProperties.Provider provider) {
        try {
            // 尝试根据提供者ID获取对应的通用OAuth2提供者配置
            CommonOAuth2Provider commonProvider = CommonOAuth2Provider.valueOf(providerId.toUpperCase());
            ClientRegistration.Builder builder = commonProvider.getBuilder(registrationId);
            // 设置提供者ID
            builder.providerConfigurationMetadata(Map.of(Oauth2ClientConstants.PROVIDER_ID, providerId));
            // 如果提供了自定义配置，则将其复制到构建器中
            if (provider != null) {
                copyProviderToBuilder(builder, provider);
            }
            return builder;
        } catch (Exception e) {
            // 如果找不到对应的通用提供者，返回null
            return null;
        }

    }

    /**
     * 从自定义社交登录提供者管理器中获取ClientRegistration.Builder实例。
     *
     * @param registrationId 客户端注册ID
     * @param providerId     提供者ID
     * @param provider       OAuth2提供者配置
     * @return ClientRegistration.Builder实例，如果社交提供者无法处理则返回null
     */
    private ClientRegistration.Builder getBuilderFromSocial(String registrationId, String providerId, OAuth2ClientProperties.Provider provider) {
        try {
            // 尝试从提供者管理器获取客户端注册构建器
            ClientRegistration.Builder builder = providerManager.getClientRegistrationBuilder(registrationId, providerId);
            // 如果提供者配置存在，则将其属性复制到构建器中
            if (provider != null) {
                copyProviderToBuilder(builder, provider);
            }
            return builder;
        } catch (Exception e) {
            // 发生异常时返回null，表示无法处理该社交提供者
            return null;
        }

    }

    /**
     * 将OAuth2提供者配置复制到ClientRegistration.Builder中。
     *
     * @param builder  ClientRegistration.Builder实例
     * @param provider OAuth2提供者配置
     * @return 配置完成的ClientRegistration.Builder实例
     */
    private ClientRegistration.Builder copyProviderToBuilder(ClientRegistration.Builder builder, OAuth2ClientProperties.Provider provider) {
        // 创建属性映射器，只映射非空属性
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();

        // 映射认证相关的URI配置
        map.from(provider::getAuthorizationUri).to(builder::authorizationUri);
        map.from(provider::getTokenUri).to(builder::tokenUri);
        map.from(provider::getUserInfoUri).to(builder::userInfoUri);

        // 映射用户信息认证方法配置
        map.from(provider::getUserInfoAuthenticationMethod)
                .as(AuthenticationMethod::new)
                .to(builder::userInfoAuthenticationMethod);

        // 映射JWK设置URI和用户名属性配置
        map.from(provider::getJwkSetUri).to(builder::jwkSetUri);
        map.from(provider::getUserNameAttribute).to(builder::userNameAttributeName);

        return builder;
    }

    /**
     * 获取提供者ID。如果提供者未指定，则使用注册ID作为默认值。
     *
     * @param registrationId 客户端注册ID
     * @param provider       提供者名称
     * @return 提供者ID
     */
    private String getProviderId(String registrationId, String provider) {
        // 如果提供者未指定，则使用注册ID作为默认值
        if (provider == null) {
            return registrationId;
        } else {
            return provider;
        }
    }

}
