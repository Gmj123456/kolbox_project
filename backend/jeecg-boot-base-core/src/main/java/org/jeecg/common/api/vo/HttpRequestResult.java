package org.jeecg.common.api.vo;

import lombok.Data;

@Data
public class HttpRequestResult<T> {

    /**
     *  返回编码格式
     *  200正常
     *  500异常
     */
    private int code;

    /**
     * 返回数据对象 data
     */
    private T data;

    /**
     *  返回消息
     */
    private String message;

}
