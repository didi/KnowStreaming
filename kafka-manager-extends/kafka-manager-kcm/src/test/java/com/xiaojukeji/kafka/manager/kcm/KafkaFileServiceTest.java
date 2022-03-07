package com.xiaojukeji.kafka.manager.kcm;

import com.xiaojukeji.kafka.manager.common.bizenum.KafkaFileEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.normal.KafkaFileDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.KafkaFileDO;
import com.xiaojukeji.kafka.manager.dao.KafkaFileDao;
import com.xiaojukeji.kafka.manager.kcm.component.storage.AbstractStorageService;
import com.xiaojukeji.kafka.manager.kcm.config.BaseTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author xuguang
 * @Date 2022/1/4
 */
public class KafkaFileServiceTest extends BaseTest {

    private static final Long KAFKA_FILE_ID = 1L;

    @Value("${test.admin}")
    private static final String ADMIN = "admin";

    private KafkaFileDTO getKafkaFileDTO() {
        KafkaFileDTO kafkaFileDTO = new KafkaFileDTO();
        kafkaFileDTO.setId(KAFKA_FILE_ID);
        kafkaFileDTO.setClusterId(-1L);
        kafkaFileDTO.setFileMd5("");
        kafkaFileDTO.setFileName(".tgz");
        kafkaFileDTO.setFileType(KafkaFileEnum.PACKAGE.getCode());
        kafkaFileDTO.setUploadFile(new MockMultipartFile("name", new byte[]{1}));
        return kafkaFileDTO;
    }

    private KafkaFileDO getKafkaFileDO() {
        KafkaFileDO kafkaFileDO = new KafkaFileDO();
        kafkaFileDO.setFileType(KafkaFileEnum.PACKAGE.getCode());
        kafkaFileDO.setFileName(".tgz");
        return kafkaFileDO;
    }

    @Autowired
    @InjectMocks
    private KafkaFileService kafkaFileService;

    @Mock
    private KafkaFileDao kafkaFileDao;

    @Mock
    private AbstractStorageService storageService;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void uploadKafkaFile() {
        // paramIllegal
        uploadKafkaFile2ParamIllegalTest();
        // mysqlError
        uploadKafkaFile2MysqlErrorTest();
        // storage upload file failed
        uploadKafkaFile2StorageUploadFileFailedTest();
        // success
        uploadKafkaFile2SuccessTest();
        // DuplicateKey
        uploadKafkaFile2DuplicateKeyTest();
    }

    private void uploadKafkaFile2ParamIllegalTest() {
        KafkaFileDTO kafkaFileDTO = getKafkaFileDTO();
        kafkaFileDTO.setUploadFile(null);
        ResultStatus resultStatus = kafkaFileService.uploadKafkaFile(kafkaFileDTO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void uploadKafkaFile2MysqlErrorTest() {
        Mockito.when(kafkaFileDao.insert(Mockito.any())).thenReturn(-1);

        KafkaFileDTO kafkaFileDTO = getKafkaFileDTO();
        ResultStatus resultStatus = kafkaFileService.uploadKafkaFile(kafkaFileDTO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.MYSQL_ERROR.getCode());
    }

    private void uploadKafkaFile2StorageUploadFileFailedTest() {
        Mockito.when(kafkaFileDao.insert(Mockito.any())).thenReturn(1);
        Mockito.when(kafkaFileDao.deleteById(Mockito.any())).thenReturn(1);
        Mockito.when(storageService.upload(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(false);

        KafkaFileDTO kafkaFileDTO = getKafkaFileDTO();
        ResultStatus resultStatus = kafkaFileService.uploadKafkaFile(kafkaFileDTO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.STORAGE_UPLOAD_FILE_FAILED.getCode());
    }

    private void uploadKafkaFile2SuccessTest() {
        Mockito.when(kafkaFileDao.insert(Mockito.any())).thenReturn(1);
        Mockito.when(kafkaFileDao.deleteById(Mockito.any())).thenReturn(1);
        Mockito.when(storageService.upload(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);

        KafkaFileDTO kafkaFileDTO = getKafkaFileDTO();
        ResultStatus resultStatus = kafkaFileService.uploadKafkaFile(kafkaFileDTO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void uploadKafkaFile2DuplicateKeyTest() {
        Mockito.when(kafkaFileDao.insert(Mockito.any())).thenThrow(DuplicateKeyException.class);
        Mockito.when(kafkaFileDao.deleteById(Mockito.any())).thenReturn(1);
        Mockito.when(storageService.upload(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);

        KafkaFileDTO kafkaFileDTO = getKafkaFileDTO();
        ResultStatus resultStatus = kafkaFileService.uploadKafkaFile(kafkaFileDTO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.RESOURCE_ALREADY_EXISTED.getCode());
    }


    @Test
    public void modifyKafkaFileTest() {
        // paramIllegal
        modifyKafkaFile2ParamIllegalTest();
        // resource not exist
        modifyKafkaFile2ResourceNotExistTest();
        // operation failed
        modifyKafkaFile2OperationFailedTest();
        // mysqlError
        modifyKafkaFile2MysqlErrorTest();
        // success
        modifyKafkaFile2SuccessTest();
    }

    private void modifyKafkaFile2ParamIllegalTest() {
        ResultStatus resultStatus = kafkaFileService.modifyKafkaFile(null, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void modifyKafkaFile2ResourceNotExistTest() {
        Mockito.when(kafkaFileDao.getById(Mockito.anyLong())).thenReturn(null);

        KafkaFileDTO kafkaFileDTO = getKafkaFileDTO();
        ResultStatus resultStatus = kafkaFileService.modifyKafkaFile(kafkaFileDTO, ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.RESOURCE_NOT_EXIST.getCode());
    }

    private void modifyKafkaFile2OperationFailedTest() {
        Mockito.when(kafkaFileDao.getById(Mockito.anyLong())).thenReturn(new KafkaFileDO());
        Mockito.when(kafkaFileDao.updateById(Mockito.any())).thenReturn(-1);

        KafkaFileDTO kafkaFileDTO = getKafkaFileDTO();
        kafkaFileDTO.setFileType(-1);
        ResultStatus resultStatus1 = kafkaFileService.modifyKafkaFile(kafkaFileDTO, ADMIN);
        Assert.assertEquals(resultStatus1.getCode(), ResultStatus.OPERATION_FAILED.getCode());

        kafkaFileDTO.setFileType(KafkaFileEnum.PACKAGE.getCode());
        kafkaFileDTO.setFileName("xxx");
        ResultStatus resultStatus2 = kafkaFileService.modifyKafkaFile(kafkaFileDTO, ADMIN);
        Assert.assertEquals(resultStatus2.getCode(), ResultStatus.OPERATION_FAILED.getCode());
    }

    private void modifyKafkaFile2MysqlErrorTest() {
        KafkaFileDO kafkaFileDO = getKafkaFileDO();
        Mockito.when(kafkaFileDao.getById(Mockito.anyLong())).thenReturn(kafkaFileDO);
        Mockito.when(kafkaFileDao.updateById(Mockito.any())).thenReturn(-1);

        KafkaFileDTO kafkaFileDTO = getKafkaFileDTO();
        ResultStatus resultStatus1 = kafkaFileService.modifyKafkaFile(kafkaFileDTO, ADMIN);
        Assert.assertEquals(resultStatus1.getCode(), ResultStatus.MYSQL_ERROR.getCode());

    }

    private void modifyKafkaFile2SuccessTest() {
        KafkaFileDO kafkaFileDO = getKafkaFileDO();
        Mockito.when(kafkaFileDao.getById(Mockito.anyLong())).thenReturn(kafkaFileDO);
        Mockito.when(kafkaFileDao.updateById(Mockito.any())).thenReturn(1);
        Mockito.when(storageService.upload(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);

        KafkaFileDTO kafkaFileDTO = getKafkaFileDTO();
        ResultStatus resultStatus1 = kafkaFileService.modifyKafkaFile(kafkaFileDTO, ADMIN);
        Assert.assertEquals(resultStatus1.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test
    public void downloadKafkaFileTest() {
        // resource not exist
        downloadKafkaFile2ResourceNotExist();
        // STORAGE_FILE_TYPE_NOT_SUPPORT
        downloadKafkaFile2FileNotSupportExist();
        // success
        downloadKafkaFile2SuccessExist();
    }

    private void downloadKafkaFile2ResourceNotExist() {
        Mockito.when(kafkaFileDao.getById(Mockito.anyLong())).thenReturn(null);
        Result<MultipartFile> resultStatus = kafkaFileService.downloadKafkaFile(KAFKA_FILE_ID);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.RESOURCE_NOT_EXIST.getCode());
    }

    private void downloadKafkaFile2FileNotSupportExist() {
        KafkaFileDO kafkaFileDO = getKafkaFileDO();
        Mockito.when(kafkaFileDao.getById(Mockito.anyLong())).thenReturn(kafkaFileDO);
        Result<MultipartFile> resultStatus = kafkaFileService.downloadKafkaFile(KAFKA_FILE_ID);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.STORAGE_FILE_TYPE_NOT_SUPPORT.getCode());
    }

    private void downloadKafkaFile2SuccessExist() {
        Mockito.when(storageService.download(Mockito.any(), Mockito.any())).thenReturn(Result.buildFrom(ResultStatus.SUCCESS));
        KafkaFileDO kafkaFileDO = getKafkaFileDO();
        kafkaFileDO.setFileType(KafkaFileEnum.SERVER_CONFIG.getCode());
        Mockito.when(kafkaFileDao.getById(Mockito.anyLong())).thenReturn(kafkaFileDO);

        Result<MultipartFile> resultStatus = kafkaFileService.downloadKafkaFile(KAFKA_FILE_ID);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());
    }


}
