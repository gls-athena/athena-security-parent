package com.gls.athena.security.oauth2.client.delegate;

import com.gls.athena.common.bean.security.SocialUser;
import com.gls.athena.security.oauth2.client.config.Oauth2ClientConstants;
import com.gls.athena.security.oauth2.client.config.Oauth2ClientProperties;
import com.gls.athena.security.oauth2.client.provider.SocialLoginProviderManager;
import com.gls.athena.security.oauth2.client.soical.ISocialUserService;
import com.gls.athena.starter.web.util.WebUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 委托OAuth2用户服务类，用于处理OAuth2和OIDC登录流程中的用户加载逻辑。
 * 根据不同的登录方式（OAuth2或OIDC）加载用户信息，并检查社交账号的绑定状态。
 * 如果未绑定则抛出认证异常，并将用户信息暂存到会话中供后续绑定流程使用。
 *
 * @author george
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DelegateOauth2UserService {

    private static final String ERROR_CODE = "social_user_not_bind";
    private static final String ERROR_MESSAGE = "社交用户未绑定，请先绑定社交账号";

    private final DefaultOAuth2UserService oauth2UserService = new DefaultOAuth2UserService();

    private final OidcUserService oidcUserService = new OidcUserService();

    private final Oauth2ClientProperties oauth2ClientProperties;

    private final ISocialUserService socialUserService;

    private final SocialLoginProviderManager providerManager;

    /**
     * 加载OAuth2用户信息。
     *
     * @param userRequest OAuth2用户请求对象，包含客户端注册信息和访问令牌等
     * @return OAuth2User 用户对象
     * @throws OAuth2AuthenticationException 当社交用户未绑定时抛出认证异常
     */
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        // 优先使用providerManager加载用户信息，如果为空则使用oauth2UserService加载
        OAuth2User oauth2User = Optional.ofNullable(providerManager.loadUser(userRequest))
                .orElseGet(() -> oauth2UserService.loadUser(userRequest));
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // 根据注册ID和OAuth2用户信息获取社交用户
        return getSocialUser(registrationId, oauth2User);
    }

    /**
     * 加载OIDC用户信息。
     *
     * @param userRequest OIDC用户请求对象，包含客户端注册信息和ID令牌等
     * @return OidcUser OIDC用户对象
     * @throws OAuth2AuthenticationException 当社交用户未绑定时抛出认证异常
     */
    public OidcUser loadOidcUser(OidcUserRequest userRequest) {
        // 尝试从providerManager加载OIDC用户，如果为空则使用oidcUserService加载
        OidcUser oidcUser = Optional.ofNullable(providerManager.loadOidcUser(userRequest))
                .orElseGet(() -> oidcUserService.loadUser(userRequest));
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // 根据注册ID和OIDC用户信息获取社交用户
        return getSocialUser(registrationId, oidcUser);
    }

    /**
     * 获取社交用户信息。如果用户不存在则创建新用户，若用户未绑定则抛出认证异常。
     *
     * @param registrationId 客户端注册ID，标识不同的社交登录提供商
     * @param oauth2User     OAuth2用户对象，包含从社交平台获取的用户信息
     * @return SocialUser 社交用户对象
     * @throws OAuth2AuthenticationException 当社交用户未绑定时抛出认证异常
     */
    private SocialUser getSocialUser(String registrationId, OAuth2User oauth2User) {
        final SocialUser socialUser = Optional.ofNullable(socialUserService.getSocialUser(registrationId, oauth2User.getName()))
                .orElseGet(() -> createSocialUser(registrationId, oauth2User));
        log.debug("加载社交用户信息, registrationId: {}, userName: {}", registrationId, oauth2User.getName());
        // 检查社交用户的绑定状态，如果未绑定，则存储用户信息到会话并抛出认证异常
        if (!socialUser.isBindStatus() && oauth2ClientProperties.isSocialUserBindRequired()) {
            // 记录警告日志，指示社交用户未绑定
            log.warn("社交用户未绑定, registrationId: {}, userName: {}", registrationId, oauth2User.getName());
            // 将社交用户信息存储到当前会话中，供后续绑定
            WebUtil.getSession()
                    .ifPresent(session -> session.setAttribute(Oauth2ClientConstants.SOCIAL_USER_SESSION_KEY, socialUser));
            // 抛出OAuth2认证异常，指示发生了需要处理的认证错误
            throw new OAuth2AuthenticationException(new OAuth2Error(ERROR_CODE, ERROR_MESSAGE, null));
        }
        // 返回加载的社交用户对象
        return socialUser;
    }

    /**
     * 创建新的社交用户对象并保存。
     *
     * @param registrationId 客户端注册ID，标识不同的社交登录提供商
     * @param oauth2User     OAuth2用户对象，包含从社交平台获取的用户信息
     * @return SocialUser 新创建并保存后的社交用户对象
     */
    private SocialUser createSocialUser(String registrationId, OAuth2User oauth2User) {
        // 如果社交用户不存在，则创建新的社交用户
        SocialUser socialUser = new SocialUser();
        // 设置社交用户的注册ID
        socialUser.setRegistrationId(registrationId);
        // 将OAuth2用户的属性复制到社交用户对象中
        socialUser.setAttributes(oauth2User.getAttributes());
        // 设置社交用户的名称
        socialUser.setName(oauth2User.getName());
        // 保存新的社交用户对象，并返回保存后的对象
        return socialUserService.saveSocialUser(socialUser);
    }

}
