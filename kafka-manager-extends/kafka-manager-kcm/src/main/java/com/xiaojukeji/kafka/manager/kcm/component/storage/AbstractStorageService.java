package com.xiaojukeji.kafka.manager.kcm.component.storage;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author zengqiao
 * @date 20/4/29
 */
public abstract class AbstractStorageService {
    /**
     * 上传
     */
    public abstract boolean upload(String fileName, String fileMd5, MultipartFile uploadFile);

    /**
     * 下载
     */
    public abstract Result<String> download(String fileName, String fileMd5);

    /**
     * 下载base地址
     */
    public abstract String getDownloadBaseUrl();
}