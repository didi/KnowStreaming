package com.xiaojukeji.kafka.manager.bpm.common.entry.apply;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zhongyuankai
 * @date 20/4/24
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDTO {
    @ApiModelProperty(value = "工单类型")
    private Integer type;

    @ApiModelProperty(value = "申请人")
    private String applicant;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "工单具体的信息(json串)")
    private String extensions;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExtensions() {
        return extensions;
    }

    public void setExtensions(String extensions) {
        this.extensions = extensions;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "type=" + type +
                ", applicant='" + applicant + '\'' +
                ", description='" + description + '\'' +
                ", extensions='" + extensions + '\'' +
                '}';
    }

    public boolean paramLegal() {
        if (ValidateUtils.isNullOrLessThanZero(type) ||
                ValidateUtils.isNull(description) ||
                ValidateUtils.isNull(applicant) ||
                ValidateUtils.isNull(extensions)) {
            return false;
        }
        return true;
    }
}
