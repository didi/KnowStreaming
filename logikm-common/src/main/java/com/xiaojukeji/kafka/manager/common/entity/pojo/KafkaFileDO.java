package com.xiaojukeji.kafka.manager.common.entity.pojo;

import java.util.Date;

/**
 * @author zhongyuankai
 * @date 2020/5/7
 */
public class KafkaFileDO {
    private Long id;

    private Date gmtCreate;

    private Date gmtModify;

    private Long clusterId;

    private String fileName;

    private String fileMd5;

    private Integer fileType;

    private String description;

    private String operator;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public Integer getFileType() {
        return fileType;
    }

    public void setFileType(Integer fileType) {
        this.fileType = fileType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return "KafkaFileDO{" +
                "id=" + id +
                ", gmtCreate=" + gmtCreate +
                ", gmtModify=" + gmtModify +
                ", clusterId=" + clusterId +
                ", fileName='" + fileName + '\'' +
                ", fileMd5='" + fileMd5 + '\'' +
                ", fileType=" + fileType +
                ", description='" + description + '\'' +
                ", operator='" + operator + '\'' +
                '}';
    }
}
