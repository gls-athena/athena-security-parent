package com.gls.athena.security.oauth2.client.feishu.domain;

import lombok.Data;

/**
 * 飞书响应
 * 用于封装飞书API的响应结果，包含响应码、响应消息和具体数据
 *
 * @param <T> 响应数据的类型参数
 * @author george
 */
@Data
public class FeishuResponse<T> {
    /**
     * 响应码
     * 用于表示请求处理的结果状态，通常0表示成功，非0表示失败
     */
    private Integer code;
    /**
     * 响应消息
     * 包含对响应状态的描述信息，用于说明请求处理的结果
     */
    private String msg;
    /**
     * 数据
     * 实际的业务数据内容，具体类型由泛型参数T决定
     */
    private T data;
}

