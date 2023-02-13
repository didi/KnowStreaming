package com.xiaojukeji.kafka.manager.service.utils;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import kafka.admin.AdminUtils;
import kafka.admin.AdminUtils$;
import kafka.utils.ZkUtils;
import org.apache.kafka.common.security.JaasUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;


/**
 * @author fengqiongfeng
 * @date 21/4/11
 */
public class HaClusterCommands {
    private static final Logger LOGGER = LoggerFactory.getLogger(HaClusterCommands.class);

    private static final String HA_CLUSTERS = "ha-clusters";

    /**
     * 修改HA集群配置
     */
    public static ResultStatus modifyHaClusterConfig(String zookeeper, Long clusterPhyId, Properties modifiedProps) {
        ZkUtils zkUtils = null;
        try {
            zkUtils = ZkUtils.apply(
                    zookeeper,
                    Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                    Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                    JaasUtils.isZkSecurityEnabled()
            );

            // 获取当前配置
            Properties props = AdminUtils.fetchEntityConfig(zkUtils, HA_CLUSTERS, clusterPhyId.toString());

            // 补充变更的配置
            props.putAll(modifiedProps);

            AdminUtils$.MODULE$.kafka$admin$AdminUtils$$changeEntityConfig(zkUtils, HA_CLUSTERS, clusterPhyId.toString(), props);

        } catch (Exception e) {
            LOGGER.error("method=modifyHaClusterConfig||zookeeper={}||clusterPhyId={}||modifiedProps={}||errMsg=exception", zookeeper, clusterPhyId, modifiedProps, e);

            return ResultStatus.ZOOKEEPER_OPERATE_FAILED;
        } finally {
            if (null != zkUtils) {
                zkUtils.close();
            }
        }
        return ResultStatus.SUCCESS;
    }

    /**
     * 获取集群高可用配置
     */
    public static Properties fetchHaClusterConfig(String zookeeper, Long clusterPhyId) {
        ZkUtils zkUtils = null;
        try {
            zkUtils = ZkUtils.apply(
                    zookeeper,
                    Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                    Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                    JaasUtils.isZkSecurityEnabled()
            );

            // 获取配置
            return AdminUtils.fetchEntityConfig(zkUtils, HA_CLUSTERS, clusterPhyId.toString());
        }catch (Exception e){
            LOGGER.error("method=fetchHaClusterConfig||zookeeper={}||clusterPhyId={}||errMsg=exception", zookeeper, clusterPhyId, e);

            return null;
        } finally {
            if (null != zkUtils) {
                zkUtils.close();
            }
        }
    }

    /**
     * 删除 高可用集群的动态配置
     */
    public static ResultStatus coverHaClusterConfig(String zookeeper, Long clusterPhyId, Properties properties){
        ZkUtils zkUtils = null;
        try {
            zkUtils = ZkUtils.apply(
                    zookeeper,
                    Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                    Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                    JaasUtils.isZkSecurityEnabled()
            );

            AdminUtils$.MODULE$.kafka$admin$AdminUtils$$changeEntityConfig(zkUtils, HA_CLUSTERS, clusterPhyId.toString(), properties);

            return ResultStatus.SUCCESS;
        }catch (Exception e){
            LOGGER.error("method=deleteHaClusterConfig||zookeeper={}||clusterPhyId={}||delProps={}||errMsg=exception", zookeeper, clusterPhyId, properties, e);

            return ResultStatus.FAIL;
        } finally {
            if (null != zkUtils) {
                zkUtils.close();
            }
        }
    }

    private HaClusterCommands() {
    }
}
