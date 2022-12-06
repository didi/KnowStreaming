package com.xiaojukeji.know.streaming.km.common.bean.entity.connect.plugin;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zengqiao
 * @date 22/10/17
 */
@Data
@ApiModel(description = "Connect插件信息")
@NoArgsConstructor
public class ConnectPluginBasic implements Serializable {
    /**
     * Json序列化时对应的字段
     */
    @JSONField(name="class")
    @JsonProperty("class")
    private String className;

    private String type;

    private String version;

    private String helpDocLink;

    public ConnectPluginBasic(String className, String type, String version, String helpDocLink) {
        this.className = className;
        this.type = type;
        this.version = version;
        this.helpDocLink = helpDocLink;
    }
}
