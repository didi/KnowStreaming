package com.xiaojukeji.kafka.manager.web.config;

import org.springframework.http.HttpHeaders;

/**
 * @author xuguang
 * @Date 2022/1/11
 */
public class HttpUtils {

    public static HttpHeaders getHttpHeaders() {
        // 需要在管控平台上配置，教程见docs -> user_guide -> call_api_bypass_login
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(Constant.TRICK_LOGIN_SWITCH, Constant.ON);
        httpHeaders.add(Constant.TRICK_LOGIN_USER, Constant.USER_ADMIN);
        return httpHeaders;
    }
}
