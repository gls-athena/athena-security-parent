package com.gls.athena.security.oauth2.authorization.server.authentication;

import cn.hutool.core.util.StrUtil;
import com.gls.athena.security.oauth2.authorization.server.support.Oauth2EndpointUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 抽象自定义认证转换器基类，提供通用的OAuth2认证转换逻辑。
 * <p>
 * 该抽象类封装了三种认证模式（邮箱、手机、密码）的通用处理逻辑，
 * 包括参数提取、验证、错误处理等，子类只需要实现特定的授权类型检查和Token创建逻辑。
 * </p>
 *
 * @author george
 */
public abstract class AbstractCustomAuthenticationConverter implements AuthenticationConverter {

    /**
     * 将HTTP请求转换为认证对象的通用实现。
     * <p>
     * 该方法提供了完整的OAuth2认证转换流程：
     * <ol>
     *   <li>提取表单参数</li>
     *   <li>检查授权类型是否匹配</li>
     *   <li>验证必要参数</li>
     *   <li>构造并返回认证Token</li>
     * </ol>
     * </p>
     *
     * @param request HTTP请求对象
     * @return 认证Token对象，如果授权类型不匹配则返回null
     */
    @Override
    public final Authentication convert(HttpServletRequest request) {
        // 获取请求中的表单参数
        MultiValueMap<String, String> parameters = Oauth2EndpointUtil.getFormParameters(request);
        String grantType = parameters.getFirst(OAuth2ParameterNames.GRANT_TYPE);

        // 判断授权类型是否匹配，如果不匹配则直接返回null
        if (!isGrantTypeSupported(grantType)) {
            return null;
        }

        // 获取当前客户端的认证信息
        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();

        // 验证用户标识参数（用户名/邮箱/手机号）
        String userIdentifier = parameters.getFirst(getUserIdentifierParameterName());
        if (StrUtil.isBlank(userIdentifier)) {
            Oauth2EndpointUtil.throwError(OAuth2ErrorCodes.INVALID_REQUEST, getUserIdentifierParameterName(),
                    Oauth2EndpointUtil.ACCESS_TOKEN_REQUEST_ERROR_URI);
        }

        // 验证用户凭证参数（密码/验证码）
        String credential = parameters.getFirst(getCredentialParameterName());
        if (StrUtil.isBlank(credential)) {
            Oauth2EndpointUtil.throwError(OAuth2ErrorCodes.INVALID_REQUEST, getCredentialParameterName(),
                    Oauth2EndpointUtil.ACCESS_TOKEN_REQUEST_ERROR_URI);
        }

        // 检查scope参数是否为空，如果为空则抛出错误
        String scopes = parameters.getFirst(OAuth2ParameterNames.SCOPE);
        if (StrUtil.isBlank(scopes)) {
            Oauth2EndpointUtil.throwError(OAuth2ErrorCodes.INVALID_SCOPE, OAuth2ParameterNames.SCOPE,
                    Oauth2EndpointUtil.ACCESS_TOKEN_REQUEST_ERROR_URI);
        }

        // 解析scope参数为Set集合
        Set<String> scope = Oauth2EndpointUtil.parseScopes(scopes);

        // 构造额外参数映射表，排除标准OAuth2参数
        Map<String, Object> additionalParameters = buildAdditionalParameters(parameters);
        // 添加DPoP参数
        Oauth2EndpointUtil.validateAndAddDPoPParametersIfAvailable(request, additionalParameters);
        // 由子类创建具体的认证Token
        return createAuthenticationToken(clientPrincipal, userIdentifier, credential, scope, additionalParameters);
    }

    /**
     * 构造额外参数映射表，排除标准OAuth2参数。
     *
     * @param parameters 请求参数
     * @return 额外参数映射表
     */
    private Map<String, Object> buildAdditionalParameters(MultiValueMap<String, String> parameters) {
        Map<String, Object> additionalParameters = new HashMap<>();
        parameters.forEach((key, value) -> {
            if (!key.equals(OAuth2ParameterNames.GRANT_TYPE) && !key.equals(OAuth2ParameterNames.CLIENT_ID)
                    && !key.equals(OAuth2ParameterNames.CODE) && !key.equals(OAuth2ParameterNames.REDIRECT_URI)) {
                additionalParameters.put(key, (value.size() == 1) ? value.getFirst() : value.toArray(new String[0]));
            }
        });
        return additionalParameters;
    }

    /**
     * 检查授权类型是否受支持。
     * 子类需要实现此方法以指定支持的授权类型。
     *
     * @param grantType 授权类型
     * @return 如果支持该授权类型返回true，否则返回false
     */
    protected abstract boolean isGrantTypeSupported(String grantType);

    /**
     * 获取用户标识参数名称。
     * 子类需要实现此方法以指定用户标识参数名（username/email/mobile）。
     *
     * @return 用户标识参数名称
     */
    protected abstract String getUserIdentifierParameterName();

    /**
     * 获取用户凭证参数名称。
     * 子类需要实现此方法以指定用户凭证参数名（password/code）。
     *
     * @return 用户凭证参数名称
     */
    protected abstract String getCredentialParameterName();

    /**
     * 创建具体的认证Token对象。
     * 子类需要实现此方法以创建对应类型的认证Token。
     *
     * @param clientPrincipal      客户端主体认证信息
     * @param userIdentifier       用户标识（用户名/邮箱/手机号）
     * @param credential           用户凭证（密码/验证码）
     * @param scopes               请求的作用域集合
     * @param additionalParameters 附加参数映射
     * @return 具体的认证Token对象
     */
    protected abstract Authentication createAuthenticationToken(
            Authentication clientPrincipal,
            String userIdentifier,
            String credential,
            Set<String> scopes,
            Map<String, Object> additionalParameters
    );
}

