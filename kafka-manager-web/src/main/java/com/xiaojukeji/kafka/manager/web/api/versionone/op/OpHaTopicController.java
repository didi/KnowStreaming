package com.xiaojukeji.kafka.manager.web.api.versionone.op;

import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.TopicOperationResult;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.topic.HaTopicRelationDTO;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import com.xiaojukeji.kafka.manager.service.biz.ha.HaTopicManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 高可用Topic操作相关接口
 * @author zengqiao
 * @date 21/5/18
 */
@Api(tags = "OP-HA-Topic操作相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_OP_PREFIX)
public class OpHaTopicController {

    @Autowired
    private HaTopicManager haTopicManager;

    @ApiOperation(value = "高可用Topic绑定")
    @PostMapping(value = "ha-topics")
    @ResponseBody
    public Result<List<TopicOperationResult>> batchCreateHaTopic(@Validated  @RequestBody HaTopicRelationDTO dto) {
        return haTopicManager.batchCreateHaTopic(dto, SpringTool.getUserName());
    }

    @ApiOperation(value = "高可用topic解绑")
    @DeleteMapping(value = "ha-topics")
    @ResponseBody
    public Result<List<TopicOperationResult>> batchRemoveHaTopic(@Validated @RequestBody HaTopicRelationDTO dto) {
        return haTopicManager.batchRemoveHaTopic(dto, SpringTool.getUserName());
    }
}
