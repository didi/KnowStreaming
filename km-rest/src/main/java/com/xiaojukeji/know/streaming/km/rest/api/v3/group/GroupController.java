package com.xiaojukeji.know.streaming.km.rest.api.v3.group;

import com.didiglobal.logi.security.util.HttpRequestUtil;
import com.xiaojukeji.know.streaming.km.biz.group.GroupManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.group.GroupOffsetDeleteDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.group.GroupOffsetResetDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.group.GroupTopicConsumedDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.po.group.GroupMemberPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.group.GroupTopicConsumedDetailVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metadata.GroupMetadataCombineExistVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.core.service.group.GroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author zengqiao
 * @date 22/02/22
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "Group-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX)
public class GroupController {
    @Autowired
    private GroupManager groupManager;

    @Autowired
    private GroupService groupService;

    @ApiOperation(value = "重置消费偏移", notes = "")
    @PutMapping(value = "group-offsets")
    @ResponseBody
    public Result<Void> resetGroupOffsets(@Validated @RequestBody GroupOffsetResetDTO dto) throws Exception {
        return groupManager.resetGroupOffsets(dto, HttpRequestUtil.getOperator());
    }

    @ApiOperation(value = "删除消费偏移", notes = "")
    @DeleteMapping(value = "group-offsets")
    @ResponseBody
    public Result<Void> deleteGroupOffsets(@Validated @RequestBody GroupOffsetDeleteDTO dto) throws Exception {
        return groupManager.deleteGroupOffsets(dto, HttpRequestUtil.getOperator());
    }

    @ApiOperation(value = "Group-Topic指标信息", notes = "")
    @PostMapping(value = "clusters/{clusterId}/topics/{topicName}/groups/{groupName}/metric")
    @ResponseBody
    public PaginationResult<GroupTopicConsumedDetailVO> getTopicGroupMetric(@PathVariable Long clusterId,
                                                                            @PathVariable String topicName,
                                                                            @PathVariable String groupName,
                                                                            @RequestBody GroupTopicConsumedDTO dto) throws Exception {
        return groupManager.pagingGroupTopicConsumedMetrics(clusterId, topicName, groupName, dto.getLatestMetricNames(), dto);
    }

    @ApiOperation(value = "Group元信息", notes = "带是否存在信息")
    @GetMapping(value = "clusters/{clusterPhyId}/groups/{groupName}/topics/{topicName}/metadata-combine-exist")
    @ResponseBody
    public Result<GroupMetadataCombineExistVO> getGroupMetadataCombineExist(@PathVariable Long clusterPhyId,
                                                                            @PathVariable String groupName,
                                                                            @PathVariable String topicName) {
        GroupMemberPO po = groupService.getGroupTopicFromDB(clusterPhyId, groupName, topicName);
        if (po == null) {
            return Result.buildSuc(new GroupMetadataCombineExistVO(clusterPhyId, groupName, topicName, false));
        }

        return Result.buildSuc(new GroupMetadataCombineExistVO(clusterPhyId, groupName, topicName, true));
    }
}
