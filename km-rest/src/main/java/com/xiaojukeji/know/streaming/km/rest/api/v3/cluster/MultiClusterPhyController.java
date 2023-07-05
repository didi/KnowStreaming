package com.xiaojukeji.know.streaming.km.rest.api.v3.cluster;

import com.xiaojukeji.know.streaming.km.biz.cluster.MultiClusterPhyManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.cluster.MultiClusterDashboardDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.ClusterPhyBaseVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.ClusterPhysHealthStateVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.ClusterPhysStateVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.ClusterPhyDashboardVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author zengqiao
 * @date 22/02/21
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "集群多Phy-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX)
public class MultiClusterPhyController {
    @Autowired
    private MultiClusterPhyManager multiClusterPhyManager;

    @Autowired
    private ClusterPhyService clusterPhyService;

    @ApiOperation(value = "多物理集群-大盘", notes = "")
    @PostMapping(value = "physical-clusters/dashboard")
    @ResponseBody
    public PaginationResult<ClusterPhyDashboardVO> getClusterPhyDashboard(@RequestBody @Validated MultiClusterDashboardDTO dto) {
        return multiClusterPhyManager.getClusterPhysDashboard(dto);
    }

    @ApiOperation(value = "多物理集群-基本信息", notes = "")
    @GetMapping(value = "physical-clusters/basic")
    @ResponseBody
    public Result<List<ClusterPhyBaseVO>> getClusterPhyBasic() {
        return multiClusterPhyManager.getClusterPhysBasic();
    }

    @ApiOperation(value = "多物理集群-状态", notes = "")
    @GetMapping(value = "physical-clusters/state")
    @ResponseBody
    public Result<ClusterPhysStateVO> getClusterPhysState() {
        return Result.buildSuc(ConvertUtil.obj2Obj(multiClusterPhyManager.getClusterPhysState(), ClusterPhysStateVO.class));
    }

    @ApiOperation(value = "多物理集群-健康状态", notes = "")
    @GetMapping(value = "physical-clusters/health-state")
    @ResponseBody
    public Result<ClusterPhysHealthStateVO> getClusterPhysHealthState() {
        return Result.buildSuc(ConvertUtil.obj2Obj(multiClusterPhyManager.getClusterPhysHealthState(), ClusterPhysHealthStateVO.class));
    }

    @ApiOperation(value = "多物理集群-已存在kafka版本", notes = "")
    @GetMapping(value = "physical-clusters/exist-version")
    public Result<List<String>> getClusterPhysVersion() {
        return Result.buildSuc(clusterPhyService.getClusterVersionList());
    }
}
