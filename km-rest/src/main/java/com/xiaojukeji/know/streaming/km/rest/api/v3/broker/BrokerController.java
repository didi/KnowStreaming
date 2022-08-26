package com.xiaojukeji.know.streaming.km.rest.api.v3.broker;

import com.xiaojukeji.know.streaming.km.biz.broker.BrokerManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.Broker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.vo.broker.BrokerBasicVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.log.LogDirVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metadata.BrokerMetadataCombineExistVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metadata.BrokerMetadataVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zengqiao
 * @date 22/02/23
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "Broker-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX)
public class BrokerController {

    @Autowired
    private BrokerService brokerService;

    @Autowired
    private BrokerManager brokerManager;

    @ApiOperation(value = "Broker元信息", notes = "")
    @GetMapping(value = "clusters/{clusterPhyId}/brokers/{brokerId}/metadata")
    @ResponseBody
    public Result<BrokerMetadataVO> getBrokerMetadata(@PathVariable Long clusterPhyId, @PathVariable Integer brokerId) {
        Broker broker = brokerService.getBroker(clusterPhyId, brokerId);
        if (broker == null) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getBrokerNotExist(clusterPhyId, brokerId));
        }

        return Result.buildSuc(new BrokerMetadataVO(broker.getBrokerId(), broker.getHost()));
    }

    @ApiOperation(value = "Broker元信息", notes = "带是否存在及是否存活")
    @GetMapping(value = "clusters/{clusterPhyId}/brokers/{brokerId}/metadata-combine-exist")
    @ResponseBody
    public Result<BrokerMetadataCombineExistVO> getBrokerMetadataCombineExist(@PathVariable Long clusterPhyId, @PathVariable Integer brokerId) {
        Broker broker = brokerService.getBroker(clusterPhyId, brokerId);
        if (broker == null) {
            BrokerMetadataCombineExistVO vo = new BrokerMetadataCombineExistVO();
            vo.setBrokerId(brokerId);
            vo.setAlive(false);
            vo.setExist(false);
            return Result.buildSuc(vo);
        }

        return Result.buildSuc(new BrokerMetadataCombineExistVO(broker.getBrokerId(), broker.getHost(), true, broker.alive()));
    }

    @ApiOperation(value = "Broker基本信息", notes = "")
    @GetMapping(value = "clusters/{clusterPhyId}/brokers/{brokerId}/basic")
    @ResponseBody
    public Result<BrokerBasicVO> getBrokerBasic(@PathVariable Long clusterPhyId, @PathVariable Integer brokerId) {
        return brokerManager.getBrokerBasic(clusterPhyId, brokerId);
    }

    @ApiOperation(value = "BrokerLogs信息", notes = "")
    @GetMapping(value = "clusters/{clusterPhyId}/brokers/{brokerId}/log-dirs")
    @ResponseBody
    public PaginationResult<LogDirVO> getBrokerLogDirs(@PathVariable Long clusterPhyId,
                                                       @PathVariable Integer brokerId,
                                                       PaginationBaseDTO dto) {
        return brokerManager.getBrokerLogDirs(clusterPhyId, brokerId, dto);
    }
}
