package com.xiaojukeji.kafka.manager.common.entity.dto.normal;

import com.xiaojukeji.kafka.manager.common.bizenum.KafkaFileEnum;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author zengqiao
 * @date 20/4/29
 */
@ApiModel(description = "Kafka文件")
public class KafkaFileDTO {
    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "集群ID, 创建的时候需要, 修改不需要, 如果是包，则传-1")
    private Long clusterId;

    @ApiModelProperty(value = "文件名, 创建时需要, 修改不需要")
    private String fileName;

    @ApiModelProperty(value = "文件MD5")
    private String fileMd5;

    @ApiModelProperty(value = "文件类型, 创建时需要, 修改不需要")
    private Integer fileType;

    @ApiModelProperty(value = "备注")
    private String description;

    @ApiModelProperty(value = "上传的文件")
    private MultipartFile uploadFile;

    @ApiModelProperty(value = "是更新操作")
    private Boolean modify;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public MultipartFile getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(MultipartFile uploadFile) {
        this.uploadFile = uploadFile;
    }

    public Boolean getModify() {
        return modify;
    }

    public void setModify(Boolean modify) {
        this.modify = modify;
    }

    @Override
    public String toString() {
        return "KafkaFileDTO{" +
                "id=" + id +
                ", clusterId=" + clusterId +
                ", fileName='" + fileName + '\'' +
                ", fileMd5='" + fileMd5 + '\'' +
                ", fileType=" + fileType +
                ", description='" + description + '\'' +
                '}';
    }

    public boolean createParamLegal() {
        if (ValidateUtils.isNull(clusterId) ||
                ValidateUtils.isBlank(fileName) ||
                ValidateUtils.isNull(fileType) ||
                ValidateUtils.isNull(fileMd5) ||
                ValidateUtils.isNull(uploadFile)) {
            return false;
        }
        if (!(fileName.endsWith(KafkaFileEnum.PACKAGE.getSuffix())
                || fileName.endsWith(KafkaFileEnum.SERVER_CONFIG.getSuffix()))) {
            // 后缀不对
            return false;
        }
        if (KafkaFileEnum.PACKAGE.getCode().equals(fileType) && clusterId != -1) {
            // 包不属于任何集群
            return false;
        }
        return true;
    }

    public boolean modifyParamLegal() {
        if (ValidateUtils.isBlank(fileName) ||
                ValidateUtils.isNull(fileMd5) ||
                ValidateUtils.isNull(uploadFile)) {
            return false;
        }
        return true;
    }
}