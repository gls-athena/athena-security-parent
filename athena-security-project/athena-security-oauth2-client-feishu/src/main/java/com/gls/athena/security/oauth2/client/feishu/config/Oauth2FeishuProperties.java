package com.gls.athena.security.oauth2.client.feishu.config;

import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 飞书OAuth2客户端配置属性类
 * <p>
 * 该类用于配置飞书OAuth2认证相关的各种URI地址，包括授权码获取、令牌获取、
 * 用户信息获取以及应用访问令牌获取等接口地址。
 * <p>
 * 配置前缀为：{@value IConstants#BASE_PROPERTIES_PREFIX}.security.oauth2.client.feishu
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".security.oauth2.client.feishu")
public class Oauth2FeishuProperties extends BaseProperties {

    /**
     * 认证服务提供商ID
     */
    private String providerId = "feishu";

    /**
     * 认证服务提供商名称
     */
    private String providerName = "飞书";

    /**
     * 飞书授权码获取URI
     * 用于引导用户到飞书授权页面获取授权码
     */
    private String authorizationUri = "https://open.feishu.cn/open-apis/authen/v1/authorize";

    /**
     * 飞书访问令牌获取URI
     * 用于通过授权码换取访问令牌和刷新令牌
     */
    private String tokenUri = "https://open.feishu.cn/open-apis/authen/v1/oidc/access_token";

    /**
     * 飞书用户信息获取URI
     * 用于通过访问令牌获取用户基本信息
     */
    private String userInfoUri = "https://open.feishu.cn/open-apis/authen/v1/user_info";

    /**
     * 飞书应用访问令牌获取URI
     * 用于获取应用级别的访问令牌，用于服务端API调用
     */
    private String appAccessTokenUri = "https://open.feishu.cn/open-apis/auth/v3/app_access_token/internal";

    /**
     * 用户名属性名称
     * 用于指定从用户信息中提取用户名的属性名称，默认为"unionId"
     */
    private String userNameAttributeName = "unionId";

    /**
     * 应用访问令牌缓存名称
     * 用于指定应用访问令牌缓存的名称，默认为"feishu:app_access_token"
     */
    private String appAccessTokenCacheName = "feishu:app_access_token";
}
