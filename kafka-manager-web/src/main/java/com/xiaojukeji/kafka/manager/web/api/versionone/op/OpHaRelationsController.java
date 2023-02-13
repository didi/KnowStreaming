package com.xiaojukeji.kafka.manager.web.api.versionone.op;

import com.xiaojukeji.kafka.manager.common.bizenum.ha.HaResTypeEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.ha.HaStatusEnum;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.common.constant.KafkaConstant;
import com.xiaojukeji.kafka.manager.common.constant.MsgConstant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.HaASRelationDO;
import com.xiaojukeji.kafka.manager.common.utils.ConvertUtil;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.ha.HaASRelationService;
import com.xiaojukeji.kafka.manager.service.utils.HaTopicCommands;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;


/**
 * @author zengqiao
 * @date 20/4/23
 */
@Api(tags = "OP-HA-Relations维度相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_OP_PREFIX)
public class OpHaRelationsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpHaRelationsController.class);

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private HaASRelationService haASRelationService;

    @ApiOperation(value = "同步Kafka的HA关系到DB")
    @PostMapping(value = "ha-relations/{clusterPhyId}/dest-db")
    @ResponseBody
    public Result<Void> syncHaRelationsToDB(@PathVariable Long clusterPhyId) {
        // 从ZK获取Topic主备关系信息
        ClusterDO clusterDO = clusterService.getById(clusterPhyId);
        if (ValidateUtils.isNull(clusterDO)) {
            return Result.buildFromRSAndMsg(ResultStatus.CLUSTER_NOT_EXIST, MsgConstant.getClusterPhyNotExist(clusterPhyId));
        }

        Map<String, Properties> haTopicsConfigMap = HaTopicCommands.fetchAllHaTopicConfig(clusterDO);
        if (haTopicsConfigMap == null) {
            LOGGER.error("method=processTask||clusterPhyId={}||msg=fetch all ha topic config failed", clusterPhyId);
            return Result.buildFailure(ResultStatus.ZOOKEEPER_READ_FAILED);
        }

        // 获取当前集群的HA信息
        List<HaASRelationDO> doList = haTopicsConfigMap.entrySet()
                .stream()
                .map(elem -> getHaASRelation(clusterPhyId, elem.getKey(), elem.getValue()))
                .filter(relation -> relation != null)
                .collect(Collectors.toList());

        // 更新HA关系表
        Result<Void> rv = haASRelationService.replaceTopicRelationsToDB(clusterPhyId, doList);
        if (rv.failed()) {
            LOGGER.error("method=processTask||clusterPhyId={}||result={}||msg=replace topic relation failed", clusterPhyId, rv);
        }

        return rv;
    }

//    @ApiOperation(value = "同步DB的HA关系到Kafka")
//    @PostMapping(value = "ha-relations/{clusterPhyId}/dest-kafka")
//    @ResponseBody
//    public Result<Void> syncHaRelationsToKafka(@PathVariable Long clusterPhyId) {
//        // 从ZK获取Topic主备关系信息
//        ClusterDO clusterDO = clusterService.getById(clusterPhyId);
//        if (ValidateUtils.isNull(clusterDO)) {
//            return Result.buildFromRSAndMsg(ResultStatus.CLUSTER_NOT_EXIST, MsgConstant.getClusterPhyNotExist(clusterPhyId));
//        }
//
//        Map<String, Properties> haTopicsConfigMap = HaTopicCommands.fetchAllHaTopicConfig(clusterDO);
//        if (haTopicsConfigMap == null) {
//            LOGGER.error("method=processTask||clusterPhyId={}||msg=fetch all ha topic config failed", clusterPhyId);
//            return Result.buildFailure(ResultStatus.ZOOKEEPER_READ_FAILED);
//        }
//
//        // 获取当前集群的HA信息
//        List<HaASRelationDO> doList = haTopicsConfigMap.entrySet()
//                .stream()
//                .map(elem -> getHaASRelation(clusterPhyId, elem.getKey(), elem.getValue()))
//                .filter(relation -> relation != null)
//                .collect(Collectors.toList());
//
//        // 更新HA关系表
//        Result<Void> rv = haASRelationService.replaceTopicRelationsToDB(clusterPhyId, doList);
//        if (rv.failed()) {
//            LOGGER.error("method=processTask||clusterPhyId={}||result={}||msg=replace topic relation failed", clusterPhyId, rv);
//        }
//
//        return rv;
//    }

    private HaASRelationDO getHaASRelation(Long standbyClusterPhyId, String standbyTopicName, Properties props) {
        Long activeClusterPhyId = ConvertUtil.string2Long(props.getProperty(KafkaConstant.DIDI_HA_REMOTE_CLUSTER));
        if (activeClusterPhyId == null) {
            return null;
        }

        String activeTopicName = props.getProperty(KafkaConstant.DIDI_HA_REMOTE_TOPIC);
        if (activeTopicName == null) {
            activeTopicName = standbyTopicName;
        }

        return new HaASRelationDO(
                activeClusterPhyId,
                activeTopicName,
                standbyClusterPhyId,
                standbyTopicName,
                HaResTypeEnum.TOPIC.getCode(),
                HaStatusEnum.STABLE.getCode()
        );
    }
}