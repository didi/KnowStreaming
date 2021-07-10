package com.xiaojukeji.kafka.manager.kcm;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.normal.KafkaFileDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.KafkaFileDO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author zhongyuankai
 * @date 2020/5/7
 */
public interface KafkaFileService {
    ResultStatus uploadKafkaFile(KafkaFileDTO kafkaFileDTO, String userName);

    ResultStatus modifyKafkaFile(KafkaFileDTO kafkaFileDTO, String userName);

    ResultStatus deleteKafkaFile(Long id);

    List<KafkaFileDO> getKafkaFiles();

    KafkaFileDO getFileById(Long id);

    KafkaFileDO getFileByFileName(String fileName);

    Result<MultipartFile> downloadKafkaFile(Long fileId);

    String getDownloadBaseUrl();
}
