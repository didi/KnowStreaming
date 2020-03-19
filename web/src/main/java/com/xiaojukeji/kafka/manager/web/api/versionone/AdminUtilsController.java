package com.xiaojukeji.kafka.manager.web.api.versionone;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiaojukeji.kafka.manager.web.model.topic.AdminTopicModel;
import com.xiaojukeji.kafka.manager.web.model.topic.TopicDeleteModel;
import com.xiaojukeji.kafka.manager.common.constant.StatusCode;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.bizenum.AdminTopicStatusEnum;
import com.xiaojukeji.kafka.manager.common.entity.po.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.po.TopicDO;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.TopicMetadata;
import com.xiaojukeji.kafka.manager.service.cache.ClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.RegionService;
import com.xiaojukeji.kafka.manager.service.service.TopicManagerService;
import com.xiaojukeji.kafka.manager.service.service.ZookeeperService;
import com.xiaojukeji.kafka.manager.service.utils.SpringContextHolder;
import com.xiaojukeji.kafka.manager.service.service.impl.AdminTopicServiceImpl;
import com.xiaojukeji.kafka.manager.web.converters.AdminUtilConverter;
import com.xiaojukeji.kafka.manager.web.model.topic.AdminExpandTopicModel;
import com.xiaojukeji.kafka.manager.web.vo.topic.TopicDeleteVO;
import com.xiaojukeji.kafka.manager.web.vo.topic.TopicDetailVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 工具类
 * @author zengqiao
 * @date 2019-04-22
 */
@Api(value = "AdminUtilsController", description = "AdminUtil相关接口")
@Controller
@RequestMapping("api/v1/admin/")
public class AdminUtilsController {
    @Autowired
    private AdminTopicServiceImpl adminTopicService;

    @Autowired
    private TopicManagerService topicManagerService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private ZookeeperService zookeeperService;

    private static final Logger logger = LoggerFactory.getLogger(AdminUtilsController.class);

    @ApiOperation(value = "创建Topic", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = Result.class)
    @RequestMapping(value = {"utils/topic"}, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result createCommonTopic(@RequestBody AdminTopicModel reqObj) {
        if (reqObj == null || !reqObj.createParamLegal()) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal");
        }
        ClusterDO clusterDO = clusterService.getById(reqObj.getClusterId());
        if (clusterDO == null) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal, cluster not exist");
        }
        List<Integer> brokerIdList = regionService.getFullBrokerId(clusterDO.getId(), reqObj.getRegionIdList(), reqObj.getBrokerIdList());
        if (brokerIdList == null || brokerIdList.isEmpty()) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal, brokerIdList or regionIdList illegal");
        }

        Properties properties = null;
        if (StringUtils.isEmpty(reqObj.getProperties())) {
            properties = new Properties();
        } else {
            properties = JSON.parseObject(reqObj.getProperties(), Properties.class);
        }
        properties.put("retention.ms", String.valueOf(reqObj.getRetentionTime()));
        TopicDO topicDO = AdminUtilConverter.convert2TopicDO(reqObj);
        TopicMetadata topicMetadata = AdminUtilConverter.convert2TopicMetadata(reqObj.getTopicName(), reqObj.getPartitionNum(), reqObj.getReplicaNum(), brokerIdList);

        String operator = SpringContextHolder.getUserName();
        AdminTopicStatusEnum adminTopicStatusEnum = adminTopicService.createTopic(clusterDO, topicMetadata, topicDO, properties, operator);
        if (AdminTopicStatusEnum.SUCCESS.equals(adminTopicStatusEnum)) {
            return new Result();
        }
        return new Result(StatusCode.OPERATION_ERROR, adminTopicStatusEnum.getMessage());
    }

    @ApiOperation(value = "修改Topic", httpMethod = "PUT", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = Result.class)
    @RequestMapping(value = {"utils/topic/config"}, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result modifyTopic(@RequestBody AdminTopicModel reqObj) {
        if (reqObj == null || !reqObj.modifyConfigParamLegal()) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal, data is empty");
        }
        ClusterDO clusterDO = clusterService.getById(reqObj.getClusterId());
        if (clusterDO == null) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal, cluster not exist");
        }

        // 获取属性
        Properties properties = new Properties();
        try {
            if (!StringUtils.isEmpty(reqObj.getProperties())) {
                properties = JSONObject.parseObject(reqObj.getProperties(), Properties.class);
            }
            properties.setProperty("retention.ms", String.valueOf(reqObj.getRetentionTime()));
        } catch (Exception e) {
            logger.error("modifyTopic@AdminUtilsController, modify failed, req:{}.", reqObj, e);
            return new Result(StatusCode.PARAM_ERROR, "param illegal, properties illegal");
        }
        TopicDO topicDO = AdminUtilConverter.convert2TopicDO(reqObj);

        // 操作修改
        String operator = SpringContextHolder.getUserName();
        AdminTopicStatusEnum adminTopicStatusEnum = adminTopicService.modifyTopic(clusterDO, topicDO, properties, operator);
        if (AdminTopicStatusEnum.SUCCESS.equals(adminTopicStatusEnum)) {
            return new Result();
        }
        return new Result(StatusCode.OPERATION_ERROR, adminTopicStatusEnum.getMessage());
    }

    @ApiOperation(value = "删除Topic", httpMethod = "DELETE", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = TopicDeleteVO.class)
    @RequestMapping(value = {"utils/topic"}, method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<List<TopicDeleteVO>> deleteTopic(@RequestBody TopicDeleteModel reqObj) {
        if (reqObj == null || !reqObj.legal()) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal");
        }
        ClusterDO clusterDO = clusterService.getById(reqObj.getClusterId());
        if (clusterDO == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal, cluster not exist");
        }
        String operator = SpringContextHolder.getUserName();
        List<TopicDeleteVO> topicDeleteVOList = new ArrayList<>();
        for (String topicName: reqObj.getTopicNameList()) {
            if (StringUtils.isEmpty(topicName)) {
                topicDeleteVOList.add(new TopicDeleteVO(clusterDO.getId(), topicName, "topic name illegal", StatusCode.PARAM_ERROR));
                continue;
            }
            AdminTopicStatusEnum adminTopicStatusEnum = adminTopicService.deleteTopic(clusterDO, topicName, operator);
            if (AdminTopicStatusEnum.SUCCESS.equals(adminTopicStatusEnum)) {
                topicDeleteVOList.add(new TopicDeleteVO(clusterDO.getId(), topicName, adminTopicStatusEnum.getMessage(), StatusCode.SUCCESS));
            } else {
                topicDeleteVOList.add(new TopicDeleteVO(clusterDO.getId(), topicName, adminTopicStatusEnum.getMessage(), StatusCode.OPERATION_ERROR));
            }
        }
        return new Result<>(topicDeleteVOList);
    }

    @ApiOperation(value = "Topic扩容", httpMethod = "PUT", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = Result.class)
    @RequestMapping(value = {"utils/topic/dilatation"}, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result expandTopic(@RequestBody AdminExpandTopicModel reqObj) {
        if (reqObj == null || !reqObj.legal()) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal");
        }
        ClusterDO clusterDO = clusterService.getById(reqObj.getClusterId());
        if (clusterDO == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal, cluster not exist");
        }
        if (!ClusterMetadataManager.isTopicExist(clusterDO.getId(), reqObj.getTopicName())) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal, topic not exist");
        }
        List<Integer> brokerIdList = regionService.getFullBrokerId(reqObj.getClusterId(), reqObj.getRegionIdList(), reqObj.getBrokerIdList());
        if (brokerIdList == null || brokerIdList.isEmpty()) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal, brokerId or regionId illegal");
        }
        TopicMetadata topicMetadata = AdminUtilConverter.convert2TopicMetadata(reqObj.getTopicName(), reqObj.getPartitionNum(), -1, brokerIdList);
        AdminTopicStatusEnum adminTopicStatusEnum = adminTopicService.expandTopic(clusterDO, topicMetadata, SpringContextHolder.getUserName());
        if (AdminTopicStatusEnum.SUCCESS.equals(adminTopicStatusEnum)) {
            return new Result();
        }
        return new Result(StatusCode.OPERATION_ERROR, adminTopicStatusEnum.getMessage());
    }

    @ApiOperation(value = "Topic详情", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = TopicDetailVO.class)
    @RequestMapping(value = {"utils/{clusterId}/topics/{topicName}/detail"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<TopicDetailVO> getTopicDetail(@PathVariable Long clusterId, @PathVariable String topicName) {
        if (clusterId == null || clusterId <= 0 || StringUtils.isEmpty(topicName)) {
            return new Result<>(StatusCode.PARAM_ERROR, "params illegal");
        }
        ClusterDO clusterDO = clusterService.getById(clusterId);
        if (clusterDO == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal, cluster not exist");
        }
        TopicMetadata topicMetadata = ClusterMetadataManager.getTopicMetaData(clusterId, topicName);
        if (topicMetadata == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal, topic not exist");
        }
        TopicDO topicDO = topicManagerService.getByTopicName(clusterId, topicName);
        Properties properties = null;
        try {
            properties = zookeeperService.getTopicProperties(clusterId, topicName);
        } catch (Exception e) {
            logger.error("");
        }
        return new Result<>(AdminUtilConverter.convert2TopicDetailVO(clusterDO, topicMetadata, properties, topicDO));
    }
}
