package com.gls.athena.security.oauth2.client.wechat.support;

import lombok.experimental.UtilityClass;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/**
 * RestTemplate工具类
 * 提供微信相关的HTTP请求工具方法
 *
 * @author george
 */
@UtilityClass
public class RestTemplateUtil {

    /**
     * 获取配置了微信消息转换器的RestTemplate实例
     * 该方法会创建一个新的RestTemplate实例，并添加WechatHttpMessageConverter
     * 用于处理微信特有的HTTP消息格式
     *
     * @return 配置了微信消息转换器的RestTemplate实例
     */
    public RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        // 添加微信专用的消息转换器以支持微信接口返回的数据格式
        restTemplate.getMessageConverters().add(new WechatHttpMessageConverter());
        return restTemplate;
    }

    /**
     * 发送HTTP GET请求并获取指定类型的响应结果
     * 使用配置了微信消息转换器的RestTemplate执行GET请求
     *
     * @param <T>          响应结果的泛型类型
     * @param url          请求的URL地址
     * @param responseType 期望的响应结果类型Class对象
     * @return 指定类型的响应结果对象
     */
    public <T> T get(String url, Class<T> responseType) {
        return getRestTemplate().getForObject(url, responseType);
    }

    /**
     * 向指定URI发起GET请求并返回响应对象
     *
     * @param <T>          响应对象的泛型类型
     * @param uri          请求的目标URI地址
     * @param responseType 期望的响应对象类型Class
     * @return 根据responseType类型解析的响应对象
     */
    public <T> T get(URI uri, Class<T> responseType) {
        return getRestTemplate().getForObject(uri, responseType);
    }

}
