package com.xiaojukeji.know.streaming.km.rest.api.v3.cluster;

import com.xiaojukeji.know.streaming.km.biz.cluster.MultiClusterPhyManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.cluster.MultiClusterDashboardDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.ClusterPhysStateVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.ClusterPhyDashboardVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


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

    @ApiOperation(value = "多物理集群-大盘", notes = "")
    @PostMapping(value = "physical-clusters/dashboard")
    @ResponseBody
    public PaginationResult<ClusterPhyDashboardVO> getClusterPhyBasic(@RequestBody @Validated MultiClusterDashboardDTO dto) {
        return multiClusterPhyManager.getClusterPhysDashboard(dto);
    }

    @ApiOperation(value = "多物理集群-状态", notes = "")
    @GetMapping(value = "physical-clusters/state")
    @ResponseBody
    public Result<ClusterPhysStateVO> getClusterPhysState() {
        return Result.buildSuc(ConvertUtil.obj2Obj(multiClusterPhyManager.getClusterPhysState(), ClusterPhysStateVO.class));
    }
}
