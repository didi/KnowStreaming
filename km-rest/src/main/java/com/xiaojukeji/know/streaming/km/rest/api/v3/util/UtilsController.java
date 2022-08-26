package com.xiaojukeji.know.streaming.km.rest.api.v3.util;

import com.xiaojukeji.know.streaming.km.common.bean.dto.util.ValidateKafkaDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.entity.util.KafkaValidate;
import com.xiaojukeji.know.streaming.km.common.bean.vo.util.KafkaBSValidateVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterValidateService;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaAdminZKClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import kafka.zk.KafkaZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Properties;

/**
 * @author zengqiao
 * @date 22/02/22
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "Utils-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX)
public class UtilsController {
    private static final Logger log = LoggerFactory.getLogger(UtilsController.class);

    @Autowired
    private KafkaAdminZKClient kafkaAdminZKClient;

    @Autowired
    private ClusterValidateService clusterValidateService;

    @ApiOperation(value = "Kafka地址校验", notes = "")
    @PostMapping(value = "utils/kafka-validator")
    @ResponseBody
    public Result<KafkaBSValidateVO> validateKafka(@RequestBody ValidateKafkaDTO dto) {
        Result<KafkaValidate> rkv = this.clusterValidateService.checkKafkaLegal(
                dto.getBootstrapServers(),
                dto.getClientProperties() == null? new Properties(): dto.getClientProperties(),
                dto.getZookeeper());
        if (rkv.failed()) {
            return Result.buildFromIgnoreData(rkv);
        }

        return Result.buildSuc(ConvertUtil.obj2Obj(rkv.getData(), KafkaBSValidateVO.class));
    }

    @ApiOperation(value = "查看ZK信息")
    @GetMapping(value = "utils/zookeeper-data")
    @ResponseBody
    public Result<Object> getZKData(@RequestParam("clusterPhyId") Long clusterPhyId,
                                    @RequestParam String cmd,
                                    @RequestParam String path,
                                    @RequestParam Integer version) throws Exception {
        KafkaZkClient kafkaZkClient = kafkaAdminZKClient.getClient(clusterPhyId);
        if (kafkaZkClient == null) {
            return Result.buildFrom(ResultStatus.NOT_EXIST);
        }

        try {
            switch (cmd) {
                case "ls":
                    return Result.buildSuc(kafkaZkClient.currentZooKeeper().getChildren(path, false));
                case "get":
                    return Result.buildSuc(new String(kafkaZkClient.currentZooKeeper().getData(path, null, null)));
                case "del":
                    kafkaZkClient.currentZooKeeper().delete(path, version);
                    return Result.buildSuc();
                default:
                    return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "cmd only support ls and get");
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();

            log.error("method=getZKData||clusterPhyId={}||cmd={}||path={}||errMsg=read failed.", clusterPhyId, cmd, path, ie);
            return Result.buildFromRSAndMsg(ResultStatus.ZK_OPERATE_FAILED, ie.getMessage());
        } catch (Exception e) {
            log.error("method=getZKData||clusterPhyId={}||cmd={}||path={}||errMsg=read failed.", clusterPhyId, cmd, path, e);
            return Result.buildFromRSAndMsg(ResultStatus.ZK_OPERATE_FAILED, e.getMessage());
        }
    }
}
