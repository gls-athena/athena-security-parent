package com.gls.athena.security.web.customizer;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.stereotype.Component;

/**
 * CSRF自定义器
 * 用于配置和自定义CSRF（跨站请求伪造）保护功能
 *
 * <p><b>为何禁用 CSRF：</b><br>
 * 本系统采用无状态 RESTful API + Bearer Token（JWT/Opaque Token）认证模式。
 * CSRF 攻击依赖浏览器自动携带 Cookie/Session，而 Bearer Token 由客户端在
 * {@code Authorization} 请求头中显式传递，浏览器不会自动发送，因此 CSRF 攻击无法生效。
 * 在此场景下禁用 CSRF 保护是安全且合理的。
 * </p>
 * <p>
 * 若项目后续引入基于 Cookie 的 Session 认证（如传统 MVC 页面），需重新评估并启用 CSRF 保护。
 * </p>
 *
 * @author george
 */
@Component
public class CsrfCustomizer
        implements Customizer<CsrfConfigurer<HttpSecurity>> {

    /**
     * 禁用 CSRF 保护。
     * <p>
     * 适用于无状态 REST API 场景，客户端通过 {@code Authorization: Bearer <token>} 头传递凭证，
     * 浏览器不会自动附加，故不存在 CSRF 风险。
     * </p>
     *
     * @param configurer CSRF配置器
     */
    @Override
    public void customize(CsrfConfigurer<HttpSecurity> configurer) {
        configurer.disable();
    }
}


