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

import com.didichuxing.datachannel.kafka.config.HAUserConfig;
import kafka.server.DynamicConfig;
import org.apache.kafka.common.requests.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Supplier;

public class UserConfigManager {
    private static final Logger log = LoggerFactory.getLogger(UserConfigManager.class);
    private static final Map<String, Properties> didiHAUserConfigs = new HashMap<>();
    private static final Set<String> clusterRelatedConfigs = new HashSet<>();

    static {
        clusterRelatedConfigs.add(HAUserConfig.DIDI_HA_ACTIVE_CLUSTER_CONFIG);
    }

    public void configure(String user, Properties configs) {
        Map<String, Object> realConfigs = DynamicConfig.getUserConfigs().parse(configs);
        Properties realKMConfigs = new Properties();
        for (String configName : HAUserConfig.configNames()) {
            if (Objects.nonNull(realConfigs.get(configName)))
                realKMConfigs.put(configName, realConfigs.get(configName));
        }
        if (realKMConfigs.isEmpty()) {
            if (didiHAUserConfigs.containsKey(user)) {
                didiHAUserConfigs.remove(user);
                log.info("Remove configs for user: {}", user);
            }
        } else {
            didiHAUserConfigs.put(user, realKMConfigs);
            log.info("Set configs for user {} : {}", user, realKMConfigs);
        }
    }

    //某User的配置中是否有对集群cluster的依赖
    public static Boolean ifConfigsClusterRelated(Properties configs, String cluster) {
        for (String clusterRelatedConfig : clusterRelatedConfigs) {
            if (configs.containsKey(clusterRelatedConfig) && Objects.equals(configs.getProperty(clusterRelatedConfig), cluster))
                return true;
        }
        return false;
    }

    public static boolean existsHAUser(String key) {
        return didiHAUserConfigs.containsKey(key)
                && didiHAUserConfigs.get(key).containsKey(HAUserConfig.DIDI_HA_ACTIVE_CLUSTER_CONFIG);
    }

    public static boolean existsHAUser(RequestContext context) {
        String identity1 = context.principal.getName() + "#" + context.clientId();
        String identity2 = context.principal.getName();
        return existsHAUser(identity1) || existsHAUser(identity2);
    }

    public static String activeCluster(RequestContext context, String defaultCluster) {
        return activeCluster(context, defaultCluster, Optional::empty);
    }

    public static String activeCluster(RequestContext context, String defaultCluster, Supplier<Optional<String>> activeClusterForTopicCallback) {
        String identity = context.principal.getName() + "#" + context.clientId();
        if (!existsHAUser(identity)) {
            Optional<String> topicCluster = activeClusterForTopicCallback.get();
            if (topicCluster.isPresent()
                    // 排除用户在切换中状态
                    && !activeClusterByIdentity(context.principal.getName(), defaultCluster).equals("None")) {
                return topicCluster.get();
            }
            identity = context.principal.getName();
        }
        return activeClusterByIdentity(identity, defaultCluster);
    }

    private static String activeClusterByIdentity(String identity, String defaultCluster) {
        return getConfigs(identity).getProperty(HAUserConfig.DIDI_HA_ACTIVE_CLUSTER_CONFIG, defaultCluster);
    }

    public static Properties getConfigs(String user) {
        if (!didiHAUserConfigs.containsKey(user)) return new Properties();
        return didiHAUserConfigs.get(user);
    }

    public static HAUserConfig getHAUserConfig(String user) {
        Properties props = getConfigs(user);
        return new HAUserConfig(props);
    }
}
