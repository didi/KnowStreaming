package com.xiaojukeji.kafka.manager.service.zookeeper;

import com.xiaojukeji.kafka.manager.common.utils.jmx.JmxConfig;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.BrokerMetadata;
import com.xiaojukeji.kafka.manager.common.zookeeper.StateChangeListener;
import com.xiaojukeji.kafka.manager.common.zookeeper.ZkConfigImpl;
import com.xiaojukeji.kafka.manager.common.zookeeper.ZkPathUtil;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * ZK Broker节点变更
 * @author zengqiao
 * @date 20/5/14
 */
public class BrokerStateListener implements StateChangeListener {
    private final static Logger LOGGER = LoggerFactory.getLogger(BrokerStateListener.class);

    private Long clusterId;

    private ZkConfigImpl zkConfig;

    private JmxConfig jmxConfig;

    public BrokerStateListener(Long clusterId, ZkConfigImpl zkConfig, JmxConfig jmxConfig) {
        this.clusterId = clusterId;
        this.zkConfig = zkConfig;
        this.jmxConfig = jmxConfig;
    }

    @Override
    public void init() {
        try {
            List<String> brokerIdList = zkConfig.getChildren(ZkPathUtil.BROKER_IDS_ROOT);
            for (String brokerId: brokerIdList) {
                processBrokerAdded(Integer.valueOf(brokerId));
            }
        } catch (Exception e) {
            LOGGER.error("init brokers metadata failed, clusterId:{}.", clusterId, e);
        }
    }

    @Override
    public void onChange(State state, String path) {
        try {
            String brokerId = ZkPathUtil.parseLastPartFromZkPath(path);
            switch (state) {
                case CHILD_ADDED:
                case CHILD_UPDATED:
                    processBrokerAdded(Integer.valueOf(brokerId));
                    break;
                case CHILD_DELETED:
                    processBrokerDelete(Integer.valueOf(brokerId));
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("process broker state change failed, clusterId:{} state:{} path:{}.",
                    clusterId, state, path, e);
        }
    }

    private void processBrokerDelete(Integer brokerId) {
        LOGGER.warn("delete broker, clusterId:{} brokerId:{}.", clusterId, brokerId);
        PhysicalClusterMetadataManager.removeBrokerMetadata(clusterId, brokerId);
    }

    private void processBrokerAdded(Integer brokerId) {
        LOGGER.warn("add broker, clusterId:{} brokerId:{}.", clusterId, brokerId);
        BrokerMetadata brokerMetadata = null;
        try {
            brokerMetadata = zkConfig.get(ZkPathUtil.getBrokerIdNodePath(brokerId), BrokerMetadata.class);
            if (!brokerMetadata.getEndpoints().isEmpty()) {
                String endpoint = brokerMetadata.getEndpoints().get(0);
                int idx = endpoint.indexOf("://");
                endpoint = endpoint.substring(idx + "://".length());
                idx = endpoint.indexOf(":");

                brokerMetadata.setHost(endpoint.substring(0, idx));
                brokerMetadata.setPort(Integer.parseInt(endpoint.substring(idx + 1)));
            }
            brokerMetadata.setClusterId(clusterId);
            brokerMetadata.setBrokerId(brokerId);
            PhysicalClusterMetadataManager.putBrokerMetadata(clusterId, brokerId, brokerMetadata, jmxConfig);
        } catch (Exception e) {
            LOGGER.error("add broker failed, clusterId:{} brokerMetadata:{}.", clusterId, brokerMetadata, e);
        }
    }
}