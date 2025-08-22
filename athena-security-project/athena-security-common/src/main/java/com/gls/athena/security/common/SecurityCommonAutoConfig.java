package com.gls.athena.security.common;

import com.gls.athena.security.common.config.SecurityCommonProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

/**
 * 安全通用自动配置类
 * <p>
 * 该类负责安全相关组件的自动配置，包括：
 * 1. 启用组件扫描，自动发现和注册安全相关的Bean
 * 2. 启用配置属性绑定，将SecurityCommonProperties配置类与配置文件进行绑定
 * </p>
 *
 * @author george
 */
@AutoConfiguration
@ComponentScan
@EnableConfigurationProperties(SecurityCommonProperties.class)
public class SecurityCommonAutoConfig {
}