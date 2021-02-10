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
     * @param fileName 文件名
     * @param fileMd5 文件md5
     * @param uploadFile 文件
     * @return 上传结果
     */
    public abstract boolean upload(String fileName, String fileMd5, MultipartFile uploadFile);

    /**
     * 下载文件
     * @param fileName 文件名
     * @param fileMd5 文件md5
     * @return 文件
     */
    public abstract Result<MultipartFile> download(String fileName, String fileMd5);

    /**
     * 下载base地址
     */
    public abstract String getDownloadBaseUrl();
}