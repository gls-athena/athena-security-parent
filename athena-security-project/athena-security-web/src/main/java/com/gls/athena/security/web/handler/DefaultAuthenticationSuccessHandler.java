package com.gls.athena.security.web.handler;

import cn.hutool.json.JSONUtil;
import com.gls.athena.common.core.domain.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 默认验证成功处理器
 * 处理用户认证成功后的逻辑操作，返回统一的 JSON 格式响应，与 {@link DefaultAuthenticationFailureHandler} 保持一致。
 *
 * @author george
 */
@Slf4j
@Component
public class DefaultAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * 在验证完成后调用。
     * 认证成功后返回 HTTP 200 及统一 JSON 格式的成功响应体 {@code Result<String>}，
     * 与 {@link DefaultAuthenticationFailureHandler} 的响应结构保持一致。
     *
     * @param request        HTTP请求对象
     * @param response       HTTP响应对象
     * @param authentication 认证信息对象
     * @throws IOException      IO异常
     * @throws ServletException Servlet异常
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("登录成功, 用户: {}", authentication.getName());
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        Result<String> result = Result.success("登录成功");
        response.getWriter().write(JSONUtil.toJsonStr(result));
    }
}


