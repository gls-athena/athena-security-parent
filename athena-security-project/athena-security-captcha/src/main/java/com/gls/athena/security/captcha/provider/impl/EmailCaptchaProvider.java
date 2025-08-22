package com.gls.athena.security.captcha.provider.impl;

import cn.hutool.core.util.StrUtil;
import com.gls.athena.sdk.message.support.MessageUtil;
import com.gls.athena.security.captcha.config.CaptchaEnums;
import com.gls.athena.security.captcha.config.CaptchaProperties;
import com.gls.athena.security.captcha.config.EmailCaptchaProperties;
import com.gls.athena.security.captcha.domain.Captcha;
import com.gls.athena.security.captcha.filter.CaptchaException;
import com.gls.athena.security.captcha.repository.CaptchaRepository;
import com.gls.athena.security.captcha.support.CaptchaUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.util.List;
import java.util.Map;

/**
 * 邮箱验证码提供者实现类，用于生成、发送和校验邮箱验证码。
 *
 * @author george
 */
@Slf4j
public class EmailCaptchaProvider extends BaseCaptchaProvider<Captcha> {

    /**
     * 邮箱验证码相关配置属性
     */
    private final EmailCaptchaProperties properties;

    /**
     * 构造方法，初始化邮箱验证码提供者。
     *
     * @param properties 验证码全局配置对象，包含邮箱验证码的子配置
     * @param repository 验证码持久化仓库，用于存储和查询验证码信息
     */
    public EmailCaptchaProvider(CaptchaProperties properties, CaptchaRepository repository) {
        super(properties, repository);
        this.properties = properties.getEmail();
    }

    /**
     * 判断当前请求是否为邮箱验证码类型。
     *
     * @param captchaEnums 验证码类型枚举
     * @return 如果是邮箱验证码类型则返回 true，否则返回 false
     */
    @Override
    protected boolean isCaptchaTypeRequest(CaptchaEnums captchaEnums) {
        return CaptchaEnums.EMAIL.equals(captchaEnums);
    }

    /**
     * 获取邮箱验证码的请求地址。
     *
     * @return 邮箱验证码请求地址
     */
    @Override
    protected String getCaptchaUrl() {
        return properties.getCaptchaUrl();
    }

    /**
     * 执行发送邮箱验证码逻辑。
     *
     * @param email    接收验证码的邮箱地址
     * @param captcha  验证码对象，包含验证码内容和过期时间等信息
     * @param response HTTP 响应对象，用于写入操作结果
     */
    @Override
    protected void doSendCaptcha(String email, Captcha captcha, HttpServletResponse response) {
        // 参数有效性校验
        if (Strings.isBlank(email)) {
            throw new CaptchaException("邮箱不能为空");
        }
        if (captcha == null || StrUtil.isBlank(captcha.getCode())) {
            throw new CaptchaException("验证码对象无效");
        }

        // 构建短信模板参数并发送
        Map<String, Object> params = Map.of(
                "code", captcha.getCode(),
                "email", email
        );

        MessageUtil.sendEmail(email, "", "", properties.getCaptchaTemplateId(), params);
        log.info("向手机[{}]发送验证码[{}]成功", email, captcha.getCode());

        // 写入成功响应
        CaptchaUtil.writeSuccessResponse(response);
    }

    /**
     * 生成邮箱验证码对象。
     *
     * @return 新生成的验证码对象
     */
    @Override
    protected Captcha generateCaptcha() {
        return CaptchaUtil.generateCaptcha(properties.getCaptchaLength(), properties.getCaptchaExpire());
    }

    /**
     * 获取邮箱地址在请求中的参数名。
     *
     * @return 邮箱地址参数名
     */
    @Override
    protected String getKeyParam() {
        return properties.getEmailParam();
    }

    /**
     * 获取需要进行验证码校验的 URL 列表。
     *
     * @return 需要校验验证码的 URL 列表
     */
    @Override
    protected List<String> getCaptchaCheckUrls() {
        return properties.getCaptchaCheckUrls();
    }

    /**
     * 获取验证码在请求中的参数名。
     *
     * @return 验证码参数名
     */
    @Override
    protected String getCaptchaCodeParam() {
        return properties.getCaptchaParam();
    }
}
