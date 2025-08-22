package com.gls.athena.security.common.support;

import cn.hutool.core.collection.CollUtil;
import com.gls.athena.common.bean.security.LoginUserHelper;
import com.gls.athena.common.bean.security.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * 内存用户服务实现类，用于在内存中管理用户信息。
 * 提供了用户创建、更新、删除、密码修改以及根据用户名加载用户等功能。
 *
 * @author george
 */
@Slf4j
public class InMemoryUserServiceImpl implements IUserService {

    /**
     * 存储所有用户的静态列表
     */
    private static final List<User> USERS = new ArrayList<>();

    /**
     * 构造方法，初始化用户列表
     *
     * @param users 可变参数形式传入的用户对象数组
     */
    public InMemoryUserServiceImpl(User... users) {
        CollUtil.addAll(USERS, users);
    }

    /**
     * 更新指定用户的密码
     *
     * @param user        要更新密码的用户对象
     * @param newPassword 新密码（应为已加密的密码字符串）
     * @return 更新后的用户详情对象
     * @throws UsernameNotFoundException 如果用户不存在则抛出异常
     */
    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        return USERS.stream()
                .filter(u -> u.getUsername().equals(user.getUsername()))
                .findFirst()
                .map(u -> {
                    u.setPassword(newPassword);
                    return u;
                })
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
    }

    /**
     * 创建新用户
     *
     * @param user 用户详情对象
     * @throws IllegalArgumentException 如果用户名已存在则抛出异常
     */
    @Override
    public void createUser(UserDetails user) {
        if (userExists(user.getUsername())) {
            throw new IllegalArgumentException("用户已存在");
        }
        USERS.add((User) user);
    }

    /**
     * 更新已有用户的信息
     *
     * @param user 包含更新信息的用户详情对象
     */
    @Override
    public void updateUser(UserDetails user) {
        USERS.stream()
                .filter(u -> u.getUsername().equals(user.getUsername()))
                .findFirst()
                .ifPresent(u -> {
                    USERS.remove(u);
                    USERS.add((User) user);
                });
    }

    /**
     * 根据用户名删除用户
     *
     * @param username 要删除的用户名
     */
    @Override
    public void deleteUser(String username) {
        USERS.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .ifPresent(USERS::remove);
    }

    /**
     * 修改当前登录用户的密码
     *
     * @param oldPassword 原始密码
     * @param newPassword 新密码
     * @throws IllegalArgumentException 如果用户未登录或原密码错误则抛出异常
     */
    @Override
    public void changePassword(String oldPassword, String newPassword) {
        User user = (User) LoginUserHelper.getCurrentUser().orElseThrow(() -> new IllegalArgumentException("用户未登录"));
        if (!user.getPassword().equals(oldPassword)) {
            throw new IllegalArgumentException("原密码错误");
        }
        USERS.stream()
                .filter(u -> u.getUsername().equals(user.getUsername()))
                .findFirst()
                .ifPresent(u -> u.setPassword(newPassword));
    }

    /**
     * 判断指定用户名的用户是否存在
     *
     * @param username 用户名
     * @return 存在返回 true，否则返回 false
     */
    @Override
    public boolean userExists(String username) {
        return USERS.stream().anyMatch(user -> user.getUsername().equals(username));
    }

    /**
     * 根据用户名加载用户信息，支持通过用户名、手机号或邮箱进行查找
     *
     * @param username 用户名/手机号/邮箱
     * @return 用户详情对象
     * @throws UsernameNotFoundException 如果用户不存在则抛出异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 支持使用用户名、手机号或邮箱登录
        return USERS.stream()
                .filter(user -> user.getUsername().equals(username) || user.getMobile().equals(username) || user.getEmail().equals(username))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
    }
}
