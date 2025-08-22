package com.gls.athena.security.oauth2.client.soical;

import com.gls.athena.common.bean.security.SocialUser;

/**
 * 社交用户服务接口
 * <p>
 * 提供社交用户的相关操作接口，包括获取和保存社交用户信息
 * </p>
 *
 * @author george
 */
public interface ISocialUserService {

    /**
     * 根据注册ID和用户名获取社交用户信息
     *
     * @param registrationId 注册ID，用于标识不同的社交登录平台
     * @param name           用户名，在社交平台中的唯一标识
     * @return SocialUser 社交用户对象，包含用户的详细信息
     */
    SocialUser getSocialUser(String registrationId, String name);

    /**
     * 保存社交用户信息
     *
     * @param socialUser 社交用户对象，包含需要保存的用户信息
     * @return SocialUser 保存后的社交用户对象
     */
    SocialUser saveSocialUser(SocialUser socialUser);
}
