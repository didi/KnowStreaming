package com.xiaojukeji.kafka.manager.web.api.versionone.rd;

import com.xiaojukeji.kafka.manager.common.bizenum.OffsetLocationEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.consumer.ConsumerGroupVO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.ConsumerService;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.web.converters.ConsumerModelConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/4/21
 */
@Api(tags = "RD-Consumer维度相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_RD_PREFIX)
public class RdConsumerController {
    @Autowired
    private ConsumerService consumerService;

    @Autowired
    private ClusterService clusterService;

    @ApiOperation(value = "集群ConsumerGroup列表", notes="")
    @RequestMapping(value = "{clusterId}/consumer-groups", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<ConsumerGroupVO>> getConsumerGroupList(@PathVariable Long clusterId) {
        return new Result<>(
                ConsumerModelConverter.convert2ConsumerGroupVOList(consumerService.getConsumerGroupList(clusterId))
        );
    }

    @ApiOperation(value = "消费组消费的Topic列表", notes = "")
    @RequestMapping(value = "{clusterId}/consumer-groups/{consumerGroup}/topics", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<String>> getConsumerGroupConsumedTopicList(@PathVariable Long clusterId,
                                                                  @PathVariable String consumerGroup,
                                                                  @RequestParam("location") String location) {
        ClusterDO clusterDO = clusterService.getById(clusterId);
        if (ValidateUtils.isNull(clusterDO)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }

        OffsetLocationEnum offsetLocation = OffsetLocationEnum.getOffsetStoreLocation(location.toLowerCase());
        if (ValidateUtils.isNull(offsetLocation)) {
            return Result.buildFrom(ResultStatus.CG_LOCATION_ILLEGAL);
        }
        return new Result<>(
                consumerService.getConsumerGroupConsumedTopicList(clusterId, consumerGroup, offsetLocation.location)
        );
    }
}