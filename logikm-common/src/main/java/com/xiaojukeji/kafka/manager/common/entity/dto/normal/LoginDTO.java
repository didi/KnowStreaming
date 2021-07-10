package com.xiaojukeji.kafka.manager.common.entity.dto.normal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;

/**
 * @author zengqiao
 * @date 19/5/3
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "登陆")
public class LoginDTO {
    private String username;

    private String password;

    private String code;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "LoginDTO{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}