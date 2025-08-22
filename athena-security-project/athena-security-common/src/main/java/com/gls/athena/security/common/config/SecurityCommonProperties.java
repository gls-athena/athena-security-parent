package com.gls.athena.security.common.config;

import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 安全核心配置属性类
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".security.common")
public class SecurityCommonProperties extends BaseProperties {
    /**
     * 登录页面
     */
    private String loginPage = "/login.html";

    /**
     * 忽略的URL
     */
    private String[] ignoreUrls = new String[]{"/login", "/logout", "/captcha", "/error"};

    /**
     * 静态资源忽略的URL
     */
    private String[] staticUrls = new String[]{"/css/**", "/fonts/**", "/img/**", "/images/**", "/js/**", "/favicon.ico", "/webjars/**", "/swagger-ui/**", "/v3/api-docs/**", "/actuator/**"};

}
