package com.gls.athena.security.captcha.support;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.gls.athena.common.bean.result.Result;
import com.gls.athena.common.bean.result.ResultStatus;
import com.gls.athena.security.captcha.domain.Captcha;
import com.gls.athena.security.captcha.filter.CaptchaException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * 验证码工具类
 * 提供生成验证码、写入响应等功能。
 *
 * @author george
 */
@Slf4j
@UtilityClass
public class CaptchaUtil {

    /**
     * 生成一个验证码对象，包含指定长度的数字验证码和过期时间。
     *
     * @param length 验证码的长度，必须为正数
     * @param expire 验证码的有效时间（单位：秒），必须为正数
     * @return 生成的验证码对象
     * @throws CaptchaException 当参数不合法时抛出异常
     */
    public Captcha generateCaptcha(int length, int expire) {
        // 参数校验
        if (length <= 0) {
            throw new CaptchaException("验证码长度必须为正数");
        }
        if (expire <= 0) {
            throw new CaptchaException("过期时间必须为正数");
        }

        Captcha captcha = new Captcha();
        // 使用更安全的随机数生成方式
        String code = RandomUtil.randomNumbers(length);
        log.debug("生成验证码：{}", code);
        captcha.setCode(code);

        // 明确时区处理
        DateTime now = DateUtil.date();
        captcha.setExpireTime(DateUtil.offsetSecond(now, expire).toJdkDate());

        return captcha;
    }

    /**
     * 向HTTP响应中写入验证码发送成功的JSON格式响应。
     *
     * @param response HTTP响应对象，用于设置状态码、内容类型及写入响应体
     * @throws CaptchaException 当写入响应失败时抛出异常
     */
    public void writeSuccessResponse(HttpServletResponse response) {
        // 设置HTTP状态码为200，表示请求已经成功处理
        response.setStatus(HttpServletResponse.SC_OK);
        // 设置响应的内容类型为application/json
        response.setContentType("application/json; charset=UTF-8");
        try {
            // 创建一个成功的结果对象，并写入响应体中
            Result<String> result = ResultStatus.SUCCESS.toResult("验证码发送成功");
            response.getWriter().write(JSONUtil.toJsonStr(result));
        } catch (Exception e) {
            // 记录在写入成功响应时发生的错误
            log.error("写入成功响应失败", e);
            throw new CaptchaException("写入成功响应失败");
        }
    }
}
