package com.gls.athena.security.web.handler;

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
 * 处理用户认证成功后的逻辑操作
 *
 * @author george
 */
@Slf4j
@Component
public class DefaultAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * 在验证完成后调用。
     * 认证成功后返回成功的响应信息
     *
     * @param request        HTTP请求对象
     * @param response       HTTP响应对象
     * @param authentication 认证信息对象
     * @throws IOException      IO异常
     * @throws ServletException Servlet异常
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 记录登录成功日志
        log.info("登录成功");
        // 设置响应状态码为200
        response.setStatus(HttpServletResponse.SC_OK);
        // 设置响应内容类型为JSON格式
        response.setContentType("application/json;charset=UTF-8");
        // 向客户端返回登录成功信息
        response.getWriter().write("登录成功");
    }
}

