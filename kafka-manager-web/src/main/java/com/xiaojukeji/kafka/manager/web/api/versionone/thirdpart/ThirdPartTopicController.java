package com.xiaojukeji.kafka.manager.web.api.versionone.thirdpart;

import com.xiaojukeji.kafka.manager.common.bizenum.TopicOffsetChangedEnum;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.KafkaMetricsCollections;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.gateway.TopicQuota;
import com.xiaojukeji.kafka.manager.common.entity.dto.normal.TopicQuotaDTO;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BaseMetrics;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AuthorityDO;
import com.xiaojukeji.kafka.manager.common.entity.vo.common.RealTimeMetricsVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.cluster.TopicMetadataVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.consumer.ConsumerGroupVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic.TopicAuthorizedAppVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic.TopicRequestTimeDetailVO;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.TopicMetadata;
import com.xiaojukeji.kafka.manager.openapi.common.dto.TopicAuthorityDTO;
import com.xiaojukeji.kafka.manager.openapi.common.vo.TopicOffsetChangedVO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.*;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AuthorityService;
import com.xiaojukeji.kafka.manager.service.service.gateway.QuotaService;
import com.xiaojukeji.kafka.manager.web.converters.CommonModelConverter;
import com.xiaojukeji.kafka.manager.web.converters.ConsumerModelConverter;
import com.xiaojukeji.kafka.manager.web.converters.TopicModelConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zengqiao
 * @date 20/7/24
 */
@Api(tags = "开放接口-Topic相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_THIRD_PART_PREFIX)
public class ThirdPartTopicController {
    private final static Logger LOGGER = LoggerFactory.getLogger(ThirdPartTopicController.class);

    @Autowired
    private TopicService topicService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ConsumerService consumerService;

    @Autowired
    private TopicManagerService topicManagerService;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private QuotaService quotaService;

    @Autowired
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @Autowired
    private AppService appService;

    @ApiOperation(value = "Topic元信息", notes = "LogX调用")
    @RequestMapping(value = "clusters/{clusterId}/topics/{topicName}/metadata", method = RequestMethod.GET)
    @ResponseBody
    public Result<TopicMetadataVO> getTopicMetadata(@PathVariable Long clusterId, @PathVariable String topicName) {
        TopicMetadata topicMetadata = PhysicalClusterMetadataManager.getTopicMetadata(clusterId, topicName);
        if (ValidateUtils.isNull(topicMetadata)) {
            return Result.buildFrom(ResultStatus.TOPIC_NOT_EXIST);
        }
        TopicMetadataVO vo = new TopicMetadataVO();
        vo.setTopicName(topicMetadata.getTopic());
        vo.setPartitionNum(topicMetadata.getPartitionNum());
        return new Result<>(vo);
    }

    @ApiOperation(value = "Topic是否有流量", notes = "")
    @RequestMapping(value = "{physicalClusterId}/topics/{topicName}/offset-changed", method = RequestMethod.GET)
    @ResponseBody
    public Result<TopicOffsetChangedVO> checkTopicExpired(@PathVariable Long physicalClusterId,
                                                          @PathVariable String topicName,
                                                          @RequestParam("latest-time") Long latestTime) {
        Result<TopicOffsetChangedEnum> enumResult =
                topicService.checkTopicOffsetChanged(physicalClusterId, topicName, latestTime);
        if (!Constant.SUCCESS.equals(enumResult.getCode())) {
            return new Result<>(enumResult.getCode(), enumResult.getMessage());
        }
        return new Result<>(new TopicOffsetChangedVO(enumResult.getData().getCode()));
    }

    @ApiOperation(value = "Topic实时流量信息", notes = "")
    @RequestMapping(value = "{physicalClusterId}/topics/{topicName}/metrics", method = RequestMethod.GET)
    @ResponseBody
    public Result<RealTimeMetricsVO> getTopicMetrics(@PathVariable Long physicalClusterId,
                                                     @PathVariable String topicName) {
        return new Result<>(CommonModelConverter.convert2RealTimeMetricsVO(
                topicService.getTopicMetricsFromJMX(
                        physicalClusterId,
                        topicName,
                        KafkaMetricsCollections.COMMON_DETAIL_METRICS,
                        true
                )
        ));
    }

    @ApiOperation(value = "Topic实时请求耗时信息", notes = "")
    @RequestMapping(value = "{physicalClusterId}/topics/{topicName}/request-time", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<TopicRequestTimeDetailVO>> getTopicRequestMetrics(@PathVariable Long physicalClusterId,
                                                                         @PathVariable String topicName) {
        BaseMetrics metrics = topicService.getTopicMetricsFromJMX(
                physicalClusterId,
                topicName,
                KafkaMetricsCollections.TOPIC_REQUEST_TIME_DETAIL_PAGE_METRICS,
                false
        );
        return new Result<>(TopicModelConverter.convert2TopicRequestTimeDetailVOList(metrics));
    }

    @ApiOperation(value = "查询Topic的消费组列表", notes = "")
    @RequestMapping(value = "{physicalClusterId}/topics/{topicName}/consumer-groups", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<ConsumerGroupVO>> getConsumeDetail(@PathVariable Long physicalClusterId,
                                                          @PathVariable String topicName) {
        ClusterDO clusterDO = clusterService.getById(physicalClusterId);
        if (ValidateUtils.isNull(clusterDO)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }

        return new Result<>(ConsumerModelConverter.convert2ConsumerGroupVOList(
                consumerService.getConsumerGroupList(physicalClusterId, topicName)
        ));
    }

    @ApiOperation(value = "Topic应用信息", notes = "")
    @RequestMapping(value = "{physicalClusterId}/topics/{topicName}/apps", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<TopicAuthorizedAppVO>> getTopicAppIds(@PathVariable Long physicalClusterId,
                                                   @PathVariable String topicName) {
        return new Result<>(TopicModelConverter.convert2TopicAuthorizedAppVOList(
                topicManagerService.getTopicAuthorizedApps(physicalClusterId, topicName))
        );
    }

    @ApiOperation(value = "配额调整",notes = "配额调整")
    @RequestMapping(value = "{topics/quota/add}",method = RequestMethod.POST)
    @ResponseBody
    public Result addTopicQuota(@RequestBody TopicQuotaDTO dto) {
        //非空校验
        if (ValidateUtils.isNull(dto) || !dto.paramLegal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        //获取物理集群id
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(dto.getClusterId());
        if (ValidateUtils.isNull(physicalClusterId)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        //权限判断(access 0:无权限, 1:读, 2:写, 3:读写)
        AuthorityDO authority = authorityService.getAuthority(physicalClusterId, dto.getTopicName(), dto.getAppId());
        if (ValidateUtils.isNull(authority) || authority.getAccess() == 0) {
            return Result.buildFrom(ResultStatus.USER_WITHOUT_AUTHORITY);
        }
        if (authority.getAccess() == 1) {
            //可以消费
            dto.setProduceQuota(null);
        }
        if (authority.getAccess() == 2) {
            //可以生产
            dto.setConsumeQuota(null);
        }
        //构建topicquota
        TopicQuota topicQuotaDO = new TopicQuota();
        topicQuotaDO.setAppId(dto.getAppId());
        topicQuotaDO.setClusterId(physicalClusterId);
        topicQuotaDO.setTopicName(dto.getTopicName());
        topicQuotaDO.setConsumeQuota(dto.getConsumeQuota());
        topicQuotaDO.setProduceQuota(dto.getProduceQuota());
        //添加配额
        if (quotaService.addTopicQuota(topicQuotaDO) > 0) {
            return Result.buildFrom(ResultStatus.SUCCESS);
        }
        return Result.buildFrom(ResultStatus.MYSQL_ERROR);
    }

    @ApiOperation(value = "权限调整",notes = "权限调整")
    @RequestMapping(value = "{topics/authority/add}",method = RequestMethod.POST)
    @ResponseBody
    public Result addAuthority(@RequestBody TopicAuthorityDTO dto) {
        //非空校验
        if (ValidateUtils.isNull(dto) || !dto.paramLegal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        //查询该用户拥有的应用
        List<AppDO> appDOs = appService.getByPrincipal(SpringTool.getUserName());
        if (ValidateUtils.isEmptyList(appDOs)) {
            //该用户无应用，需要先申请应用
            return Result.buildFrom(ResultStatus.APP_NOT_EXIST);
        }
        List<Long> appIds = appDOs.stream().map(AppDO::getId).collect(Collectors.toList());
        if (!appIds.contains(dto.getAccess())) {
            //入参中的appId，该用户未拥有
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        //获取物理集群id
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(dto.getClusterId());
        if (ValidateUtils.isNull(physicalClusterId)) {
            //集群不存在
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        //获取集群信息
        ClusterDO clusterDO = clusterService.getById(physicalClusterId);
        if (ValidateUtils.isNull(clusterDO)) {
            //集群不存在
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        TopicDO topic = topicManagerService.getByTopicName(physicalClusterId, dto.getTopicName());
        if (ValidateUtils.isNull(topic)) {
            //topic不存在
            return Result.buildFrom(ResultStatus.TOPIC_NOT_EXIST);
        }
        //构建authorityDo
        AuthorityDO authorityDO = new AuthorityDO();
        authorityDO.setClusterId(physicalClusterId);
        authorityDO.setAppId(dto.getAppId());
        authorityDO.setTopicName(dto.getTopicName());
        authorityDO.setAccess(dto.getAccess());
        if (authorityService.addAuthority(authorityDO) > 0) {
            return Result.buildFrom(ResultStatus.SUCCESS);
        }
        return Result.buildFrom(ResultStatus.MYSQL_ERROR);
    }
}
