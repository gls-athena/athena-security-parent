package com.gls.athena.security.oauth2.authorization.server.customizer;

import cn.hutool.core.bean.BeanUtil;
import com.gls.athena.common.bean.security.SocialUser;
import org.springframework.security.config.Customizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OidcUserInfoEndpointConfigurer;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.stereotype.Component;

import java.security.Principal;

/**
 * OIDC用户信息端点自定义器
 * 用于自定义OIDC用户信息端点的配置
 *
 * @author george
 */
@Component
public class OidcUserInfoEndpointCustomizer implements Customizer<OidcUserInfoEndpointConfigurer> {

    /**
     * 自定义OIDC用户信息端点配置
     * 设置用户信息映射器，用于将认证上下文映射为OIDC用户信息
     *
     * @param configurer OIDC用户信息端点配置器，用于配置用户信息端点的相关参数
     */
    @Override
    public void customize(OidcUserInfoEndpointConfigurer configurer) {
        configurer.userInfoMapper(this::mapUserInfo);
    }

    /**
     * 将OIDC用户信息认证上下文映射为OIDC用户信息对象
     * 从认证授权信息中提取用户主体信息，并转换为OIDC用户信息格式
     *
     * @param context OIDC用户信息认证上下文，包含当前的授权信息
     * @return OidcUserInfo OIDC用户信息对象
     */
    private OidcUserInfo mapUserInfo(OidcUserInfoAuthenticationContext context) {
        // 从认证上下文中获取OAuth2授权信息
        OAuth2Authorization oauth2Authorization = context.getAuthorization();
        // 从授权信息中获取认证对象
        Authentication authentication = oauth2Authorization.getAttribute(Principal.class.getName());
        assert authentication != null;
        // 获取认证主体对象
        Object principal = authentication.getPrincipal();
        // 根据主体类型转换为OIDC用户信息
        if (principal instanceof SocialUser socialUser) {
            return new OidcUserInfo(BeanUtil.beanToMap(socialUser.getUser()));
        }
        return new OidcUserInfo(BeanUtil.beanToMap(principal));
    }
}

