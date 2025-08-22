package com.gls.athena.security.captcha.provider.impl;

import cn.hutool.core.util.StrUtil;
import com.gls.athena.sdk.message.support.MessageUtil;
import com.gls.athena.security.captcha.config.CaptchaEnums;
import com.gls.athena.security.captcha.config.CaptchaProperties;
import com.gls.athena.security.captcha.config.SmsCaptchaProperties;
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
 * 短信验证码提供者实现类
 * 用于生成和发送短信验证码，并提供相关的配置和逻辑支持
 *
 * @author george
 */
@Slf4j
public class SmsCaptchaProvider extends BaseCaptchaProvider<Captcha> {

    private final SmsCaptchaProperties properties;

    /**
     * 构造方法注入必要的属性
     *
     * @param properties        验证码属性配置
     * @param captchaRepository 验证码仓库
     */
    public SmsCaptchaProvider(CaptchaProperties properties, CaptchaRepository captchaRepository) {
        super(properties, captchaRepository);
        this.properties = properties.getSms();
    }

    /**
     * 判断是否是短信验证码类型请求
     *
     * @param captchaEnums 验证码枚举类型
     * @return 如果是短信验证码类型返回true，否则返回false
     */
    @Override
    protected boolean isCaptchaTypeRequest(CaptchaEnums captchaEnums) {
        return CaptchaEnums.SMS.equals(captchaEnums);
    }

    /**
     * 获取短信验证码发送的URL
     *
     * @return 短信验证码发送的URL
     */
    @Override
    protected String getCaptchaUrl() {
        return properties.getCaptchaSendUrl();
    }

    /**
     * 执行发送短信验证码逻辑
     *
     * @param mobile   手机号
     * @param captcha  验证码对象
     * @param response HTTP响应对象
     */
    @Override
    protected void doSendCaptcha(String mobile, Captcha captcha, HttpServletResponse response) {
        // 参数有效性校验
        if (Strings.isBlank(mobile)) {
            throw new CaptchaException("手机号不能为空");
        }
        if (captcha == null || StrUtil.isBlank(captcha.getCode())) {
            throw new CaptchaException("验证码对象无效");
        }

        // 构建短信模板参数并发送
        Map<String, Object> params = Map.of(
                "code", captcha.getCode(),
                "mobile", mobile
        );

        MessageUtil.sendSms(mobile, properties.getCaptchaTemplateId(), params);
        log.info("向手机[{}]发送验证码[{}]成功", mobile, captcha.getCode());

        // 写入成功响应
        CaptchaUtil.writeSuccessResponse(response);
    }

    /**
     * 生成短信验证码对象
     * <p>
     * 此方法用于生成一个短信验证码对象，包括验证码的代码和过期时间
     * 它基于配置的验证码长度和过期时间进行生成
     *
     * @return 生成的验证码对象
     * @throws IllegalArgumentException 如果验证码长度或过期时间配置不正确抛出此异常
     */
    @Override
    protected Captcha generateCaptcha() {
        return CaptchaUtil.generateCaptcha(properties.getCaptchaLength(), properties.getCaptchaExpire());
    }

    /**
     * 获取短信验证码接口中移动电话参数的名称
     *
     * @return 移动电话参数的名称
     */
    @Override
    protected String getKeyParam() {
        return properties.getMobileParam();
    }

    /**
     * 获取需要进行验证码校验的URL列表
     *
     * @return 需要进行验证码校验的URL列表
     */
    @Override
    protected List<String> getCaptchaCheckUrls() {
        return properties.getCaptchaCheckUrls();
    }

    /**
     * 获取短信验证码接口中验证码参数的名称
     *
     * @return 验证码参数的名称
     */
    @Override
    protected String getCaptchaCodeParam() {
        return properties.getCaptchaParam();
    }
}
