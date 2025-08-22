package com.gls.athena.security.oauth2.client.config;

import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;

/**
 * OAuth2客户端配置属性类
 * <p>
 * 该类用于封装OAuth2客户端的相关配置属性，通过Spring Boot的配置属性绑定机制，
 * 可以自动从配置文件中读取以指定前缀开头的配置项并注入到对应的属性中。
 * </p>
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".security.oauth2.client")
public class Oauth2ClientProperties extends BaseProperties {

    /**
     * 授权请求的基础URI
     * <p>
     * 用于构建OAuth2授权请求的URL路径前缀，默认使用Spring Security OAuth2客户端提供的默认值。
     * </p>
     */
    private String authorizationRequestBaseUri = OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;
    /**
     * 是否需要绑定社交用户
     * <p>
     * 用于控制在OAuth2登录成功后是否强制要求用户绑定社交账号。
     * 默认为true，表示需要绑定社交用户。
     * </p>
     */
    private boolean socialUserBindRequired = true;
}


