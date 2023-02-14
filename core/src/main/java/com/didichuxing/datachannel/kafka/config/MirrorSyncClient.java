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

package com.didichuxing.datachannel.kafka.config;

import com.didichuxing.datachannel.kafka.config.manager.ClusterConfigManager;
import kafka.server.MirrorTopic;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.DescribeConfigsOptions;
import org.apache.kafka.common.acl.AccessControlEntryFilter;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.acl.AclBindingFilter;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.resource.PatternType;
import org.apache.kafka.common.resource.ResourcePattern;
import org.apache.kafka.common.resource.ResourcePatternFilter;
import org.apache.kafka.common.resource.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * User: didi
 * Date: 2022/1/12
 */
public class MirrorSyncClient implements Closeable {

    private HashMap<String, List<AclBinding>> cachedAcls = new HashMap<>();

    private HashMap<String, Properties> loadedClusterProperties = new HashMap<>();
    private HashMap<String, AdminClient> adminClientMap = new HashMap<>();

    private String targetClusterId;

    private static final Logger log = LoggerFactory.getLogger(MirrorSyncClient.class);
    private static final ResourcePatternFilter ANY_TOPIC = new ResourcePatternFilter(ResourceType.TOPIC,
            null, PatternType.ANY);
    private static final AclBindingFilter ANY_TOPIC_ACL = new AclBindingFilter(ANY_TOPIC, AccessControlEntryFilter.ANY);

    private static final String DiDiCustomTopicConfigPropertiesPrefix = "didi.ha";

    public MirrorSyncClient(Set<String> clusterSet, String targetClusterId) {
        this.targetClusterId = targetClusterId;
        HashSet<String> allSet = new HashSet<>();
        allSet.addAll(clusterSet);
        allSet.add(targetClusterId);
        initAllAdminClient(allSet);
    }

    /**
     * init all cluster AdminClient also include targetClusterId
     * @param clusterSet
     */
    protected void initAllAdminClient(Set<String> clusterSet) {
        clusterSet.forEach(clusterId -> {
            Properties properties = ClusterConfigManager.getConfigs(clusterId);
            if (!properties.isEmpty()) {
                createAdminClient(clusterId, properties);
            }
        });
    }

    /**
     * release all remote cluster AdminClient
     */
    protected void releaseAllRemoteAdminClient() {
        adminClientMap.entrySet().stream().filter(x -> !x.getKey().equals(targetClusterId)).forEach(y -> {
            releaseAdminClient(y.getKey(), y.getValue());
        });
    }

    /**
     * release  remote cluster adminClient by clusterId Set
     *
     * @param clusterSet
     */
    protected void releaseRemovedRemoteAdminClient(Set<String> clusterSet) {
        clusterSet.forEach(x -> {
           releaseAdminClient(x, adminClientMap.get(x));
        });
    }

    /**
     * prepare Remote AdminClient when start sync acls and sync configs
     *
     * @param clusterSet
     */
    public void prepareRemoteAdminClient(Set<String> clusterSet) {
        if (clusterSet.isEmpty()) {
            releaseAllRemoteAdminClient();
        } else {
            // check 不再需要维持的 clusterId 的AdminClient
            Set<String> removedSet = adminClientMap.keySet().stream()
                    .filter(x -> !x.equals(targetClusterId))
                    .filter(y -> !clusterSet.contains(y)).collect(Collectors.toSet());
            if (!removedSet.isEmpty()) {
                releaseRemovedRemoteAdminClient(removedSet);
            }
            reloadClusterConfig(clusterSet);
        }
    }

    /**
     *  reload one clusterId AdminClient
     *  case  clusterid in mirrorTopic but cluster config is removed need release AdminClinet
     *  case  clusterid is not exists in clientMap && clusterid config is exist need to create
     *  csae  clusterid is exists in client && clusterid config is updated we need release  and create
     *
     * @param clusterId
     * @return
     */
    private boolean reloadAdminClient(String clusterId) {
        AdminClient client = adminClientMap.get(clusterId);
        Properties newProperties = ClusterConfigManager.getConfigs(clusterId);
        if (client == null) {
            if (!newProperties.isEmpty()) {
                createAdminClient(clusterId, newProperties);
            }
        } else {
            if (newProperties.isEmpty()) {
                log.debug("cluster {} will released admin client", clusterId);
                releaseAdminClient(clusterId, client);
            } else {
                if (loadedClusterProperties.get(clusterId) != null && !newProperties.equals(loadedClusterProperties.get(clusterId))) {
                    log.info("cluster {} will reload admin client use new properties {}", clusterId, newProperties);
                    releaseAdminClient(clusterId, client);
                    createAdminClient(clusterId, newProperties);
                }
            }
        }
        return true;
    }

    /**
     * reload all AdminClient in clusterIds
     * it always used when prepare AdminClient or clusterid config is change notify to reload
     * @param clusterids
     */
    public synchronized void reloadClusterConfig(Set<String> clusterids) {
        // reload cluster by id
        clusterids.forEach(this::reloadAdminClient);
    }

    /**
     * validate adminclient if created return true else return false
     *
     * @param clusterId
     * @return
     */
    protected boolean validateClusterId(String clusterId) {
        return adminClientMap.containsKey(clusterId);
    }

    /**
     * sync acls
     *
     * sync topic acls from source to target
     *
     * delete unknows topic acls and add exists acls
     *
     * @param topics
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public synchronized void syncTopicAcls(List<MirrorTopic> topics) {
        try {
            Map<String, List<MirrorTopic>> grouped = topics.stream().collect(Collectors.groupingBy(MirrorTopic::remoteCluster));
            grouped.forEach((key, value) -> {
                try {
                    if (validateClusterId(key) && validateClusterId(targetClusterId)) {
                        syncClusterTopicAcls(key, value.stream().collect(Collectors.toMap(MirrorTopic::remoteTopic, x -> x)));
                    } else {
                        if (!validateClusterId(key)) {
                            log.warn("sync config from source cluster {} adminClient is not initialed. maybe cluster config is not set or invalidated", key);
                        }
                        if (!validateClusterId(targetClusterId)) {
                            log.warn("sync config to target cluster {} adminClient is not initialed. maybe cluster config is not set or invalidated", targetClusterId);
                        }
                    }
                } catch (Exception e) {
                    log.error("sync topic {} ", value, e);
                }
            });
        } catch (Exception e) {
            log.error("sync topic alcs {} ", topics, e);
        }
    }

    /**
     * sync topics config from source to target
     *
     * don't sync high version to low version maybe invalidated
     *
     * @param topics
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public synchronized void syncTopicConfigs(List<MirrorTopic> topics) {
        try {
            Map<String, List<MirrorTopic>> grouped = topics.stream().collect(Collectors.groupingBy(MirrorTopic::remoteCluster));
            grouped.forEach((key, value) -> {
                try {
                    if (validateClusterId(key) && validateClusterId(targetClusterId)) {
                        updateClusterTopicConfigsSimple(key, value.stream().collect(Collectors.toMap(MirrorTopic::remoteTopic, x -> x)));
                    } else {
                        if (!validateClusterId(key)) {
                            log.warn("sync config from source cluster {} adminClient is not initialed. maybe cluster config is not set or invalidated", key);
                        }
                        if (!validateClusterId(targetClusterId)) {
                            log.warn("sync config to target cluster {} adminClient is not initialed. maybe cluster config is not set or invalidated", targetClusterId);
                        }
                    }
                } catch (Exception e) {
                    log.error("sync topic config failure error {}", value, e);
                }
            });
        } catch (Exception e) {
            log.error("sync topic config {} ", topics, e);
        }
    }

    /**
     *
     * 同步集群 cluster 对应 topics 的acls
     *
     * @param clusterId
     * @param topics
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private void syncClusterTopicAcls(String clusterId, Map<String, MirrorTopic> topics) throws InterruptedException, ExecutionException {
        Collection<AclBinding> result = listTopicAclBindings(clusterId);
        List<AclBinding> bindings = result.stream()
                .filter(x -> x.pattern().resourceType() == ResourceType.TOPIC)
                .filter(x -> x.pattern().patternType() == PatternType.LITERAL)
                .filter(x -> topics.keySet().contains(x.pattern().name()))
                .map(x -> new AclBinding(new ResourcePattern(x.pattern().resourceType(), topics.get(x.pattern().name()).localTopic(), x.pattern().patternType()), x.entry()))
                .collect(Collectors.toList());

        log.debug("sync acls from cluster {} for topics {}", clusterId, topics);
        List<AclBinding> postedAcls = new ArrayList<>();
        if (cachedAcls.get(clusterId) != null) {
            postedAcls.addAll(this.cachedAcls.get(clusterId));
            postedAcls.removeAll(bindings);
            if (!postedAcls.isEmpty()) {
                deleteTopicAcls(postedAcls);
            }
        }
        if (!bindings.isEmpty()) {
            if (!bindings.equals(this.cachedAcls.get(clusterId))) {
                updateClusterTopicAcls(bindings);
            }
        }
        cachedAcls.put(clusterId, bindings);
    }


    /**
     * 更新集群ACL
     *
     * @param bindings
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private void updateClusterTopicAcls(List<AclBinding> bindings)
            throws InterruptedException, ExecutionException {
        AdminClient client = adminClientMap.get(targetClusterId);
        if (client != null) {
            client.createAcls(bindings).values().forEach((k, v) -> v.whenComplete((x, e) -> {
                if (e != null) {
                    log.warn("Could not sync ACL of topic {}.", k.pattern().name(), e);
                } else {
                    log.info("sync ACL of topic {} success", k.pattern().name());
                }
            }));
        }
    }

    /**
     * 获取 集群的 topic config
     *
     * @param clusterId
     * @param topics
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private Map<String, Config> describeTopicConfigs(String clusterId, Map<String, MirrorTopic> topics)
            throws InterruptedException, ExecutionException {
        AdminClient client = adminClientMap.get(clusterId);
        if (client != null) {
            Set<ConfigResource> resources = topics.values().stream().filter(topic -> !topic.remoteTopic().equals(""))
                    .map(x -> new ConfigResource(ConfigResource.Type.TOPIC, x.remoteTopic()))
                    .collect(Collectors.toSet());
            DescribeConfigsOptions describeOptions = new DescribeConfigsOptions().includeSynonyms(true);
            return client.describeConfigs(resources, describeOptions).all().get().entrySet().stream()
                    .collect(Collectors.toMap(x ->  topics.get(x.getKey().name()).localTopic(), x -> x.getValue()));
        } else {
            log.warn("can not list topic configs from cluster {}", clusterId);
            return new HashMap<String, Config>();
        }
    }

    /**
     * describe local cluster topic configs
     *
     * @param topics
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private Map<String, Config> describeLocalConfigs(Map<String, MirrorTopic> topics) throws InterruptedException, ExecutionException {
        AdminClient client = adminClientMap.get(targetClusterId);
        if (client != null) {
            Set<ConfigResource> resources = topics.values().stream()
                    .map(x -> new ConfigResource(ConfigResource.Type.TOPIC, x.localTopic()))
                    .collect(Collectors.toSet());
            DescribeConfigsOptions describeOptions = new DescribeConfigsOptions().includeSynonyms(true);
            return client.describeConfigs(resources, describeOptions).all().get().entrySet().stream()
                    .collect(Collectors.toMap(x -> x.getKey().name(), x -> x.getValue()));
        } else {
            return new HashMap<String, Config>();
        }
    }

    /**
     * 简单模式，直接merge source and target config properties map
     *
     * @param clusterId
     * @param topics
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private void updateClusterTopicConfigsSimple(String clusterId, Map<String, MirrorTopic> topics) throws InterruptedException, ExecutionException {
        log.debug("sync from cluster {} for topics {}", clusterId, topics);
        final Map<String, Config> sourceConfigs = describeTopicConfigs(clusterId, topics);
        final Map<String, Config> localConfigs = describeLocalConfigs(topics);
        final boolean[] needUpdate = {false};
        HashMap<String, ArrayList<ConfigEntry>> updateConfigMap = new HashMap<String, ArrayList<ConfigEntry>>();
        localConfigs.entrySet().stream().forEach(entry -> {
            Config config = entry.getValue();
            if (sourceConfigs.containsKey(entry.getKey())) {
                Config sourceConfig = sourceConfigs.get(entry.getKey());
                ArrayList<ConfigEntry> merged = new ArrayList<ConfigEntry>();
                HashMap<String, ConfigEntry> list = sourceConfig.entries().stream()
                        .filter(item -> !item.name().startsWith(DiDiCustomTopicConfigPropertiesPrefix))
                        .filter(property -> property.source() == ConfigEntry.ConfigSource.DYNAMIC_TOPIC_CONFIG)
                        .collect(Collectors.toMap(item -> item.name(), item -> item, (n1, n2) -> n1, HashMap::new));
                //add local not exist config entry
                list.entrySet().stream().forEach(x -> {
                    if (!config.entries().stream().filter(property -> property.source() == ConfigEntry.ConfigSource.DYNAMIC_TOPIC_CONFIG).map(y -> y.name()).collect(Collectors.toSet()).contains(x.getKey())) {
                        needUpdate[0] = true;
                        merged.add(x.getValue());
                    }
                });
                //update local exist config
                config.entries()
                        .stream()
                        .filter(property -> property.source() == ConfigEntry.ConfigSource.DYNAMIC_TOPIC_CONFIG)
                        .forEach(z -> {
                            if (list.keySet().contains(z.name())) {
                                merged.add(list.get(z.name()));
                                // update value from remote
                                if (!list.get(z.name()).value().equals(z.value())) {
                                    needUpdate[0] = true;
                                }
                            } else {
                                if (z.name().startsWith(DiDiCustomTopicConfigPropertiesPrefix)) {
                                    merged.add(z);
                                } else {
                                    // local exist but remote removed
                                    needUpdate[0] = true;
                                }
                            }
                        });
                if (needUpdate[0]) {
                    updateConfigMap.put(entry.getKey(), merged);
                    needUpdate[0] = false;
                }
            }
        });

        if (!updateConfigMap.isEmpty()) {
            log.info("remote config changed. update config cluster {} for topics {}", clusterId, topics);
            updateTopicConfigs(convertResourceMap(updateConfigMap));
        }
    }

    private Map<String, Config> convertResourceMap(HashMap<String, ArrayList<ConfigEntry>> topicConfigMap) {
        HashMap<String, Config> maps = new HashMap<String, Config>();
        topicConfigMap.entrySet().stream().forEach(item -> {
            maps.put(item.getKey(), new Config(item.getValue()));
        });
        return maps;
    }

    /**
     * list cluster all acls
     *
     * @param clusterId
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private Collection<AclBinding> listTopicAclBindings(String clusterId)
            throws InterruptedException, ExecutionException {
        AdminClient client = adminClientMap.get(clusterId);
        if (client != null) {
            return client.describeAcls(ANY_TOPIC_ACL).values().get();
        }
        return Collections.emptySet();
    }


    /**
     * 更新 config
     *
     * @param topicConfigs
     * @throws InterruptedException
     * @throws ExecutionException
     */
    // use deprecated alterConfigs API for broker compatibility back to 0.11.0
    private void updateTopicConfigs(Map<String, Config> topicConfigs)
            throws InterruptedException, ExecutionException {
        AdminClient client = adminClientMap.get(targetClusterId);
        if (client != null) {
            Map<ConfigResource, Config> configs = topicConfigs.entrySet().stream()
                    .collect(Collectors.toMap(x ->
                            new ConfigResource(ConfigResource.Type.TOPIC, x.getKey()), x -> x.getValue()));

            client.alterConfigs(configs).values().forEach((k, v) -> v.whenComplete((x, e) -> {
                if (e != null) {
                    log.warn("Could not alter configuration of topic {} ", k.name(), e);
                } else {
                    log.debug("sync topic {} config success", k.name());
                }
            }));
        }
    }

    /**
     * delete topic acls
     *
     * @param bindings
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private void deleteTopicAcls(List<AclBinding> bindings) throws InterruptedException, ExecutionException {
        AdminClient client = adminClientMap.get(targetClusterId);
        if (client != null) {
            Collection<AclBindingFilter> filters = bindings.stream().map(item -> item.toFilter()).collect(Collectors.toList());
            client.deleteAcls(filters).values().forEach((k, v) -> v.whenComplete((x, e) -> {
                if (e != null) {
                    log.warn("Could not delete ACL of topic {} ", k.patternFilter().name(), e);
                } else {
                    log.debug("delete topic {} acls successful",k.patternFilter().name());
                }
            }));
        }
    }

    /**
     * create cluster adminClient
     *
     * @param sourceClusterId
     * @param properties  cluster config
     * @return
     */
    private boolean createAdminClient(String sourceClusterId, Properties properties) {
        AdminClient client = null;
        try {
            client = AdminClient.create(properties);
            if (client != null) {
                loadedClusterProperties.put(sourceClusterId, properties);
                adminClientMap.put(sourceClusterId, client);
                log.debug("create cluster {} admin client properties use {}", sourceClusterId, properties);
                return true;
            }
        } catch (Exception ex) {
            log.error("can not init source adminclient for cluster {} with {} ", sourceClusterId, properties, ex);
        }
        return false;
    }

    /**
     * release cluster AdminClient
     *
     * @param client
     */
    private void releaseAdminClient(String clusterId, AdminClient client) {
        try {
            if (client != null) {
                client.close();
            }
        } catch (Exception e) {
            log.error("release client failure ", e);
        } finally {
            adminClientMap.remove(clusterId);
            loadedClusterProperties.remove(clusterId);
            cachedAcls.remove(clusterId);
        }
    }

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     *
     * <p> As noted in {@link AutoCloseable#close()}, cases where the
     * close may fail require careful attention. It is strongly advised
     * to relinquish the underlying resources and to internally
     * <em>mark</em> the {@code Closeable} as closed, prior to throwing
     * the {@code IOException}.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        // don't use adminClientMap.forEach
        adminClientMap.entrySet().stream().forEach(y -> {
            releaseAdminClient(y.getKey(), y.getValue());
        });
    }
}
