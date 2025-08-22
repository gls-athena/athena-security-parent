package com.gls.athena.security.oauth2.client.config;

import com.gls.athena.security.oauth2.client.provider.SocialLoginProviderManager;
import com.gls.athena.security.oauth2.client.registration.SocialRegistrationMapper;
import com.gls.athena.security.oauth2.client.soical.ISocialUserService;
import com.gls.athena.security.oauth2.client.soical.InMemorySocialUserServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

/**
 * OAuth2客户端配置类
 * <p>
 * 该类用于配置OAuth2客户端的相关参数和行为，作为Spring配置类使用。
 * 通过@Configuration注解标识为配置类，会被Spring容器自动扫描和加载。
 *
 * @author george
 */
@Configuration
@EnableConfigurationProperties(OAuth2ClientProperties.class)
public class Oauth2ClientConfig {

    /**
     * 创建SocialUserService Bean实例
     *
     * <p>该方法用于创建SocialUserService的Bean实例，当容器中不存在SocialUserService类型的Bean时才会创建。
     * 使用InMemorySocialUserServiceImpl作为默认实现。</p>
     *
     * @return ISocialUserService SocialUserService实例
     */
    @Bean
    @ConditionalOnMissingBean(ISocialUserService.class)
    public ISocialUserService socialUserService() {
        return new InMemorySocialUserServiceImpl();
    }

    /**
     * 创建客户端注册仓库的Bean
     *
     * <p>该方法用于创建一个基于内存的客户端注册仓库实例。只有当配置属性
     * athena.security.client.type 的值为 "InMemory" 或未设置时才会生效。</p>
     *
     * @param socialRegistrationMapper 社交注册映射器，用于获取客户端注册信息
     * @return ClientRegistrationRepository 客户端注册仓库实例
     */
    @Bean
    @ConditionalOnMissingBean(ClientRegistrationRepository.class)
    public ClientRegistrationRepository clientRegistrationRepository(SocialRegistrationMapper socialRegistrationMapper) {
        // 创建内存中的客户端注册仓库
        return new InMemoryClientRegistrationRepository(socialRegistrationMapper.getClientRegistrations());
    }

    /**
     * 创建社交注册映射器的Bean
     *
     * <p>该方法用于创建SocialRegistrationMapper实例，用于将OAuth2客户端属性和社交登录提供商管理器
     * 映射为客户端注册信息列表。仅在容器中不存在SocialRegistrationMapper类型的Bean时才会创建。</p>
     *
     * @param oauth2ClientProperties     OAuth2客户端属性配置对象
     * @param socialLoginProviderManager 社交登录提供商管理器
     * @return SocialRegistrationMapper 社交注册映射器实例
     */
    @Bean
    @ConditionalOnMissingBean(SocialRegistrationMapper.class)
    public SocialRegistrationMapper socialRegistrationMapper(OAuth2ClientProperties oauth2ClientProperties,
                                                             SocialLoginProviderManager socialLoginProviderManager) {
        // 创建社交注册映射器
        return new SocialRegistrationMapper(oauth2ClientProperties, socialLoginProviderManager);
    }

}
