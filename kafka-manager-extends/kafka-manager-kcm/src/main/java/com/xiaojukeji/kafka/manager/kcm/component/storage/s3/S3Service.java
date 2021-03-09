package com.xiaojukeji.kafka.manager.kcm.component.storage.s3;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.kcm.component.storage.AbstractStorageService;
import io.minio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;


@Service("storageService")
public class S3Service extends AbstractStorageService {
    private final static Logger LOGGER = LoggerFactory.getLogger(S3Service.class);

    @Value("${kcm.s3.endpoint:}")
    private String endpoint;

    @Value("${kcm.s3.access-key:}")
    private String accessKey;

    @Value("${kcm.s3.secret-key:}")
    private String secretKey;

    @Value("${kcm.s3.bucket:}")
    private String bucket;

    private MinioClient minioClient;

    @PostConstruct
    public void init() {
        try {
            if (ValidateUtils.anyBlank(this.endpoint, this.accessKey, this.secretKey, this.bucket)) {
                // without config s3
                return;
            }
            minioClient = new MinioClient(endpoint, accessKey, secretKey);
        } catch (Exception e) {
            LOGGER.error("class=S3Service||method=init||fields={}||errMsg={}", this.toString(), e.getMessage());
        }
    }

    @Override
    public boolean upload(String fileName, String fileMd5, MultipartFile uploadFile) {
        InputStream inputStream = null;
        try {
            if (!createBucketIfNotExist()) {
                return false;
            }

            inputStream = uploadFile.getInputStream();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(this.bucket)
                    .object(fileName)
                    .stream(inputStream, inputStream.available(), -1)
                    .build()
            );
            return true;
        } catch (Exception e) {
            LOGGER.error("class=S3Service||method=upload||fileName={}||errMsg={}||msg=upload failed", fileName, e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    ; // ignore
                }
            }
        }
        return false;
    }

    @Override
    public Result<MultipartFile> download(String fileName, String fileMd5) {
        try {
            final ObjectStat stat = minioClient.statObject(this.bucket, fileName);

            InputStream is = minioClient.getObject(this.bucket, fileName);

            return Result.buildSuc(new MockMultipartFile(fileName, fileName, stat.contentType(), is));
        } catch (Exception e) {
            LOGGER.error("class=S3Service||method=download||fileName={}||errMsg={}||msg=download failed", fileName, e.getMessage());
        }
        return Result.buildFrom(ResultStatus.STORAGE_DOWNLOAD_FILE_FAILED);
    }

    @Override
    public String getDownloadBaseUrl() {
        if (this.endpoint.startsWith("http://")) {
            return this.endpoint + "/" + this.bucket;
        }
        return "http://" + this.endpoint + "/" + this.bucket;
    }

    private boolean createBucketIfNotExist() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(this.bucket).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(this.bucket).build());
            }

            LOGGER.info("class=S3Service||method=createBucketIfNotExist||bucket={}||msg=check and create bucket success", this.bucket);
            return true;
        } catch (Exception e) {
            LOGGER.error("class=S3Service||method=createBucketIfNotExist||bucket={}||errMsg={}||msg=create bucket failed", this.bucket, e.getMessage());
        }
        return false;
    }

    @Override
    public String toString() {
        return "S3Service{" +
                "endpoint='" + endpoint + '\'' +
                ", accessKey='" + accessKey + '\'' +
                ", secretKey='" + secretKey + '\'' +
                ", bucket='" + bucket + '\'' +
                '}';
    }
}
