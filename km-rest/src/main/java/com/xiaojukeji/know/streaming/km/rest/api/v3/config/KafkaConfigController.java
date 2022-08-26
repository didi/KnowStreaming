package com.xiaojukeji.know.streaming.km.rest.api.v3.config;

import com.didiglobal.logi.security.util.HttpRequestUtil;
import com.xiaojukeji.know.streaming.km.biz.broker.BrokerConfigManager;
import com.xiaojukeji.know.streaming.km.biz.topic.TopicConfigManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.config.KafkaConfigModifyBrokerDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.config.KafkaConfigModifyTopicDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationMulPreciseFilterDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.kafkaconfig.KafkaTopicDefaultConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.kafkaconfig.KafkaConfigDetail;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.config.KafkaBrokerConfigModifyParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.config.KafkaTopicConfigParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.config.kafka.KafkaTopicDefaultConfigVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.config.kafka.KafkaBrokerConfigVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.config.kafka.KafkaTopicConfigVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.PaginationUtil;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author zengqiao
 * @date 22/02/24
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "KafkaConfig-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX)
public class KafkaConfigController {
    @Autowired
    private TopicConfigService kafkaConfigService;

    @Autowired
    private TopicConfigManager topicConfigManager;

    @Autowired
    private BrokerConfigManager brokerConfigManager;

    @ApiOperation(value = "Config-Broker配置查看", notes = "")
    @PostMapping(value = "clusters/{clusterPhyId}/config-brokers/{brokerId}/configs")
    @ResponseBody
    public PaginationResult<KafkaBrokerConfigVO> getConfigBroker(@PathVariable Long clusterPhyId,
                                                                 @PathVariable Integer brokerId,
                                                                 @RequestBody PaginationMulPreciseFilterDTO dto) {
        Result<List<KafkaBrokerConfigVO>> configResult = brokerConfigManager.getBrokerConfigDetail(clusterPhyId, brokerId);
        if (configResult.failed()) {
            return PaginationResult.buildFailure(configResult, dto);
        }

        return PaginationUtil.pageBySubData(
                PaginationUtil.pageByPreciseFilter(
                        PaginationUtil.pageByFuzzyFilter(configResult.getData(), dto.getSearchKeywords(), Arrays.asList("name")),
                        dto.getPreciseFilterDTOList()
                ),
                dto
        );
    }

    @ApiOperation(value = "Config-Topic默认配置", notes = "")
    @GetMapping(value = "clusters/{clusterPhyId}/config-topics/default")
    @ResponseBody
    public Result<List<KafkaTopicDefaultConfigVO>> getDefaultTopicConfig(@PathVariable Long clusterPhyId) {
        Result<List<KafkaTopicDefaultConfig>> configResult = topicConfigManager.getDefaultTopicConfig(clusterPhyId);
        if (configResult.failed()) {
            return Result.buildFromIgnoreData(configResult);
        }

        return Result.buildSuc(ConvertUtil.list2List(configResult.getData(), KafkaTopicDefaultConfigVO.class));
    }

    @ApiOperation(value = "Config-Topic配置查看", notes = "")
    @PostMapping(value = "clusters/{clusterPhyId}/config-topics/{topicName}/configs")
    @ResponseBody
    public PaginationResult<KafkaTopicConfigVO> getConfigTopic(@PathVariable Long clusterPhyId,
                                                               @PathVariable String topicName,
                                                               @RequestBody PaginationMulPreciseFilterDTO dto) {
        Result<List<KafkaConfigDetail>> configResult = kafkaConfigService.getTopicConfigDetailFromKafka(clusterPhyId, topicName);
        if (configResult.failed()) {
            return PaginationResult.buildFailure(configResult, dto);
        }

        return PaginationUtil.pageBySubData(
                PaginationUtil.pageByPreciseFilter(
                        PaginationUtil.pageByFuzzyFilter(ConvertUtil.list2List(configResult.getData(), KafkaTopicConfigVO.class), dto.getSearchKeywords(), Arrays.asList("name")),
                        dto.getPreciseFilterDTOList()
                ),
                dto
        );
    }

    @ApiOperation(value = "Config-Broker配置修改", notes = "")
    @PutMapping(value = "config-brokers")
    @ResponseBody
    public Result<Void> modifyKafkaBrokerConfig(@Validated @RequestBody KafkaConfigModifyBrokerDTO dto) {
        return brokerConfigManager.modifyBrokerConfig(
                new KafkaBrokerConfigModifyParam(
                        dto.getClusterId(),
                        dto.getBrokerId(),
                        new HashMap<String, String>((Map) dto.getChangedProps()),
                        dto.getApplyAll()
                ),
                HttpRequestUtil.getOperator()
        );
    }

    @ApiOperation(value = "Config-Topic配置修改", notes = "")
    @PutMapping(value = "config-topics")
    @ResponseBody
    public Result<Void> modifyKafkaTopicConfig(@Validated @RequestBody KafkaConfigModifyTopicDTO dto) {
        return kafkaConfigService.modifyTopicConfig(
                new KafkaTopicConfigParam(dto.getClusterId(), dto.getTopicName(), (Map) dto.getChangedProps()),
                HttpRequestUtil.getOperator()
        );
    }
}
