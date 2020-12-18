package com.xiaojukeji.kafka.manager.kcm.component.storage.local;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.xiaojukeji.kafka.manager.kcm.component.storage.AbstractStorageService;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author zengqiao
 * @date 20/9/17
 */
@Service("storageService")
public class Local extends AbstractStorageService {
    @Value("${kcm.storage.base-url}")
    private String      baseUrl;

    @Override
    public boolean upload(String fileName, String fileMd5, MultipartFile uploadFile) {
        return false;
    }

    @Override
    public Result<String> download(String fileName, String fileMd5) {
        return Result.buildFrom(ResultStatus.DOWNLOAD_FILE_FAIL);
    }

    @Override
    public String getDownloadBaseUrl() {
        return baseUrl;
    }
}