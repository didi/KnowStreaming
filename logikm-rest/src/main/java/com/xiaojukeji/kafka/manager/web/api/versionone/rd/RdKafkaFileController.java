package com.xiaojukeji.kafka.manager.web.api.versionone.rd;

import com.xiaojukeji.kafka.manager.common.bizenum.KafkaFileEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.dto.normal.KafkaFileDTO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.KafkaFileVO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.kcm.component.storage.common.StorageEnum;
import com.xiaojukeji.kafka.manager.common.entity.pojo.KafkaFileDO;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.kcm.KafkaFileService;
import com.xiaojukeji.kafka.manager.common.utils.JsonUtils;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.web.converters.KafkaFileConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zengqiao
 * @date 20/4/26
 */
@Api(tags = "RD-Package管理相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_RD_PREFIX)
public class RdKafkaFileController {
    private final static Logger LOGGER = LoggerFactory.getLogger(RdKafkaFileController.class);

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private KafkaFileService kafkaFileService;

    @ApiOperation(value = "文件枚举信息", notes = "")
    @RequestMapping(value = "kafka-files/enums", method = RequestMethod.GET)
    @ResponseBody
    public Result getKafkaFileEnums() {
        Map<String, Object> enumMap = new HashMap<>(2);
        enumMap.put("fileEnum", JsonUtils.toJson(KafkaFileEnum.class));
        enumMap.put("storageEnum", JsonUtils.toJson(StorageEnum.class));
        return new Result<>(enumMap);
    }

    @ApiOperation(value = "上传文件", notes = "")
    @RequestMapping(value = "kafka-files", method = RequestMethod.POST)
    @ResponseBody
    public Result uploadKafkaFile(KafkaFileDTO dto) {
        if (ValidateUtils.isNull(dto.getModify()) || !dto.getModify()) {
            return Result.buildFrom(kafkaFileService.uploadKafkaFile(dto, SpringTool.getUserName()));
        }
        return Result.buildFrom(kafkaFileService.modifyKafkaFile(dto, SpringTool.getUserName()));
    }

    @ApiOperation(value = "删除文件", notes = "")
    @RequestMapping(value = "kafka-files", method = RequestMethod.DELETE)
    @ResponseBody
    public Result deleteKafkaFile(@RequestParam("id") Long id) {
        return Result.buildFrom(kafkaFileService.deleteKafkaFile(id));
    }

    @ApiOperation(value = "文件列表", notes = "")
    @RequestMapping(value = "kafka-files", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<KafkaFileVO>> getKafkaFiles() {
        List<KafkaFileDO> kafkaFileDOList = kafkaFileService.getKafkaFiles();
        return new Result<>(KafkaFileConverter.convertKafkaFileVOList(kafkaFileDOList, clusterService));
    }

    @Deprecated
    @ApiOperation(value = "文件下载", notes = "")
    @RequestMapping(value = "kafka-files/{fileId}/config-files", method = RequestMethod.GET)
    public Result downloadKafkaFile(@PathVariable("fileId") Long fileId, HttpServletResponse response) {
        Result<MultipartFile> multipartFileResult = kafkaFileService.downloadKafkaFile(fileId);

        if (multipartFileResult.failed() || ValidateUtils.isNull(multipartFileResult.getData())) {
            return multipartFileResult;
        }

        InputStream is = null;
        try {
            response.setContentType(multipartFileResult.getData().getContentType());
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(multipartFileResult.getData().getOriginalFilename(), "UTF-8"));
            is = multipartFileResult.getData().getInputStream();
            IOUtils.copy(is, response.getOutputStream());
        } catch (Exception e) {
            LOGGER.error("class=RdKafkaFileController||method=downloadKafkaFile||fileId={}||errMsg={}||msg=modify response failed", fileId, e.getMessage());
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
            }
        }
        return Result.buildSuc();
    }
}