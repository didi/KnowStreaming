package com.xiaojukeji.kafka.manager.service.zookeeper;

import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.BrokerMetadata;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.ControllerData;
import com.xiaojukeji.kafka.manager.common.zookeeper.StateChangeListener;
import com.xiaojukeji.kafka.manager.common.zookeeper.ZkConfigImpl;
import com.xiaojukeji.kafka.manager.common.zookeeper.ZkPathUtil;
import com.xiaojukeji.kafka.manager.dao.ControllerDao;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ControllerDO;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;


/**
 * Controller 变更监听
 * @author zengqiao
 * @date 20/5/14
 */
public class ControllerStateListener implements StateChangeListener {
    private final static Logger LOGGER = LoggerFactory.getLogger(ControllerStateListener.class);

    private Long clusterId;

    private ZkConfigImpl zkConfig;

    private ControllerDao controllerDao;

    public ControllerStateListener(Long clusterId, ZkConfigImpl zkConfig, ControllerDao controllerDao) {
        this.clusterId = clusterId;
        this.zkConfig = zkConfig;
        this.controllerDao = controllerDao;
    }

    @Override
    public void init() {
        processControllerChange();
        return;
    }

    @Override
    public void onChange(State state, String path) {
        try {
            switch (state) {
                case NODE_DATA_CHANGED:
                    processControllerChange();
                default:
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("process controller state change failed, clusterId:{} state:{} path:{}.",
                    clusterId, state, path, e);
        }
    }

    private void processControllerChange(){
        LOGGER.warn("init controllerData or controller change, clusterId:{}.", clusterId);
        ControllerData controllerData = null;
        try {
            controllerData = zkConfig.get(ZkPathUtil.CONTROLLER_ROOT_NODE, ControllerData.class);
            if (controllerData == null) {
                PhysicalClusterMetadataManager.removeControllerData(clusterId);
            }
            PhysicalClusterMetadataManager.putControllerData(clusterId, controllerData);

            BrokerMetadata brokerMetadata =
                    PhysicalClusterMetadataManager.getBrokerMetadata(clusterId, controllerData.getBrokerid());
            ControllerDO controllerDO = ControllerDO.newInstance(
                    clusterId,
                    controllerData.getBrokerid(),
                    brokerMetadata != null? brokerMetadata.getHost(): "",
                    controllerData.getTimestamp(),
                    controllerData.getVersion()
            );
            controllerDao.insert(controllerDO);
        } catch (DuplicateKeyException e) {
            //ignore
        } catch (Exception e) {
            LOGGER.error("add controller failed, clusterId:{} controllerData:{}.", clusterId, controllerData, e);
        }
    }
}