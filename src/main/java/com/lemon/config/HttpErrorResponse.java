package com.lemon.config;

import lombok.Getter;

/**
 * @author Liang Zhancheng
 * @date 2021/8/2 17:50
 *  错误响应配置
 */
@Getter
public class HttpErrorResponse {
    private String code;
    private String msg;

    public static <T> HttpErrorResponse failure(String msg) {
        HttpErrorResponse result = new HttpErrorResponse();
        result.code = "500";
        result.msg = msg;
        return result;
    }
}
