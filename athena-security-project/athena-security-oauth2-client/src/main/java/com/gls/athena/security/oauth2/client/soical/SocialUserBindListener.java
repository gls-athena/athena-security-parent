package com.gls.athena.security.oauth2.client.soical;

import com.gls.athena.common.bean.security.SocialUser;
import com.gls.athena.common.bean.security.User;
import com.gls.athena.security.oauth2.client.config.Oauth2ClientConstants;
import com.gls.athena.starter.web.util.WebUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * 社交用户绑定监听器
 * 监听认证成功事件，当用户通过用户名密码认证成功后，自动绑定之前登录的社交账号
 *
 * @author george
 */
@Slf4j
@Component
public class SocialUserBindListener {

    @Resource
    private ISocialUserService socialUserService;

    /**
     * 处理认证成功事件，绑定社交用户
     * 当用户通过用户名密码认证成功后，检查是否存在未绑定的社交用户信息，
     * 如果存在则将社交账号与系统用户进行绑定
     *
     * @param event 认证成功事件对象，包含认证信息
     */
    @EventListener(AuthenticationSuccessEvent.class)
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        // 只处理用户名密码认证成功的事件
        if (!(authentication instanceof UsernamePasswordAuthenticationToken)) {
            return;
        }

        // 从session中获取社交用户信息
        SocialUser socialUser = WebUtil.getSession().map(session -> (SocialUser) session.getAttribute(Oauth2ClientConstants.SOCIAL_USER_SESSION_KEY)).orElse(null);
        // 如果没有社交用户信息或已经绑定过，则直接返回
        if (socialUser == null || socialUser.isBindStatus()) {
            return;
        }

        // 绑定社交账号
        User user = (User) authentication.getPrincipal();
        log.info("正在绑定社交用户，系统用户ID: {}, 社交平台: {}", user.getId(), socialUser.getRegistrationId());
        socialUser.setUser(user);
        socialUser.setBindStatus(true);
        socialUserService.saveSocialUser(socialUser);
        WebUtil.getSession().ifPresent(session -> session.removeAttribute(Oauth2ClientConstants.SOCIAL_USER_SESSION_KEY));
        log.info("社交用户绑定成功，用户ID: {}", user.getId());
    }
}
