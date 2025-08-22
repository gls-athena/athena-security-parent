package com.gls.athena.security.oauth2.client.soical;

import com.gls.athena.common.bean.security.SocialUser;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于内存的社交用户服务实现类
 * 提供社交用户信息的存储和查询功能
 *
 * @author george
 */
public class InMemorySocialUserServiceImpl implements ISocialUserService {

    private final List<SocialUser> users = new ArrayList<>();

    /**
     * 根据注册ID和用户名获取社交用户信息
     *
     * @param registrationId 注册ID，用于标识不同的社交登录提供商
     * @param name           用户名，在特定注册ID下唯一标识用户
     * @return 社交用户信息，如果未找到则返回null
     */
    @Override
    public SocialUser getSocialUser(String registrationId, String name) {
        return users.stream()
                .filter(user -> user.getRegistrationId().equals(registrationId) && user.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * 保存社交用户信息
     * 如果已存在相同注册ID和用户名的用户，则更新原有记录；否则新增记录
     *
     * @param socialUser 社交用户信息对象
     * @return 保存后的社交用户信息
     */
    @Override
    public SocialUser saveSocialUser(SocialUser socialUser) {
        // 查找是否已存在相同的用户记录
        return users.stream()
                .filter(user -> user.getRegistrationId().equals(socialUser.getRegistrationId()) && user.getName().equals(socialUser.getName()))
                .findFirst()
                .map(existingUser -> {
                    // 如果存在则先删除再添加（更新操作）
                    users.remove(existingUser);
                    users.add(socialUser);
                    return socialUser;
                })
                .orElseGet(() -> {
                    // 如果不存在则直接添加（新增操作）
                    users.add(socialUser);
                    return socialUser;
                });
    }
}

