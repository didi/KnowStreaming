package com.xiaojukeji.know.streaming.km.rest.api.v3.enterprise.rebalance;


import com.didiglobal.logi.security.util.HttpRequestUtil;
import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.dto.ClusterBalanceOverviewDTO;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.dto.ClusterBalancePreviewDTO;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.dto.ClusterBalanceStrategyDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.vo.*;
import com.xiaojukeji.know.streaming.km.rebalance.core.service.ClusterBalanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@EnterpriseLoadReBalance
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "集群均衡-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX)
public class ClusterBalanceController {

    @Autowired
    private ClusterBalanceService clusterBalanceService;

    @ApiOperation(value = "集群均衡配置信息")
    @GetMapping(value = "clusters/{clusterPhyId}/balance-config")
    @ResponseBody
    public Result<ClusterBalanceJobConfigVO> balanceConfig(@PathVariable Long clusterPhyId) {
        return clusterBalanceService.config(clusterPhyId);
    }

    @ApiOperation(value = "集群周期均衡信息")
    @GetMapping(value = "clusters/{clusterPhyId}/balance-state")
    @ResponseBody
    public Result<ClusterBalanceStateVO> state(@PathVariable Long clusterPhyId) {
        return clusterBalanceService.state(clusterPhyId);
    }

    @ApiOperation(value = "集群均衡列表信息")
    @PostMapping(value = "clusters/{clusterPhyId}/balance-overview")
    @ResponseBody
    public PaginationResult<ClusterBalanceOverviewVO> overview(@PathVariable Long clusterPhyId,
                                                               @RequestBody ClusterBalanceOverviewDTO dto) {
        return clusterBalanceService.overview(clusterPhyId, dto);
    }

    @ApiOperation(value = "集群均衡历史信息")
    @PostMapping(value = "clusters/{clusterPhyId}/balance-history")
    @ResponseBody
    public PaginationResult<ClusterBalanceHistoryVO> history(@PathVariable Long clusterPhyId,
                                                             @RequestBody PaginationBaseDTO dto) {
        return clusterBalanceService.history(clusterPhyId, dto);
    }

    @ApiOperation(value = "集群均衡计划信息")
    @GetMapping(value = "clusters/{clusterPhyId}/balance-plan/{jobId}")
    @ResponseBody
    public Result<ClusterBalancePlanVO> plan(@PathVariable Long clusterPhyId, @PathVariable Long jobId) {
        return clusterBalanceService.plan(clusterPhyId, jobId);
    }

    @ApiOperation(value = "集群均衡状态信息")
    @GetMapping(value = "clusters/{clusterPhyId}/balance-schedule/{jobId}")
    @ResponseBody
    public Result<ClusterBalancePlanVO> schedule(@PathVariable Long clusterPhyId, @PathVariable Long jobId) {
        return clusterBalanceService.schedule(clusterPhyId, jobId);
    }

    @ApiOperation(value = "集群立即均衡预览信息")
    @PostMapping(value = "clusters/{clusterPhyId}/balance-preview")
    @ResponseBody
    public Result<ClusterBalancePlanVO> preview(@PathVariable Long clusterPhyId,
                                             @RequestBody ClusterBalancePreviewDTO clusterBalancePreviewDTO) {
        return clusterBalanceService.preview(clusterPhyId, clusterBalancePreviewDTO);
    }

    @ApiOperation(value = "集群均衡")
    @PostMapping(value = "clusters/{clusterPhyId}/balance-strategy")
    @ResponseBody
    public Result<Void> strategy(@PathVariable Long clusterPhyId, @RequestBody ClusterBalanceStrategyDTO dto) {
        return clusterBalanceService.strategy(clusterPhyId, dto, HttpRequestUtil.getOperator());
    }
}
