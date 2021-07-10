package com.xiaojukeji.kafka.manager.web.api.versionone.normal;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.normal.TopicModifyDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.normal.TopicRetainDTO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic.TopicExpiredVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic.TopicMineVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic.TopicVO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.TopicExpiredService;
import com.xiaojukeji.kafka.manager.service.service.TopicManagerService;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.web.utils.ResultCache;
import com.xiaojukeji.kafka.manager.web.converters.TopicMineConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/3/31
 */
@Api(tags = "Normal-Topic操作相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX)
public class NormalTopicMineController {
    @Autowired
    private TopicExpiredService topicExpiredService;

    @Autowired
    private TopicManagerService topicManagerService;

    @Autowired
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @ApiOperation(value = "我的Topic", notes = "")
    @RequestMapping(value = "topics/mine", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<TopicMineVO>> getMineTopics() {
        return new Result<>(TopicMineConverter.convert2TopicMineVOList(
                topicManagerService.getMyTopics(SpringTool.getUserName())
        ));
    }

    @ApiOperation(value = "全部Topic", notes = "")
    @RequestMapping(value = "topics", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<TopicVO>> getTopics() {
        Result r = ResultCache.get(ApiPrefix.API_V1_NORMAL_PREFIX + "topics");
        if (ValidateUtils.isNull(r) || !Constant.SUCCESS.equals(r.getCode())) {
            r = new Result<>(TopicMineConverter.convert2TopicVOList(
                    topicManagerService.getTopics(SpringTool.getUserName())
            ));
            ResultCache.put(ApiPrefix.API_V1_NORMAL_PREFIX + "topics", r);
        }
        return r;

    }

    @ApiOperation(value = "修改Topic信息", notes = "延长保留, 修改基本信息")
    @RequestMapping(value = "topics", method = RequestMethod.PUT)
    @ResponseBody
    public Result modifyTopic(@RequestBody TopicModifyDTO dto) {
        if (!dto.paramLegal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(dto.getClusterId());
        if (ValidateUtils.isNull(physicalClusterId)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        return Result.buildFrom(
                topicManagerService.modifyTopic(
                        physicalClusterId,
                        dto.getTopicName(),
                        dto.getDescription(),
                        SpringTool.getUserName()
                )
        );
    }

    @ApiOperation(value = "过期Topic信息", notes = "")
    @RequestMapping(value = "topics/expired", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<TopicExpiredVO>> getExpiredTopics() {
        return new Result<>(TopicMineConverter.convert2TopicExpiredVOList(
               topicExpiredService.getExpiredTopicDataList(SpringTool.getUserName())
        ));
    }

    @ApiOperation(value = "过期Topic保留", notes = "")
    @RequestMapping(value = "topics/expired", method = RequestMethod.PUT)
    @ResponseBody
    public Result retainExpiredTopic(@RequestBody TopicRetainDTO dto) {
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(dto.getClusterId());
        if (ValidateUtils.isNull(physicalClusterId)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        return Result.buildFrom(
                topicExpiredService.retainExpiredTopic(physicalClusterId, dto.getTopicName(), dto.getRetainDays())
        );
    }
}