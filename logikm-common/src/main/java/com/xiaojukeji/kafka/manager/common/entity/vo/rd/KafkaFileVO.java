package com.xiaojukeji.kafka.manager.common.entity.vo.rd;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * @author zengqiao
 * @date 20/4/29
 */
@ApiModel(description = "Kafka文件信息")
public class KafkaFileVO {
    @ApiModelProperty(value = "集群名称")
    private String clusterName;

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "文件类型")
    private Integer fileType;

    @ApiModelProperty(value = "存储位置")
    private String storageName;

    @ApiModelProperty(value = "文件MD5")
    private String fileMd5;

    @ApiModelProperty(value = "操作人")
    private String operator;

    @ApiModelProperty(value = "备注")
    private String description;

    @ApiModelProperty(value = "修改时间")
    private Date gmtModify;

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getFileType() {
        return fileType;
    }

    public void setFileType(Integer fileType) {
        this.fileType = fileType;
    }

    public String getStorageName() {
        return storageName;
    }

    public void setStorageName(String storageName) {
        this.storageName = storageName;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
    }

    @Override
    public String toString() {
        return "KafkaFileVO{" +
                "clusterName='" + clusterName + '\'' +
                ", id=" + id +
                ", fileName='" + fileName + '\'' +
                ", fileType=" + fileType +
                ", storageName='" + storageName + '\'' +
                ", fileMd5='" + fileMd5 + '\'' +
                ", operator='" + operator + '\'' +
                ", description='" + description + '\'' +
                ", gmtModify=" + gmtModify +
                '}';
    }
}