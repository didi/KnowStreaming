package com.xiaojukeji.kafka.manager.service.biz.ha.impl;

import com.xiaojukeji.kafka.manager.common.bizenum.ClusterModeEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.DBStatusEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.ha.HaResTypeEnum;
import com.xiaojukeji.kafka.manager.common.constant.MsgConstant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.ClusterDetailDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.LogicalClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.RegionDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.HaASRelationDO;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.service.biz.ha.HaClusterManager;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.LogicalClusterService;
import com.xiaojukeji.kafka.manager.service.service.RegionService;
import com.xiaojukeji.kafka.manager.service.service.ZookeeperService;
import com.xiaojukeji.kafka.manager.service.service.ha.HaASRelationService;
import com.xiaojukeji.kafka.manager.service.service.ha.HaClusterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

@Component
public class HaClusterManagerImpl implements HaClusterManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(HaClusterManagerImpl.class);
    
    @Autowired
    private ClusterService clusterService;

    @Autowired
    private HaClusterService haClusterService;

    @Autowired
    private ZookeeperService zookeeperService;

    @Autowired
    private LogicalClusterService logicalClusterService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private HaASRelationService haASRelationService;

    @Override
    public List<ClusterDetailDTO> getClusterDetailDTOList(Boolean needDetail) {
        return clusterService.getClusterDetailDTOList(needDetail);
    }

    @Override
    @Transactional
    public Result<Void> addNew(ClusterDO clusterDO, Long activeClusterId, String operator) {
        if (activeClusterId == null) {
            // 普通集群，直接写入DB
            Long clusterPhyId = zookeeperService.getClusterIdAndNullIfFailed(clusterDO.getZookeeper());
            if (clusterPhyId != null && clusterService.getById(clusterPhyId) == null) {
                // 该集群ID不存在时，则进行设置，如果已经存在了，则忽略
                clusterDO.setId(clusterPhyId);
            }

            return Result.buildFrom(clusterService.addNew(clusterDO, operator));
        }

        //高可用集群
        ClusterDO activeClusterDO = clusterService.getById(activeClusterId);
        if (activeClusterDO == null) {
            // 主集群不存在
            return Result.buildFromRSAndMsg(ResultStatus.RESOURCE_NOT_EXIST, MsgConstant.getClusterPhyNotExist(activeClusterId));
        }

        HaASRelationDO oldRelationDO = haClusterService.getHA(activeClusterId);
        if (oldRelationDO != null){
            return Result.buildFromRSAndMsg(ResultStatus.RESOURCE_ALREADY_USED,
                    MsgConstant.getActiveClusterDuplicate(activeClusterDO.getId(), activeClusterDO.getClusterName()));
        }

        Long standbyClusterPhyId = zookeeperService.getClusterIdAndNullIfFailed(clusterDO.getZookeeper());
        if (standbyClusterPhyId != null && clusterService.getById(standbyClusterPhyId) == null) {
            // 该集群ID不存在时，则进行设置，如果已经存在了，则忽略
            clusterDO.setId(standbyClusterPhyId);
        }

        ResultStatus rs = clusterService.addNew(clusterDO, operator);
        if (!ResultStatus.SUCCESS.equals(rs)) {
            return Result.buildFrom(rs);
        }

        Result<List<Integer>> rli = zookeeperService.getBrokerIds(clusterDO.getZookeeper());
        if (!rli.hasData()){
            return Result.buildFrom(ResultStatus.BROKER_NOT_EXIST);
        }

        // 备集群创建region
        RegionDO regionDO = new RegionDO(DBStatusEnum.ALIVE.getStatus(), clusterDO.getClusterName(), clusterDO.getId(), ListUtils.intList2String(rli.getData()));
        rs = regionService.createRegion(regionDO);
        if (!ResultStatus.SUCCESS.equals(rs)){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            return Result.buildFrom(rs);
        }

        // 备集群创建逻辑集群
        List<LogicalClusterDO> logicalClusterDOS = logicalClusterService.getByPhysicalClusterId(activeClusterId);
        if (!logicalClusterDOS.isEmpty()) {
            // 有逻辑集群，则对应创建逻辑集群
            Integer mode = logicalClusterDOS.get(0).getMode();
            LogicalClusterDO logicalClusterDO = new LogicalClusterDO(
                    clusterDO.getClusterName(),
                    clusterDO.getClusterName(),
                    ClusterModeEnum.INDEPENDENT_MODE.getCode().equals(mode)?mode:ClusterModeEnum.SHARED_MODE.getCode(),
                    ClusterModeEnum.INDEPENDENT_MODE.getCode().equals(mode)?logicalClusterDOS.get(0).getAppId(): "",
                    clusterDO.getId(),
                    regionDO.getId().toString()
            );
            ResultStatus clcRS = logicalClusterService.createLogicalCluster(logicalClusterDO);
            if (clcRS.getCode() != ResultStatus.SUCCESS.getCode()){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return Result.buildFrom(clcRS);
            }
        }

        return haClusterService.createHA(activeClusterId, clusterDO.getId(), operator);
    }

    @Override
    @Transactional
    public Result<Void> deleteById(Long clusterId, String operator) {
        HaASRelationDO haRelationDO = haClusterService.getHA(clusterId);
        if (haRelationDO == null){
            return clusterService.deleteById(clusterId, operator);
        }

        Result rv = checkForDelete(haRelationDO, clusterId);
        if (rv.failed()){
            return rv;
        }

        //解除高可用关系
        Result result = haClusterService.deleteHA(haRelationDO.getActiveClusterPhyId(), haRelationDO.getStandbyClusterPhyId());
        if (result.failed()){
            return result;
        }

        //删除集群
        result = clusterService.deleteById(clusterId, operator);
        if (result.failed()){
            return result;
        }
        return Result.buildSuc();
    }

    private Result<Void> checkForDelete(HaASRelationDO haRelationDO, Long clusterId){
        List<HaASRelationDO> relationDOS = haASRelationService.listAllHAFromDB(haRelationDO.getActiveClusterPhyId(),
                haRelationDO.getStandbyClusterPhyId(),
                HaResTypeEnum.TOPIC);
        if (relationDOS.stream().filter(relationDO -> !relationDO.getActiveResName().startsWith("__")).count() > 0){
            return Result.buildFromRSAndMsg(ResultStatus.OPERATION_FORBIDDEN, "集群还存在高可topic");
        }
        return Result.buildSuc();
    }
}
