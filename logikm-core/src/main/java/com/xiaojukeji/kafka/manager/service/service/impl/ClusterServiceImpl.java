package com.xiaojukeji.kafka.manager.service.service.impl;

import com.xiaojukeji.kafka.manager.common.bizenum.DBStatusEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.ModuleEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.OperateEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.ClusterDetailDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.cluster.ControllerPreferredCandidate;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.cluster.ClusterNameDTO;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.*;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.BrokerMetadata;
import com.xiaojukeji.kafka.manager.dao.ClusterDao;
import com.xiaojukeji.kafka.manager.dao.ClusterMetricsDao;
import com.xiaojukeji.kafka.manager.dao.ControllerDao;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.*;
import com.xiaojukeji.kafka.manager.service.utils.ConfigUtils;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * ClusterServiceImpl
 * @author zengqiao
 * @date 19/4/3
 */
@Service("clusterService")
public class ClusterServiceImpl implements ClusterService {
    private final static Logger LOGGER = LoggerFactory.getLogger(ClusterServiceImpl.class);

    @Autowired
    private ClusterDao clusterDao;

    @Autowired
    private ClusterMetricsDao clusterMetricsDao;

    @Autowired
    private ControllerDao controllerDao;

    @Autowired
    private ConsumerService consumerService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @Autowired
    private PhysicalClusterMetadataManager physicalClusterMetadataManager;

    @Autowired
    private ConfigUtils configUtils;

    @Autowired
    private ZookeeperService zookeeperService;

    @Autowired
    private OperateRecordService operateRecordService;

    @Override
    public ResultStatus addNew(ClusterDO clusterDO, String operator) {
        if (ValidateUtils.isNull(clusterDO) || ValidateUtils.isNull(operator)) {
            return ResultStatus.PARAM_ILLEGAL;
        }
        if (!isZookeeperLegal(clusterDO.getZookeeper())) {
            return ResultStatus.ZOOKEEPER_CONNECT_FAILED;
        }
        try {
            Map<String, String> content = new HashMap<>();
            content.put("zk address", clusterDO.getZookeeper());
            content.put("bootstrap servers", clusterDO.getBootstrapServers());
            content.put("security properties", clusterDO.getSecurityProperties());
            content.put("jmx properties", clusterDO.getJmxProperties());
            operateRecordService.insert(operator, ModuleEnum.CLUSTER, clusterDO.getClusterName(), OperateEnum.ADD, content);

            if (clusterDao.insert(clusterDO) <= 0) {
                LOGGER.error("add new cluster failed, clusterDO:{}.", clusterDO);
                return ResultStatus.MYSQL_ERROR;
            }
        } catch (DuplicateKeyException e) {
            LOGGER.error("add new cluster failed, cluster already existed, clusterDO:{}.", clusterDO, e);
            return ResultStatus.RESOURCE_ALREADY_EXISTED;
        } catch (Exception e) {
            LOGGER.error("add new cluster failed, operate mysql failed, clusterDO:{}.", clusterDO, e);
            return ResultStatus.MYSQL_ERROR;
        }
        return ResultStatus.SUCCESS;
    }

    @Override
    public ResultStatus updateById(ClusterDO clusterDO, String operator) {
        if (ValidateUtils.isNull(clusterDO) || ValidateUtils.isNull(operator)) {
            return ResultStatus.PARAM_ILLEGAL;
        }

        ClusterDO originClusterDO = this.getById(clusterDO.getId());
        if (ValidateUtils.isNull(originClusterDO)) {
            return ResultStatus.CLUSTER_NOT_EXIST;
        }

        if (!originClusterDO.getZookeeper().equals(clusterDO.getZookeeper())) {
            // 不允许修改zk地址
            return ResultStatus.CHANGE_ZOOKEEPER_FORBIDDEN;
        }
        Map<String, String> content = new HashMap<>();
        content.put("cluster id", clusterDO.getId().toString());
        content.put("security properties", clusterDO.getSecurityProperties());
        content.put("jmx properties", clusterDO.getJmxProperties());
        operateRecordService.insert(operator, ModuleEnum.CLUSTER, clusterDO.getClusterName(), OperateEnum.EDIT, content);

        clusterDO.setStatus(originClusterDO.getStatus());
        return updateById(clusterDO);
    }

    @Override
    public ResultStatus modifyStatus(Long clusterId, Integer status, String operator) {
        if (ValidateUtils.isNull(clusterId) || ValidateUtils.isNull(status)) {
            return ResultStatus.PARAM_ILLEGAL;
        }

        ClusterDO clusterDO = this.getById(clusterId);
        if (ValidateUtils.isNull(clusterDO)) {
            return ResultStatus.CLUSTER_NOT_EXIST;
        }
        clusterDO.setStatus(status);
        return updateById(clusterDO);
    }

    private ResultStatus updateById(ClusterDO clusterDO) {
        try {
            if (clusterDao.updateById(clusterDO) <= 0) {
                LOGGER.error("update cluster failed, clusterDO:{}.", clusterDO);
                return ResultStatus.MYSQL_ERROR;
            }
        } catch (Exception e) {
            LOGGER.error("update cluster failed, clusterDO:{}.", clusterDO, e);
            return ResultStatus.MYSQL_ERROR;
        }
        return ResultStatus.SUCCESS;
    }

    @Override
    public ClusterDO getById(Long clusterId) {
        if (ValidateUtils.isNull(clusterId)) {
            return null;
        }
        try {
            return clusterDao.getById(clusterId);
        } catch (Exception e) {
            LOGGER.error("get cluster failed, clusterId:{}.", clusterId, e);
        }
        return null;
    }

    @Override
    public List<ClusterDO> list() {
        try {
            return clusterDao.list();
        } catch (Exception e) {
            LOGGER.error("list cluster failed.", e);
        }
        return new ArrayList<>();
    }

    @Override
    public Map<Long, ClusterDO> listMap() {
        List<ClusterDO> doList = this.list();
        Map<Long, ClusterDO> doMap = new HashMap<>();
        for (ClusterDO elem: doList) {
            doMap.put(elem.getId(), elem);
        }
        return doMap;
    }

    @Override
    public List<ClusterDO> listAll() {
        try {
            return clusterDao.listAll();
        } catch (Exception e) {
            LOGGER.error("list cluster failed.", e);
        }
        return new ArrayList<>();
    }

    @Override
    public List<ClusterMetricsDO> getClusterMetricsFromDB(Long clusterId, Date startTime, Date endTime) {
        return clusterMetricsDao.getClusterMetrics(clusterId, startTime, endTime);
    }

    @Override
    public List<ControllerDO> getKafkaControllerHistory(Long clusterId) {
        if (ValidateUtils.isNull(clusterId)) {
            return new ArrayList<>();
        }
        return controllerDao.getByClusterId(clusterId);
    }

    private boolean isZookeeperLegal(String zookeeper) {
        boolean status = false;

        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(zookeeper, 1000, null);
            for (int i = 0; i < 15; ++i) {
                if (zk.getState().isConnected()) {
                    // 只有状态是connected的时候，才表示地址是合法的
                    status = true;
                    break;
                }
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            LOGGER.error("class=ClusterServiceImpl||method=isZookeeperLegal||zookeeper={}||msg=zk address illegal||errMsg={}", zookeeper, e.getMessage());
        } finally {
            try {
                if (zk != null) {
                    zk.close();
                }
            } catch (Exception e) {
                LOGGER.error("class=ClusterServiceImpl||method=isZookeeperLegal||zookeeper={}||msg=close zk  client failed||errMsg={}", zookeeper, e.getMessage());
            }
        }
        return status;
    }

    @Override
    public ClusterDetailDTO getClusterDetailDTO(Long clusterId, Boolean needDetail) {
        ClusterDO clusterDO = this.getById(clusterId);
        if (ValidateUtils.isNull(clusterDO)) {
            return null;
        }
        return getClusterDetailDTO(clusterDO, needDetail);
    }

    @Override
    public List<ClusterDetailDTO> getClusterDetailDTOList(Boolean needDetail) {
        List<ClusterDO> doList = this.listAll();

        Map<Long, Integer> regionNumMap =
                needDetail? regionService.getRegionNum(): new HashMap<>(0);
        Map<Long, Integer> consumerGroupNumMap =
                needDetail? consumerService.getConsumerGroupNumMap(doList): new HashMap<>(0);

        List<ClusterDetailDTO> dtoList = new ArrayList<>();
        for (ClusterDO clusterDO: doList) {
            ClusterDetailDTO dto = getClusterDetailDTO(clusterDO, needDetail);
            dto.setConsumerGroupNum(consumerGroupNumMap.get(clusterDO.getId()));
            dto.setRegionNum(regionNumMap.get(clusterDO.getId()));
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public ClusterNameDTO getClusterName(Long logicClusterId) {
        ClusterNameDTO clusterNameDTO = new ClusterNameDTO();
        LogicalClusterDO logicalClusterDO = logicalClusterMetadataManager.getLogicalCluster(logicClusterId);
        if (ValidateUtils.isNull(logicalClusterDO)) {
            return clusterNameDTO;
        }
        clusterNameDTO.setLogicalClusterId(logicalClusterDO.getId());
        clusterNameDTO.setLogicalClusterName(logicalClusterDO.getName());
        clusterNameDTO.setRegionIdList(ListUtils.string2LongList(logicalClusterDO.getRegionList()));

        ClusterDO clusterDO = this.getById(logicalClusterDO.getClusterId());
        clusterNameDTO.setPhysicalClusterId(clusterDO.getId());
        clusterNameDTO.setPhysicalClusterName(clusterDO.getClusterName());
        return clusterNameDTO;
    }

    @Override
    public ResultStatus deleteById(Long clusterId, String operator) {
        List<RegionDO> regionDOList = regionService.getByClusterId(clusterId);
        if (!ValidateUtils.isEmptyList(regionDOList)) {
            return ResultStatus.OPERATION_FORBIDDEN;
        }
        try {
            Map<String, String> content = new HashMap<>();
            content.put("cluster id", clusterId.toString());
            operateRecordService.insert(operator, ModuleEnum.CLUSTER, String.valueOf(clusterId), OperateEnum.DELETE, content);
            if (clusterDao.deleteById(clusterId) <= 0) {
                LOGGER.error("delete cluster failed, clusterId:{}.", clusterId);
                return ResultStatus.MYSQL_ERROR;
            }
        } catch (Exception e) {
            LOGGER.error("delete cluster failed, clusterId:{}.", clusterId, e);
            return ResultStatus.MYSQL_ERROR;
        }
        return ResultStatus.SUCCESS;
    }

    private ClusterDetailDTO getClusterDetailDTO(ClusterDO clusterDO, Boolean needDetail) {
        if (ValidateUtils.isNull(clusterDO)) {
            return new ClusterDetailDTO();
        }

        ClusterDetailDTO dto = new ClusterDetailDTO();
        dto.setClusterId(clusterDO.getId());
        dto.setClusterName(clusterDO.getClusterName());
        dto.setZookeeper(clusterDO.getZookeeper());
        dto.setBootstrapServers(clusterDO.getBootstrapServers());
        dto.setKafkaVersion(physicalClusterMetadataManager.getKafkaVersionFromCache(clusterDO.getId()));
        dto.setIdc(configUtils.getIdc());
        dto.setSecurityProperties(clusterDO.getSecurityProperties());
        dto.setJmxProperties(clusterDO.getJmxProperties());
        dto.setStatus(clusterDO.getStatus());
        dto.setGmtCreate(clusterDO.getGmtCreate());
        dto.setGmtModify(clusterDO.getGmtModify());
        if (ValidateUtils.isNull(needDetail) || !needDetail) {
            return dto;
        }
        dto.setBrokerNum(PhysicalClusterMetadataManager.getBrokerIdList(clusterDO.getId()).size());
        dto.setTopicNum(PhysicalClusterMetadataManager.getTopicNameList(clusterDO.getId()).size());
        dto.setControllerId(PhysicalClusterMetadataManager.getControllerId(clusterDO.getId()));
        return dto;
    }

    @Override
    public Result<List<ControllerPreferredCandidate>> getControllerPreferredCandidates(Long clusterId) {
        Result<List<Integer>> candidateResult = zookeeperService.getControllerPreferredCandidates(clusterId);
        if (candidateResult.failed()) {
            return new Result<>(candidateResult.getCode(), candidateResult.getMessage());
        }
        if (ValidateUtils.isEmptyList(candidateResult.getData())) {
            return Result.buildSuc(new ArrayList<>());
        }

        List<ControllerPreferredCandidate> controllerPreferredCandidateList = new ArrayList<>();
        for (Integer brokerId: candidateResult.getData()) {
            ControllerPreferredCandidate controllerPreferredCandidate = new ControllerPreferredCandidate();
            controllerPreferredCandidate.setBrokerId(brokerId);
            BrokerMetadata brokerMetadata = PhysicalClusterMetadataManager.getBrokerMetadata(clusterId, brokerId);
            if (ValidateUtils.isNull(brokerMetadata)) {
                controllerPreferredCandidate.setStatus(DBStatusEnum.DEAD.getStatus());
            } else {
                controllerPreferredCandidate.setHost(brokerMetadata.getHost());
                controllerPreferredCandidate.setStartTime(brokerMetadata.getTimestamp());
                controllerPreferredCandidate.setStatus(DBStatusEnum.ALIVE.getStatus());
            }
            controllerPreferredCandidateList.add(controllerPreferredCandidate);
        }
        return Result.buildSuc(controllerPreferredCandidateList);
    }

    @Override
    public Result addControllerPreferredCandidates(Long clusterId, List<Integer> brokerIdList) {
        if (ValidateUtils.isNull(clusterId) || ValidateUtils.isEmptyList(brokerIdList)) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }

        // 增加的BrokerId需要判断是否存活
        for (Integer brokerId: brokerIdList) {
            if (!PhysicalClusterMetadataManager.isBrokerAlive(clusterId, brokerId)) {
                return Result.buildFrom(ResultStatus.BROKER_NOT_EXIST);
            }

            Result result =  zookeeperService.addControllerPreferredCandidate(clusterId, brokerId);
            if (result.failed()) {
                return result;
            }
        }
        return Result.buildSuc();
    }

    @Override
    public Result deleteControllerPreferredCandidates(Long clusterId, List<Integer> brokerIdList) {
        if (ValidateUtils.isNull(clusterId) || ValidateUtils.isEmptyList(brokerIdList)) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }

        for (Integer brokerId: brokerIdList) {
            Result result = zookeeperService.deleteControllerPreferredCandidate(clusterId, brokerId);
            if (result.failed()) {
                return result;
            }
        }
        return Result.buildSuc();
    }
}
