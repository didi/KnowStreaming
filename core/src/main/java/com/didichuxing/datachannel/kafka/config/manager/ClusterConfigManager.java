/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.didichuxing.datachannel.kafka.config.manager;

import com.didichuxing.datachannel.kafka.config.HAClusterConfig;
import kafka.coordinator.mirror.MirrorCoordinator;
import kafka.server.ConfigType;
import kafka.server.ReplicaManager;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.errors.InvalidConfigurationException;
import org.apache.kafka.common.network.ListenerName;
import org.apache.kafka.common.requests.RequestContext;
import org.apache.kafka.common.security.auth.SecurityProtocol;
import org.apache.kafka.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ClusterConfigManager {
    public static final String GATEWAY_CLUSTER_ID = "GW";

    private static final Logger log = LoggerFactory.getLogger(ClusterConfigManager.class);

    private static Map<String, Properties> didiHAClusterConfigs = new HashMap<>();

    private static ConfigDef DIDI_HA_CLUSTER_CONFIGS = HAClusterConfig.configDef();

    private ReplicaManager replicaManager;

    private MirrorCoordinator mirrorCoordinator;

    public ClusterConfigManager(ReplicaManager replicaManager, MirrorCoordinator mirrorCoordinator) {
        this.replicaManager = replicaManager;
        this.mirrorCoordinator = mirrorCoordinator;
    }

    public static void validateConfigs(Properties configs) {
        for (String key : configs.stringPropertyNames()) {
            if (!HAClusterConfig.configNames().contains(key))
                throw new InvalidConfigurationException(String.format("Unknown cluster config name: %s", key));
        }
        Map<String, Object> realConfigs = DIDI_HA_CLUSTER_CONFIGS.parse(configs);
        for (String key : configs.stringPropertyNames()) {
            if (DIDI_HA_CLUSTER_CONFIGS.configKeys().get(key).validator != null)
                DIDI_HA_CLUSTER_CONFIGS.configKeys().get(key).validator.ensureValid(key, realConfigs.get(key));
        }
    }

    public void configure(String cluster, Properties configs) {
        if (configs.isEmpty()) {
            if (didiHAClusterConfigs.containsKey(cluster)) {
                didiHAClusterConfigs.remove(cluster);
                log.info("clean configs for cluster:" + cluster);
            }
            return;
        }
        if (didiHAClusterConfigs.put(cluster, configs) != null) {
            this.replicaManager.reloadRemoteCluster(cluster);
            this.mirrorCoordinator.onConfigChanged(ConfigType.HACluster(), cluster);
        }
        log.info("set or update configs for cluster: {}, configs of this cluster: {}", cluster, getConfigs(cluster));
    }

    public static Object getConfig(String clusterId, String key) {
        if (!didiHAClusterConfigs.containsKey(clusterId)) return null;
        return didiHAClusterConfigs.get(clusterId).get(key);
    }

    public static Properties getConfigs(String clusterId) {
        if (!didiHAClusterConfigs.containsKey(clusterId)) return new Properties();
        return didiHAClusterConfigs.get(clusterId);
    }

    public static boolean existsHACluster(String clusterId) {
        return didiHAClusterConfigs.containsKey(clusterId);
    }

    public static HAClusterConfig getHAClusterConfig(String clusterId) {
        Properties props = getConfigs(clusterId);
        log.info("Get mirror config for cluster {}} configs {}", clusterId, props);
        return new HAClusterConfig(props);
    }

    public static Collection<Node> appendWithGatewayNodes(RequestContext context, Collection<Node> brokers) {
        if (context.securityProtocol == SecurityProtocol.PLAINTEXT) {
            return brokers;
        }
        if (!UserConfigManager.existsHAUser(context)) {
            return brokers;
        }
        List<String> urls = getHAClusterConfig(GATEWAY_CLUSTER_ID)
                .getList(HAClusterConfig.BOOTSTRAP_SERVERS_CONFIG);
        List<Node> nodes = new ArrayList<>(brokers);
        int id = -10;
        for (String url : urls) {
            nodes.add(new Node(id, Utils.getHost(url),  Utils.getPort(url)));
            id --;
        }
        return nodes;
    }

}
