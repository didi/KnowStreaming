package com.xiaojukeji.kafka.manager.common.entity.vo.op.task;

import io.swagger.annotations.ApiModel;

/**
 * @author zengqiao
 * @date 20/5/11
 */
@ApiModel(value="Kafka相关文件")
public class ClusterTaskKafkaFilesVO {
    private String fileName;

    private String fileMd5;

    private Integer fileType;

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

    @Override
    public String toString() {
        return "ClusterTaskKafkaFilesVO{" +
                "fileName='" + fileName + '\'' +
                ", fileMd5='" + fileMd5 + '\'' +
                ", fileType=" + fileType +
                '}';
    }
}