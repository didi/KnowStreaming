package com.xiaojukeji.kafka.manager.service.service.impl;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.StatusCode;
import com.xiaojukeji.kafka.manager.common.entity.po.ClusterMetricsDO;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.po.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.po.ControllerDO;
import com.xiaojukeji.kafka.manager.dao.ClusterDao;
import com.xiaojukeji.kafka.manager.dao.ClusterMetricsDao;
import com.xiaojukeji.kafka.manager.dao.ControllerDao;
import com.xiaojukeji.kafka.manager.service.cache.ClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.schedule.ScheduleCollectDataManager;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * ClusterServiceImpl
 * @author zengqiao
 * @date 19/4/3
 */
@Service("clusterService")
public class ClusterServiceImpl implements ClusterService {
    private final static Logger logger = LoggerFactory.getLogger(ClusterServiceImpl.class);

    @Autowired
    private ClusterDao clusterDao;

    @Autowired
    private ClusterMetricsDao clusterMetricsDao;

    @Autowired
    private ClusterMetadataManager clusterMetadataManager;

    @Autowired
    private ScheduleCollectDataManager scheduleCollectDataManager;

    @Autowired
    private ControllerDao controllerDao;

    @Override
    public Result addNewCluster(ClusterDO clusterDO, String operator) {
        if (clusterDO == null) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal");
        }
        if (!checkZookeeper(clusterDO.getZookeeper())) {
            logger.error("addNewCluster@ClusterServiceImpl, zookeeper address invalid, cluster:{}", clusterDO);
            return new Result(StatusCode.PARAM_ERROR, "param illegal, zookeeper address illegal");
        }
        if (clusterDao.insert(clusterDO) <= 0) {
            return new Result(StatusCode.MY_SQL_INSERT_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
        }

        boolean status = clusterMetadataManager.addNew(clusterDO);
        if (!status) {
            return new Result(StatusCode.OPERATION_ERROR, "add zookeeper watch failed");
        }
        scheduleCollectDataManager.start(clusterDO);

        if (clusterDO.getAlarmFlag() == null || clusterDO.getAlarmFlag() <= 0) {
            return new Result();
        }
        return new Result();
    }

    @Override
    public ClusterDO getById(Long clusterId) {
        if (clusterId == null || clusterId < 0) {
            return null;
        }
        try {
            return clusterDao.getById(clusterId);
        } catch (Exception e) {
            logger.error("getById@ClusterServiceImpl, select failed, clusterId:{}.", clusterId, e);
        }
        return null;
    }

    @Override
    public Result updateCluster(ClusterDO clusterDO, boolean reload, String operator) {
        if (clusterDO == null) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal");
        }
        if (!checkZookeeper(clusterDO.getZookeeper())) {
            return new Result(StatusCode.PARAM_ERROR, "zookeeper address invalid");
        }
        try {
            clusterDao.updateById(clusterDO);
        } catch (Exception e) {
            logger.error("update cluster failed, newCluster:{} reload:{}.", clusterDO, reload, e);
            return new Result(StatusCode.MY_SQL_UPDATE_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
        }
        if (!reload) {
            return new Result();
        }
        boolean status = clusterMetadataManager.reload(clusterDO);
        if (!status) {
            return new Result(StatusCode.OPERATION_ERROR, "reload cache failed");
        }
        return new Result();
    }

    @Override
    public List<ClusterDO> listAll() {
        return clusterDao.listAll();
    }

    @Override
    public List<ClusterMetricsDO> getClusterMetricsByInterval(long clusterId, Date startTime, Date endTime) {
        return clusterMetricsDao.getClusterMetricsByInterval(clusterId, startTime, endTime);
    }

    @Override
    public List<ControllerDO> getKafkaControllerHistory(Long clusterId) {
        if (clusterId == null) {
            return new ArrayList<>();
        }
        return controllerDao.getByClusterId(clusterId);
    }

    private boolean checkZookeeper(String server) {
        ZooKeeper zookeeper = null;
        try {
            zookeeper = new ZooKeeper(server, 1000, null);
        } catch (Exception e) {
            logger.warn("checkZookeeper@ClusterServiceImpl, create ZOOKEEPER instance error.", e);
            return false;
        } finally {
            try {
                if (zookeeper != null) {
                    zookeeper.close();
                }
            } catch (Throwable t) {
            }
        }
        return true;
    }
}
