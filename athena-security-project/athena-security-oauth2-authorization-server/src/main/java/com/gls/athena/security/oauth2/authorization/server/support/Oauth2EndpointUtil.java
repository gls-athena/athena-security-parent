package com.gls.athena.security.oauth2.authorization.server.support;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.endpoint.PkceParameterNames;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * OAuth2 端点工具类，提供与 OAuth2 请求参数处理、验证和转换相关的辅助方法。
 *
 * @author george
 */
@UtilityClass
public class Oauth2EndpointUtil {

    /**
     * 访问令牌请求错误的 RFC 6749 规范 URI。
     */
    public final String ACCESS_TOKEN_REQUEST_ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2";

    /**
     * 从 HTTP 请求中提取表单参数（即非查询参数）。
     *
     * @param request HTTP 请求对象
     * @return 包含所有表单参数的 MultiValueMap
     */
    public MultiValueMap<String, String> getFormParameters(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameterMap.forEach((key, values) -> {
            String queryString = StringUtils.hasText(request.getQueryString()) ? request.getQueryString() : "";
            // 如果参数不在查询字符串中，则认为是表单参数
            if (!queryString.contains(key)) {
                for (String value : values) {
                    parameters.add(key, value);
                }
            }
        });
        return parameters;
    }

    /**
     * 从 HTTP 请求中提取查询参数。
     *
     * @param request HTTP 请求对象
     * @return 包含所有查询参数的 MultiValueMap
     */
    public MultiValueMap<String, String> getQueryParameters(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameterMap.forEach((key, values) -> {
            String queryString = StringUtils.hasText(request.getQueryString()) ? request.getQueryString() : "";
            // 如果参数在查询字符串中，则认为是查询参数
            if (queryString.contains(key)) {
                for (String value : values) {
                    parameters.add(key, value);
                }
            }
        });
        return parameters;
    }

    /**
     * 如果当前请求匹配授权码模式（Authorization Code Grant），则提取请求参数。
     *
     * @param request    HTTP 请求对象
     * @param exclusions 需要排除的参数名称数组
     * @return 包含提取参数的 Map，如果请求不匹配则返回空 Map
     */
    public Map<String, Object> getParametersIfMatchesAuthorizationCodeGrantRequest(HttpServletRequest request,
                                                                                   String... exclusions) {
        if (!matchesAuthorizationCodeGrantRequest(request)) {
            return Collections.emptyMap();
        }
        MultiValueMap<String, String> multiValueParameters = "GET".equals(request.getMethod())
                ? getQueryParameters(request) : getFormParameters(request);
        for (String exclusion : exclusions) {
            multiValueParameters.remove(exclusion);
        }

        Map<String, Object> parameters = new HashMap<>();
        multiValueParameters.forEach(
                (key, value) -> parameters.put(key, (value.size() == 1) ? value.get(0) : value.toArray(new String[0])));

        return parameters;
    }

    /**
     * 判断当前请求是否为授权码模式（Authorization Code Grant）请求。
     *
     * @param request HTTP 请求对象
     * @return 如果是授权码模式请求返回 true，否则返回 false
     */
    public boolean matchesAuthorizationCodeGrantRequest(HttpServletRequest request) {
        return AuthorizationGrantType.AUTHORIZATION_CODE.getValue()
                .equals(request.getParameter(OAuth2ParameterNames.GRANT_TYPE))
                && request.getParameter(OAuth2ParameterNames.CODE) != null;
    }

    /**
     * 判断当前请求是否为 PKCE 模式的令牌请求。
     *
     * @param request HTTP 请求对象
     * @return 如果是 PKCE 模式的令牌请求返回 true，否则返回 false
     */
    public boolean matchesPkceTokenRequest(HttpServletRequest request) {
        return matchesAuthorizationCodeGrantRequest(request)
                && request.getParameter(PkceParameterNames.CODE_VERIFIER) != null;
    }

    /**
     * 验证并添加 DPoP 相关参数到附加参数映射中。
     *
     * @param request              HTTP 请求对象
     * @param additionalParameters 用于存储附加参数的映射
     */
    public void validateAndAddDPoPParametersIfAvailable(HttpServletRequest request,
                                                        Map<String, Object> additionalParameters) {
        final String dPoPProofHeaderName = OAuth2AccessToken.TokenType.DPOP.getValue();
        String dPoPProof = request.getHeader(dPoPProofHeaderName);
        if (StringUtils.hasText(dPoPProof)) {
            if (Collections.list(request.getHeaders(dPoPProofHeaderName)).size() != 1) {
                throwError(OAuth2ErrorCodes.INVALID_REQUEST, dPoPProofHeaderName, ACCESS_TOKEN_REQUEST_ERROR_URI);
            } else {
                additionalParameters.put("dpop_proof", dPoPProof);
                additionalParameters.put("dpop_method", request.getMethod());
                additionalParameters.put("dpop_target_uri", request.getRequestURL().toString());
            }
        }
    }

    /**
     * 抛出 OAuth2 认证异常。
     *
     * @param errorCode     错误代码
     * @param parameterName 出错的参数名称
     * @param errorUri      错误信息的规范 URI
     */
    public void throwError(String errorCode, String parameterName, String errorUri) {
        OAuth2Error error = new OAuth2Error(errorCode, "OAuth 2.0 Parameter: " + parameterName, errorUri);
        throw new OAuth2AuthenticationException(error);
    }

    /**
     * 标准化用户输入的用户码（user code），将其转换为大写并插入分隔符。
     *
     * @param userCode 用户输入的用户码
     * @return 标准化后的用户码（格式为 XXXX-XXXX）
     */
    public String normalizeUserCode(String userCode) {
        Assert.hasText(userCode, "userCode cannot be empty");
        StringBuilder sb = new StringBuilder(userCode.toUpperCase(Locale.ENGLISH).replaceAll("[^A-Z\\d]+", ""));
        Assert.isTrue(sb.length() == 8, "userCode must be exactly 8 alpha/numeric characters");
        sb.insert(4, '-');
        return sb.toString();
    }

    /**
     * 验证用户码是否符合规范（长度为 8 位字母或数字）。
     *
     * @param userCode 用户输入的用户码
     * @return 如果用户码有效返回 true，否则返回 false
     */
    public boolean validateUserCode(String userCode) {
        return (userCode != null && userCode.toUpperCase(Locale.ENGLISH).replaceAll("[^A-Z\\d]+", "").length() == 8);
    }

    /**
     * 解析作用域字符串，将其转换为Set集合
     *
     * @param scopes 以空格分隔的作用域字符串，可能为null或空字符串
     * @return 包含所有作用域的Set集合，如果输入为null或空则返回空集合
     */
    public Set<String> parseScopes(String scopes) {
        // 处理空值情况，返回空集合
        if (scopes == null || scopes.isEmpty()) {
            return Collections.emptySet();
        }

        // 解析作用域字符串并添加到集合中
        Set<String> scopeSet = new HashSet<>();
        String[] scopeArray = scopes.split(" ");
        for (String scope : scopeArray) {
            // 过滤空字符串，只添加非空作用域
            if (!scope.isBlank()) {
                scopeSet.add(scope);
            }
        }
        return scopeSet;
    }

}
