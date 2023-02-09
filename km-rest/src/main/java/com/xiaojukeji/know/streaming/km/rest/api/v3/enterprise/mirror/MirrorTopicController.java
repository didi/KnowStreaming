package com.xiaojukeji.know.streaming.km.rest.api.v3.enterprise.mirror;

import com.xiaojukeji.know.streaming.km.common.bean.dto.ha.mirror.MirrorTopicCreateDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.ha.mirror.MirrorTopicDeleteDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.ha.mirror.TopicMirrorInfoVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.ha.mirror.service.MirrorTopicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zengqiao
 * @date 22/12/12
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "Mirror-Topic-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_HA_MIRROR_PREFIX)
public class MirrorTopicController {

    @Autowired
    private MirrorTopicService mirrorTopicService;

    @ApiOperation(value = "批量创建Topic镜像", notes = "")
    @PostMapping(value = "topics")
    @ResponseBody
    public Result<Void> batchCreateMirrorTopic(@Validated @RequestBody List<MirrorTopicCreateDTO> dtoList) {
        return mirrorTopicService.batchCreateMirrorTopic(dtoList);
    }

    @ApiOperation(value = "批量删除Topic镜像", notes = "")
    @DeleteMapping(value = "topics")
    @ResponseBody
    public Result<Void> batchDeleteMirrorTopic(@Validated @RequestBody List<MirrorTopicDeleteDTO> dtoList) {
        return mirrorTopicService.batchDeleteMirrorTopic(dtoList);
    }

    @ApiOperation(value = "Topic镜像信息", notes = "")
    @GetMapping(value = "clusters/{clusterPhyId}/topics/{topicName}/mirror-info")
    @ResponseBody
    public Result<List<TopicMirrorInfoVO>> getTopicsMirrorInfo(@PathVariable Long clusterPhyId,
                                                               @PathVariable String topicName) {
        return mirrorTopicService.getTopicsMirrorInfo(clusterPhyId, topicName);
    }
}
