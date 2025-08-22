package com.gls.athena.security.oauth2.client.registration;

import com.gls.athena.security.oauth2.client.config.Oauth2ClientConstants;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 社交登录客户端注册配置类
 * <p>
 * 该类用于配置OAuth2客户端注册所需的各种参数，包括客户端基本信息、认证方式、授权模式、
 * 重定向URI、作用域以及各种OAuth2端点URL等配置信息。
 *
 * @author george
 */
@Data
@Accessors(chain = true)
public class SocialRegistration implements Serializable {

    /**
     * 注册ID，用于唯一标识一个客户端注册配置
     */
    private final String registrationId;

    /**
     * 客户端ID，由授权服务器分配给客户端的唯一标识符
     */
    private String clientId;

    /**
     * 客户端密钥，用于客户端身份验证的机密信息
     */
    private String clientSecret;

    /**
     * 客户端认证方法，默认使用BASIC认证方式
     */
    private ClientAuthenticationMethod clientAuthenticationMethod = ClientAuthenticationMethod.CLIENT_SECRET_BASIC;

    /**
     * 授权授予类型，默认使用授权码模式
     */
    private AuthorizationGrantType authorizationGrantType = AuthorizationGrantType.AUTHORIZATION_CODE;

    /**
     * 重定向URI模板，用于OAuth2授权流程完成后的回调地址
     */
    private String redirectUri = "{baseUrl}/{action}/oauth2/code/{registrationId}";

    /**
     * 请求的权限作用域集合
     */
    private Set<String> scopes;

    /**
     * 授权端点URI，用于获取授权码的地址
     */
    private String authorizationUri;

    /**
     * 令牌端点URI，用于获取访问令牌的地址
     */
    private String tokenUri;

    /**
     * 用户信息端点URI，用于获取用户信息的地址
     */
    private String userInfoUri;

    /**
     * 用户信息端点认证方法，默认使用HTTP头部认证
     */
    private AuthenticationMethod userInfoAuthenticationMethod = AuthenticationMethod.HEADER;

    /**
     * 用户名属性名称，用于指定用户信息中的用户名字段
     */
    private String userNameAttributeName;

    /**
     * JWK Set URI，用于获取JSON Web Key集合的地址
     */
    private String jwkSetUri;

    /**
     * 发行方URI，用于标识JWT的发行方
     */
    private String issuerUri;

    /**
     * 配置元数据映射，存储额外的配置信息
     */
    private Map<String, Object> metadata = new HashMap<>();

    /**
     * 客户端显示名称
     */
    private String clientName;

    /**
     * 客户端设置信息
     */
    private ClientRegistration.ClientSettings clientSettings = ClientRegistration.ClientSettings.builder().build();

    /**
     * 认证服务提供商ID
     */
    private String providerId;

    /**
     * 认证服务提供商名称
     */
    private String providerName;

    /**
     * 将当前客户端注册信息转换为构建器对象
     * <p>
     * 该方法创建一个新的ClientRegistration.Builder实例，并将当前对象的所有属性值设置到构建器中。
     * 对于非空属性，使用Optional的方式安全地设置到构建器中。
     * 配置元数据会被复制并添加特定的提供者信息后设置到构建器中。
     *
     * @return ClientRegistration.Builder 构建器对象，包含了当前客户端注册的所有配置信息
     */
    public ClientRegistration.Builder toBuilder() {
        // 创建一个新的ClientRegistration构建器
        ClientRegistration.Builder builder = ClientRegistration.withRegistrationId(registrationId);

        // 使用Optional安全地设置各个属性
        Optional.ofNullable(clientId).ifPresent(builder::clientId);
        Optional.ofNullable(clientSecret).ifPresent(builder::clientSecret);
        Optional.ofNullable(clientAuthenticationMethod).ifPresent(builder::clientAuthenticationMethod);
        Optional.ofNullable(authorizationGrantType).ifPresent(builder::authorizationGrantType);
        Optional.ofNullable(redirectUri).ifPresent(builder::redirectUri);
        Optional.ofNullable(scopes).ifPresent(builder::scope);
        Optional.ofNullable(authorizationUri).ifPresent(builder::authorizationUri);
        Optional.ofNullable(tokenUri).ifPresent(builder::tokenUri);
        Optional.ofNullable(userInfoUri).ifPresent(builder::userInfoUri);
        Optional.ofNullable(userInfoAuthenticationMethod).ifPresent(builder::userInfoAuthenticationMethod);
        Optional.ofNullable(userNameAttributeName).ifPresent(builder::userNameAttributeName);
        Optional.ofNullable(jwkSetUri).ifPresent(builder::jwkSetUri);
        Optional.ofNullable(issuerUri).ifPresent(builder::issuerUri);
        Optional.ofNullable(clientName).ifPresent(builder::clientName);
        Optional.ofNullable(clientSettings).ifPresent(builder::clientSettings);

        // 复制现有元数据并添加自定义提供者信息
        Map<String, Object> configurationMetadata = new HashMap<>(this.metadata);
        Optional.ofNullable(providerId).ifPresent(providerId -> configurationMetadata.put(Oauth2ClientConstants.PROVIDER_ID, providerId));
        Optional.ofNullable(providerName).ifPresent(providerName -> configurationMetadata.put(Oauth2ClientConstants.PROVIDER_NAME, providerName));

        // 设置最终的配置元数据并返回构建器
        return builder.providerConfigurationMetadata(configurationMetadata);
    }

}
