package com.gls.athena.security.common.support;

import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.provisioning.UserDetailsManager;

/**
 * 用户服务接口
 * <p>
 * 该接口继承了Spring Security的UserDetailsManager和UserDetailsPasswordService接口，
 * 提供了用户管理和服务的核心功能，包括用户创建、更新、删除、查询以及密码管理等操作。
 * </p>
 *
 * @author george
 */
public interface IUserService extends UserDetailsManager, UserDetailsPasswordService {
}