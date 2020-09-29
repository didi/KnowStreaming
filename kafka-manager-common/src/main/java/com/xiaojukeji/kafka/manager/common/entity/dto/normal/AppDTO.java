package com.xiaojukeji.kafka.manager.common.entity.dto.normal;

import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/5/4
 */
@ApiModel(description="App信息")
public class AppDTO {
    @ApiModelProperty(value="AppId, 不可修改")
    private String appId;

    @ApiModelProperty(value="App名称")
    private String name;

    @ApiModelProperty(value="App描述")
    private String description;

    @ApiModelProperty(value="App负责人")
    private String principals;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrincipals() {
        return principals;
    }

    public void setPrincipals(String principals) {
        this.principals = principals;
    }

    @Override
    public String toString() {
        return "AppDTO{" +
                "appId='" + appId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", principals='" + principals + '\'' +
                '}';
    }

    public boolean legal() {
        if (ValidateUtils.isBlank(appId)
                || ValidateUtils.isBlank(name)
                || ValidateUtils.isBlank(principals)
                || ValidateUtils.isBlank(description)) {
            return false;
        }
        return true;
    }
}