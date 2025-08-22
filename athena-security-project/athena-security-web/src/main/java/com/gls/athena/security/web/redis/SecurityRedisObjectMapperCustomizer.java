package com.gls.athena.security.web.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gls.athena.starter.data.redis.support.RedisObjectMapperCustomizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.stereotype.Component;

/**
 * 安全Redis对象映射器自定义器
 * 用于为Redis序列化配置Spring Security相关的Jackson模块
 *
 * @author george
 */
@Slf4j
@Component
public class SecurityRedisObjectMapperCustomizer implements RedisObjectMapperCustomizer {
    /**
     * 自定义ObjectMapper配置
     * 注册Spring Security相关的Jackson模块，使Redis能够正确序列化和反序列化Spring Security相关对象
     *
     * @param objectMapper ObjectMapper对象，用于JSON序列化和反序列化
     */
    @Override
    public void customize(ObjectMapper objectMapper) {
        log.debug("注册Spring Security相关的Jackson模块");
        // 获取Spring Security相关的Jackson模块并注册到ObjectMapper中
        SecurityJackson2Modules.getModules(getClass().getClassLoader())
                .forEach(objectMapper::registerModule);
    }
}

