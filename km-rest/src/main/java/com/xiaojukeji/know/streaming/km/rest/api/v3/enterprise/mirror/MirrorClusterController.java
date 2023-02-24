package com.xiaojukeji.know.streaming.km.rest.api.v3.enterprise.mirror;

import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.ClusterPhyBaseVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zengqiao
 * @date 22/12/12
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "Mirror-集群-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_HA_MIRROR_PREFIX)
public class MirrorClusterController {

    @Autowired
    private ClusterPhyService clusterPhyService;

    @ApiOperation(value = "集群列表(支持Mirror)", notes = "")
    @GetMapping(value = "physical-clusters/basic")
    @ResponseBody
    public Result<List<ClusterPhyBaseVO>> listClusters() {
        List<ClusterPhy> clusterPhyList = clusterPhyService.listAllClusters().stream().filter(item -> item.getKafkaVersion().contains("2.5.0-d-")).collect(Collectors.toList());
        return Result.buildSuc(ConvertUtil.list2List(clusterPhyList, ClusterPhyBaseVO.class));
    }
}
