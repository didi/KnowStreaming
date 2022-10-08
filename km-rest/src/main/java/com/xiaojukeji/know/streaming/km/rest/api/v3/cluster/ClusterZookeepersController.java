package com.xiaojukeji.know.streaming.km.rest.api.v3.cluster;

import com.xiaojukeji.know.streaming.km.biz.cluster.ClusterZookeepersManager;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.zookeeper.ClusterZookeepersOverviewVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.bean.dto.cluster.ClusterZookeepersOverviewDTO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.zookeeper.ClusterZookeepersStateVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.zookeeper.ZnodeVO;
import com.xiaojukeji.know.streaming.km.core.service.zookeeper.ZnodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author zengqiao
 * @date 22/09/19
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "集群ZK-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX)
public class ClusterZookeepersController {
    @Autowired
    private ClusterZookeepersManager clusterZookeepersManager;

    @Autowired
    private ZnodeService znodeService;

    @ApiOperation("集群Zookeeper状态信息")
    @GetMapping(value = "clusters/{clusterPhyId}/zookeepers-state")
    public Result<ClusterZookeepersStateVO> getClusterZookeepersState(@PathVariable Long clusterPhyId) {
        return clusterZookeepersManager.getClusterPhyZookeepersState(clusterPhyId);
    }

    @ApiOperation("集群Zookeeper信息列表")
    @PostMapping(value = "clusters/{clusterPhyId}/zookeepers-overview")
    public PaginationResult<ClusterZookeepersOverviewVO> getClusterZookeepersOverview(@PathVariable Long clusterPhyId,
                                                                                      @RequestBody ClusterZookeepersOverviewDTO dto) {
        return clusterZookeepersManager.getClusterPhyZookeepersOverview(clusterPhyId, dto);
    }

    @ApiOperation("Zookeeper节点数据")
    @GetMapping(value = "clusters/{clusterPhyId}/znode-data")
    public Result<ZnodeVO> getClusterZookeeperData(@PathVariable Long clusterPhyId,
                                                   @RequestParam String path) {
        return clusterZookeepersManager.getZnodeVO(clusterPhyId, path);
    }

    @ApiOperation("Zookeeper节点列表")
    @GetMapping(value = "clusters/{clusterPhyId}/znode-children")
    public Result<List<String>> getClusterZookeeperChild(@PathVariable Long clusterPhyId,
                                                         @RequestParam String path,
                                                         @RequestParam(required = false) String keyword) {
        return znodeService.listZnodeChildren(clusterPhyId, path, keyword);
    }

}
