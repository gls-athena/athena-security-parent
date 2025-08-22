package com.gls.athena.security.oauth2.client.delegate;

import com.gls.athena.security.oauth2.client.config.Oauth2ClientProperties;
import com.gls.athena.security.oauth2.client.provider.SocialLoginProviderManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

/**
 * 自定义的 OAuth2 授权请求解析器，用于在标准 OAuth2 授权请求流程中插入自定义逻辑。
 * 该类通过委托模式包装了 Spring Security 的默认解析器，并允许通过 SocialLoginProvider 对授权请求进行定制。
 *
 * @author george
 */
@Slf4j
@Component
public class DelegateOauth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private static final String REGISTRATION_ID_URI_VARIABLE_NAME = "registrationId";

    /**
     * 委托的默认 OAuth2 授权请求解析器
     */
    private final DefaultOAuth2AuthorizationRequestResolver delegate;

    /**
     * 客户端注册信息仓库，用于根据 registrationId 查找客户端配置
     */
    private final ClientRegistrationRepository clientRegistrationRepository;

    /**
     * 请求匹配器，用于匹配 OAuth2 授权请求路径
     */
    private final RequestMatcher authorizationRequestMatcher;

    /**
     * 社交登录提供者，用于自定义授权请求构建过程
     */
    private final SocialLoginProviderManager providerManager;

    /**
     * 构造函数
     *
     * @param clientRegistrationRepository 客户端注册信息仓库
     * @param providerManager              社交登录提供者，用于自定义授权请求
     * @param properties                   OAuth2 客户端属性配置
     */
    public DelegateOauth2AuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository,
                                                      SocialLoginProviderManager providerManager,
                                                      Oauth2ClientProperties properties) {
        this.delegate = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, properties.getAuthorizationRequestBaseUri());
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.authorizationRequestMatcher = PathPatternRequestMatcher.withDefaults()
                .matcher(properties.getAuthorizationRequestBaseUri() + "/{" + REGISTRATION_ID_URI_VARIABLE_NAME + "}");
        this.providerManager = providerManager;
    }

    /**
     * 解析 OAuth2 授权请求
     *
     * @param request 当前 HTTP 请求
     * @return OAuth2 授权请求对象，如果无法解析则返回 null
     */
    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        // 获取客户端注册ID
        String clientRegistrationId = getClientRegistrationId(request);
        // 自定义解析处理
        customizeResolve(request, clientRegistrationId);
        // 委托给代理对象进行实际解析
        return delegate.resolve(request);
    }

    /**
     * 根据指定的客户端注册 ID 解析 OAuth2 授权请求
     *
     * @param request              当前 HTTP 请求
     * @param clientRegistrationId 客户端注册 ID
     * @return OAuth2 授权请求对象，如果无法解析则返回 null
     */
    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        // 自定义解析处理
        customizeResolve(request, clientRegistrationId);
        // 委托给代理对象进行实际的解析操作
        return delegate.resolve(request, clientRegistrationId);
    }

    /**
     * 自定义解析逻辑，在委托解析前设置自定义的授权请求构建器
     *
     * @param request              当前 HTTP 请求
     * @param clientRegistrationId 客户端注册 ID
     */
    private void customizeResolve(HttpServletRequest request, String clientRegistrationId) {
        if (clientRegistrationId == null) {
            return;
        }
        // 获取客户端注册信息
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(clientRegistrationId);
        log.debug("客户端注册信息：{}", clientRegistration);
        // 设置授权请求自定义器，用于定制授权请求的构建过程
        delegate.setAuthorizationRequestCustomizer(builder -> providerManager.customizeAuthorizationRequest(builder, clientRegistration, request));
    }

    /**
     * 从请求中提取客户端注册 ID
     *
     * @param request 当前 HTTP 请求
     * @return 客户端注册 ID，如果请求不匹配则返回 null
     */
    private String getClientRegistrationId(HttpServletRequest request) {
        // 检查请求是否匹配授权请求模式
        if (this.authorizationRequestMatcher.matches(request)) {
            // 从匹配结果中提取注册 ID 变量
            return this.authorizationRequestMatcher.matcher(request)
                    .getVariables()
                    .get(REGISTRATION_ID_URI_VARIABLE_NAME);
        }
        return null;
    }

}
