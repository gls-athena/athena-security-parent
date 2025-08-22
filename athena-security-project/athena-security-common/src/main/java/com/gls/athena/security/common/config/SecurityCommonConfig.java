package com.gls.athena.security.common.config;

import cn.hutool.core.collection.CollUtil;
import com.gls.athena.common.bean.security.Role;
import com.gls.athena.common.bean.security.User;
import com.gls.athena.security.common.support.IUserService;
import com.gls.athena.security.common.support.InMemoryUserServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 安全通用配置类
 * 提供安全相关的通用配置，包括用户服务的默认实现
 *
 * @author george
 */
@Configuration
public class SecurityCommonConfig {

    /**
     * 创建默认的用户服务Bean
     * 当IOC容器中不存在IUserService类型的Bean时，创建一个基于内存的用户服务实现
     * 默认创建一个admin用户，密码为admin，角色为管理员
     *
     * @return IUserService 用户服务接口的实现类实例
     */
    @Bean
    @ConditionalOnMissingBean(IUserService.class)
    public IUserService userService() {
        // 创建默认管理员用户
        User user = new User();
        user.setUsername("admin");
        user.setPassword("{noop}admin");
        user.setMobile("13800000000");

        // 创建管理员角色
        Role role = new Role();
        role.setCode("admin");
        role.setName("管理员");
        role.setDefaultRole(true);
        user.setRoles(CollUtil.newArrayList(role));

        // 返回基于内存的用户服务实现
        return new InMemoryUserServiceImpl(user);
    }

    /**
     * 创建密码编码器Bean
     * <p>
     * 当Spring容器中不存在PasswordEncoder类型的Bean时，创建并返回一个默认的密码编码器实例。
     * 该方法使用PasswordEncoderFactories创建一个委托密码编码器，能够支持多种密码编码格式。
     *
     * @return PasswordEncoder 密码编码器实例，用于密码的加密和验证
     */
    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        // 创建委托密码编码器，支持多种密码编码算法
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
