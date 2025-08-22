package com.gls.athena.security.web;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * Servlet安全配置
 * 该配置类用于启用Web安全功能，通过Spring Security提供安全控制
 *
 * @author george
 */
@Configuration
@ComponentScan
@EnableWebSecurity
public class SecurityWebAutoConfig {
}

