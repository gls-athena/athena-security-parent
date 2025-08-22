package com.gls.athena.security.captcha.config;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 图形验证码配置
 * 用于配置图形验证码相关的参数，包括验证码图片属性、验证规则、URL路径等配置信息
 *
 * @author george
 */
@Data
public class ImageCaptchaProperties implements Serializable {
    /**
     * 验证码参数名称
     * 用于指定在请求参数中验证码的参数名
     */
    private String captchaParam = "imageCaptcha";
    /**
     * 验证码过期时间
     * 验证码的有效时间，单位为毫秒，默认60秒
     */
    private int captchaExpire = 60000;
    /**
     * 验证码发送间隔
     * 控制验证码发送的时间间隔，防止频繁请求，单位为毫秒，默认60秒
     */
    private long captchaInterval = 60000;
    /**
     * 验证码图片宽度
     * 设置生成的验证码图片的宽度，单位为像素，默认120像素
     */
    private int width = 120;
    /**
     * 验证码图片高度
     * 设置生成的验证码图片的高度，单位为像素，默认40像素
     */
    private int height = 40;
    /**
     * 验证码字符个数
     * 指定验证码中包含的字符数量，默认4个字符
     */
    private int codeCount = 4;
    /**
     * 验证码干扰线数
     * 设置验证码图片中的干扰线数量，用于增加识别难度，默认20条
     */
    private int lineCount = 20;
    /**
     * 字体大小
     * 设置验证码字符的字体大小比例，默认1.2倍
     */
    private float size = 1.2f;
    /**
     * uuid参数名称
     * 用于指定在请求参数中UUID的参数名
     */
    private String uuidParam = "uuid";
    /**
     * 获取图片验证码的url
     * 客户端获取验证码图片的接口路径
     */
    private String captchaUrl = "/captcha/image";
    /**
     * 图片验证码校验URL
     * 需要进行验证码校验的URL列表
     */
    private List<String> captchaCheckUrls = new ArrayList<>();

}

