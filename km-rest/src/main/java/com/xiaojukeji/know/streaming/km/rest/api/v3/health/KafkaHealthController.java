package com.xiaojukeji.know.streaming.km.rest.api.v3.health;

import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.health.HealthCheckConfigVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.health.HealthScoreBaseResultVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.health.HealthScoreResultDetailVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.converter.HealthScoreVOConverter;
import com.xiaojukeji.know.streaming.km.common.enums.config.ConfigGroupEnum;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckDimensionEnum;
import com.xiaojukeji.know.streaming.km.core.service.health.checkresult.HealthCheckResultService;
import com.xiaojukeji.know.streaming.km.core.service.health.state.HealthStateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author zengqiao
 * @date 22/02/23
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "KafkaHealth-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX)
public class KafkaHealthController {
    @Autowired
    private HealthStateService healthStateService;

    @Autowired
    private HealthCheckResultService healthCheckResultService;

    @ApiOperation(value = "集群-健康检查详情")
    @GetMapping(value = "clusters/{clusterPhyId}/health-detail")
    @ResponseBody
    public Result<List<HealthScoreResultDetailVO>> getClusterHealthCheckResultDetail(@PathVariable Long clusterPhyId,
                                                                                     @RequestParam(required = false) Integer dimensionCode) {
        HealthCheckDimensionEnum dimensionEnum = HealthCheckDimensionEnum.getByCode(dimensionCode);
        if (!dimensionEnum.equals(HealthCheckDimensionEnum.UNKNOWN)) {
            return Result.buildSuc(
                    HealthScoreVOConverter.convert2HealthScoreResultDetailVOList(
                            healthStateService.getDimensionHealthResult(clusterPhyId, Collections.singletonList(dimensionCode))
                    )
            );
        }

        return Result.buildSuc(
                HealthScoreVOConverter.convert2HealthScoreResultDetailVOList(
                        healthStateService.getAllDimensionHealthResult(clusterPhyId)
                )
        );
    }

    @ApiOperation(value = "集群-健康检查详情")
    @PostMapping(value = "clusters/{clusterPhyId}/health-detail")
    @ResponseBody
    public Result<List<HealthScoreResultDetailVO>> getClusterHealthCheckResultDetail(@PathVariable Long clusterPhyId,
                                                                                     @RequestBody List<Integer> dimensionCodeList) {
        if (dimensionCodeList.isEmpty()) {
            return Result.buildSuc(
                    HealthScoreVOConverter.convert2HealthScoreResultDetailVOList(
                            healthStateService.getAllDimensionHealthResult(clusterPhyId)
                    )
            );
        }

        return Result.buildSuc(HealthScoreVOConverter.convert2HealthScoreResultDetailVOList(
                healthStateService.getDimensionHealthResult(clusterPhyId, dimensionCodeList)
        ));
    }

    @ApiOperation(value = "具体资源-健康检查详情")
    @GetMapping(value = "clusters/{clusterPhyId}/dimensions/{dimensionCode}/resources/{resName}/health-detail")
    @ResponseBody
    public Result<List<HealthScoreBaseResultVO>> getClusterResHealthCheckResult(@PathVariable Long clusterPhyId,
                                                                                @PathVariable Integer dimensionCode,
                                                                                @PathVariable String resName) {
        return Result.buildSuc(HealthScoreVOConverter.convert2HealthScoreBaseResultVOList(
                healthStateService.getResHealthResult(clusterPhyId, clusterPhyId, dimensionCode, resName)
        ));
    }

    @ApiOperation(value = "健康检查配置")
    @GetMapping(value = "clusters/{clusterPhyId}/health-configs")
    @ResponseBody
    public Result<List<HealthCheckConfigVO>> getHealthCheckConfig(@PathVariable Long clusterPhyId) {
        return Result.buildSuc(
                HealthScoreVOConverter.convert2HealthCheckConfigVOList(
                        ConfigGroupEnum.HEALTH.name(),
                        new ArrayList<>(healthCheckResultService.getClusterHealthConfig(clusterPhyId).values())
        ));
    }
}
