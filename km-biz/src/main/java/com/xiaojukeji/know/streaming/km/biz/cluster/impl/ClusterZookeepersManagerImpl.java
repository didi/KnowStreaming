package com.xiaojukeji.know.streaming.km.biz.cluster.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.biz.cluster.ClusterZookeepersManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.cluster.ClusterZookeepersOverviewDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.ZKConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ZookeeperMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.metric.ZookeeperMetricParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.entity.zookeeper.Znode;
import com.xiaojukeji.know.streaming.km.common.bean.entity.zookeeper.ZookeeperInfo;
import com.xiaojukeji.know.streaming.km.common.bean.vo.zookeeper.ClusterZookeepersOverviewVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.zookeeper.ClusterZookeepersStateVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.zookeeper.ZnodeVO;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.enums.zookeeper.ZKRoleEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.PaginationUtil;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import com.xiaojukeji.know.streaming.km.core.service.version.metrics.ZookeeperMetricVersionItems;
import com.xiaojukeji.know.streaming.km.core.service.zookeeper.ZnodeService;
import com.xiaojukeji.know.streaming.km.core.service.zookeeper.ZookeeperMetricService;
import com.xiaojukeji.know.streaming.km.core.service.zookeeper.ZookeeperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ClusterZookeepersManagerImpl implements ClusterZookeepersManager {
    private static final ILog LOGGER = LogFactory.getLog(ClusterZookeepersManagerImpl.class);

    @Autowired
    private ClusterPhyService clusterPhyService;

    @Autowired
    private ZookeeperService zookeeperService;

    @Autowired
    private ZookeeperMetricService zookeeperMetricService;

    @Autowired
    private ZnodeService znodeService;

    @Override
    public Result<ClusterZookeepersStateVO> getClusterPhyZookeepersState(Long clusterPhyId) {
        ClusterPhy clusterPhy = clusterPhyService.getClusterByCluster(clusterPhyId);
        if (clusterPhy == null) {
            return Result.buildFromRSAndMsg(ResultStatus.CLUSTER_NOT_EXIST, MsgConstant.getClusterPhyNotExist(clusterPhyId));
        }

//        // TODO
//        private Integer healthState;
//        private Integer healthCheckPassed;
//        private Integer healthCheckTotal;

        List<ZookeeperInfo> infoList = zookeeperService.listFromDBByCluster(clusterPhyId);

        ClusterZookeepersStateVO vo = new ClusterZookeepersStateVO();
        vo.setTotalServerCount(infoList.size());
        vo.setAliveFollowerCount(0);
        vo.setTotalFollowerCount(0);
        vo.setAliveObserverCount(0);
        vo.setTotalObserverCount(0);
        vo.setAliveServerCount(0);
        for (ZookeeperInfo info: infoList) {
            if (info.getRole().equals(ZKRoleEnum.LEADER.getRole())) {
                vo.setLeaderNode(info.getHost());
            }

            if (info.getRole().equals(ZKRoleEnum.FOLLOWER.getRole())) {
                vo.setTotalFollowerCount(vo.getTotalFollowerCount() + 1);
                vo.setAliveFollowerCount(info.alive()? vo.getAliveFollowerCount() + 1: vo.getAliveFollowerCount());
            }

            if (info.getRole().equals(ZKRoleEnum.OBSERVER.getRole())) {
                vo.setTotalObserverCount(vo.getTotalObserverCount() + 1);
                vo.setAliveObserverCount(info.alive()? vo.getAliveObserverCount() + 1: vo.getAliveObserverCount());
            }

            if (info.alive()) {
                vo.setAliveServerCount(vo.getAliveServerCount() + 1);
            }
        }

        Result<ZookeeperMetrics> metricsResult = zookeeperMetricService.collectMetricsFromZookeeper(new ZookeeperMetricParam(
                clusterPhyId,
                infoList.stream().filter(elem -> elem.alive()).map(item -> new Tuple<String, Integer>(item.getHost(), item.getPort())).collect(Collectors.toList()),
                ConvertUtil.str2ObjByJson(clusterPhy.getZkProperties(), ZKConfig.class),
                ZookeeperMetricVersionItems.ZOOKEEPER_METRIC_WATCH_COUNT
        ));
        if (metricsResult.failed()) {
            LOGGER.error(
                    "class=ClusterZookeepersManagerImpl||method=getClusterPhyZookeepersState||clusterPhyId={}||errMsg={}",
                    clusterPhyId, metricsResult.getMessage()
            );
            return Result.buildSuc(vo);
        }
        Float watchCount = metricsResult.getData().getMetric(ZookeeperMetricVersionItems.ZOOKEEPER_METRIC_WATCH_COUNT);
        vo.setWatchCount(watchCount != null? watchCount.intValue(): null);

        return Result.buildSuc(vo);
    }

    @Override
    public PaginationResult<ClusterZookeepersOverviewVO> getClusterPhyZookeepersOverview(Long clusterPhyId, ClusterZookeepersOverviewDTO dto) {
        //获取集群zookeeper列表
        List<ClusterZookeepersOverviewVO> clusterZookeepersOverviewVOList = ConvertUtil.list2List(zookeeperService.listFromDBByCluster(clusterPhyId), ClusterZookeepersOverviewVO.class);

        //搜索
        clusterZookeepersOverviewVOList = PaginationUtil.pageByFuzzyFilter(clusterZookeepersOverviewVOList, dto.getSearchKeywords(), Arrays.asList("host"));

        //分页
        PaginationResult<ClusterZookeepersOverviewVO> paginationResult = PaginationUtil.pageBySubData(clusterZookeepersOverviewVOList, dto);

        return paginationResult;
    }

    @Override
    public Result<ZnodeVO> getZnodeVO(Long clusterPhyId, String path) {
        Result<Znode> result = znodeService.getZnode(clusterPhyId, path);
        if (result.failed()) {
            return Result.buildFromIgnoreData(result);
        }
        return Result.buildSuc(ConvertUtil.obj2ObjByJSON(result.getData(), ZnodeVO.class));
    }

    /**************************************************** private method ****************************************************/

}
