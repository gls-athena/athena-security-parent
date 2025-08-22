package com.gls.athena.security.web.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.gls.athena.common.bean.result.Result;
import com.gls.athena.common.bean.result.ResultStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 默认验证失败处理器
 * 用于处理认证失败的情况，返回统一格式的错误响应
 *
 * @author george
 */
@Slf4j
@Component
public class DefaultAuthenticationFailureHandler implements AuthenticationFailureHandler {

    /**
     * 在验证过程中遇到异常时调用。
     * 该方法会记录异常日志，并返回JSON格式的错误响应给客户端
     *
     * @param request   HTTP请求对象
     * @param response  HTTP响应对象
     * @param exception 认证异常对象
     * @throws IOException      IO异常
     * @throws ServletException Servlet异常
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // 记录异常日志
        String message = null;
        if (exception instanceof OAuth2AuthenticationException oauth2AuthenticationException) {
            // OAuth2异常
            message = oauth2AuthenticationException.getError().getDescription();
        }
        if (StrUtil.isBlank(message)) {
            message = exception.getMessage();
        }
        // 输出异常信息
        log.error(message, exception);
        Result<String> result = ResultStatus.PARAM_ERROR.toResult(message);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSONUtil.toJsonStr(result));
    }
}

