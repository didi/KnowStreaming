package com.xiaojukeji.know.streaming.km.rest.api.v3.topic;

import com.didiglobal.logi.security.util.HttpRequestUtil;
import com.xiaojukeji.know.streaming.km.biz.topic.OpTopicManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.topic.ClusterTopicDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.topic.TopicCreateDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.topic.TopicExpansionDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.topic.TopicParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.Topic;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metadata.TopicMetadataCombineExistVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metadata.TopicMetadataVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.converter.TopicVOConverter;
import com.xiaojukeji.know.streaming.km.core.service.topic.OpTopicService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author zengqiao
 * @date 22/02/22
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "Topic-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX)
public class TopicController {
    @Autowired
    private TopicService topicService;

    @Autowired
    private OpTopicManager opTopicManager;

    @Autowired
    private OpTopicService opTopicService;

    @ApiOperation(value = "Topic创建", notes = "")
    @PostMapping(value = "topics")
    @ResponseBody
    public Result<Void> createTopic(@Validated @RequestBody TopicCreateDTO dto) {
        return opTopicManager.createTopic(dto, HttpRequestUtil.getOperator());
    }

    @ApiOperation(value = "Topic删除", notes = "")
    @DeleteMapping(value ="topics")
    @ResponseBody
    public Result<Void> deleteTopics(@Validated @RequestBody ClusterTopicDTO dto) {
        return opTopicService.deleteTopic(new TopicParam(dto.getClusterId(), dto.getTopicName()), HttpRequestUtil.getOperator());
    }

    @ApiOperation(value = "Topic扩分区", notes = "")
    @PostMapping(value = "topics/expand-partitions")
    @ResponseBody
    public Result<Void> expandTopics(@Validated @RequestBody TopicExpansionDTO dto) {
        return opTopicManager.expandTopic(dto, HttpRequestUtil.getOperator());
    }

    @ApiOperation(value = "Topic数据清空", notes = "")
    @PostMapping(value = "topics/truncate-topic")
    @ResponseBody
    public Result<Void> truncateTopic(@Validated @RequestBody ClusterTopicDTO dto) {
        return opTopicManager.truncateTopic(dto.getClusterId(), dto.getTopicName(), HttpRequestUtil.getOperator());
    }

    @ApiOperation(value = "Topic元信息", notes = "带是否存在信息")
    @GetMapping(value = "clusters/{clusterPhyId}/topics/{topicName}/metadata-combine-exist")
    @ResponseBody
    public Result<TopicMetadataCombineExistVO> getTopicsMetadataCombineExist(@PathVariable Long clusterPhyId,
                                                                             @PathVariable String topicName) {
        Topic topic = topicService.getTopic(clusterPhyId, topicName);

        return Result.buildSuc(TopicVOConverter.convert2TopicMetadataCombineExistVO(topicName, topic));
    }

    @ApiOperation(value = "Topic元信息", notes = "")
    @GetMapping(value = "clusters/{clusterPhyId}/topics/{topicName}/metadata")
    @ResponseBody
    public Result<TopicMetadataVO> getTopicsMetadata(@PathVariable Long clusterPhyId,
                                                     @PathVariable String topicName) {
        Topic topic = topicService.getTopic(clusterPhyId, topicName);
        if (topic == null) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getTopicNotExist(clusterPhyId, topicName));
        }

        return Result.buildSuc(TopicVOConverter.convert2TopicMetadataVO(topic));
    }
}
