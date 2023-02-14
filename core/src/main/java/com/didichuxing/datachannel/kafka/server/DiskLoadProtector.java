/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.didichuxing.datachannel.kafka.server;


import com.didichuxing.datachannel.kafka.metrics.ExMeter;
import com.didichuxing.datachannel.kafka.metrics.KafkaExMetrics;
import com.yammer.metrics.core.Gauge;
import com.yammer.metrics.core.MetricName;
import org.apache.kafka.common.Configurable;
import org.apache.kafka.common.TopicPartition;
import org.apache.mina.util.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

//This class used to protect read file from disk. it avoid that disk read by random for too many read request.
// 1. check whether the read request can invoke physical disk read. if no allow the access otherwise, continue
// 2. read access caller has some roles like HIGHTPRIORY_REPLICA, NORMAL_REPLICA, CONSUMER, MAINTAIN_REPLIC
// 3. each roles has different wight for read
// 4. when the disk is busy that detected by system ioutil, it tell result for the caller by checkRead()
// 5. each read can be allowed that the caller should get the token. the token controlled the throughput
//    and concurrent by read for the caller.
// 6. the background task to check the disk ioutil and return the token for user
public class DiskLoadProtector implements Configurable {

    // fetcher's roles
    enum Role {
        // the fetcher is replica that's fetch disk data when the broker bring up or the topic expand replica
        maintainReplica,
        // the fetcher indicate by replica id, if id equals -1 it should be consumer, otherwise is replcia
        consumer,
        // the fetcher is replica that's not maintain replica
        normalReplica,
        // the fetcher is replica that's selected from normal replica by disk load protector,
        // and than give more bandwidth to handle it's read
        highPrioryReplica,

        MAX,
    }

    private static final Logger log = LoggerFactory.getLogger(DiskLoadProtector.class);

    // weight of the roles.
    private int maintainReplicaWeight = 1;
    private int consumerWeight = 2;
    private int normalReplicaWeight = 3;

    // the rules generate by weight of roles
    private Role[] rules= new Role[1024];

    // ioutil check frequency (ms)
    private int ioUtilFrequency = 10;
    // max io capacity mb
    private int maxIoCapacity= 100;

    // allow max read request for one disk in one ioutil frequency
    private int maxIoConcurrent = 3;

    //enable or disable disk load protctor
    private boolean enable = true;

    // use mincore to check whether the read data is in page cache
    private boolean accurateHotdataCheck = true;
    // if the fetch request lag more than unreplicaLagMin and the read data is in disk. the replica called unreplica
    private int unreplicaLagMin = 10000;

    // use to select highPrioryTopicPartition from unrplicas
    private ConcurrentHashMap<TopicPartitionAndReplica, UnreplicaTopicPartition> unReplicaTopicPartitions;
    // the front of priory queue by these condiftion:
    // 1. not maintain topicparitition and replica
    // 2. lag is less than other
    private PriorityQueue<UnreplicaTopicPartition> priorityQueue;
    private TopicPartitionAndReplica highPrioryTopicPartition;
    private ConcurrentHashSet<TopicPartitionAndReplica> maintainTopicPartitions;
    private int cleanUnreplicaFrequncy = 5000;

    //the token s used to limit throuput and concurrent for read on the disk
    private HashMap<String ,DiskToken> diskTokens;

    private Random random;
    private ScheduledExecutorService executorService;
    private HashMap<String, DiskLoadProtectMetrics>  metrics;

    public DiskLoadProtector() {
    }

    public DiskLoadProtector(ScheduledExecutorService executorService, boolean enable) {
        this.enable = enable;
        priorityQueue = new PriorityQueue<>();
        unReplicaTopicPartitions = new ConcurrentHashMap<>();
        maintainTopicPartitions = new ConcurrentHashSet<>();
        random = new Random();

        List<String> disks = OSUtil.instance().getAllDisks();
        diskTokens = new HashMap<>();
        for (String disk : disks) {
            diskTokens.put(disk, new DiskToken(disk));
        }

        buildRules(maintainReplicaWeight, consumerWeight, normalReplicaWeight);

        this.executorService = executorService;
        createMetrics();
        startSchedule();
    }

    private void startSchedule() {
        // these flag need to set that check it support by os
        accurateHotdataCheck = accurateHotdataCheck && OSUtil.instance().supportCheckFileInCache();
        enable = enable && OSUtil.instance().supportIoUtil();

        log.info("Disk load protector is {}, accurrate hot data check is {}, ioutil is {} supported",
                enable ? "enable" : "disable", accurateHotdataCheck? "enable" : "disable",
                OSUtil.instance().supportIoUtil() ? "" : "not");

        // start task for check ioutitl that monitor all disks. and the task return token by the disk's ioutil.
        OSUtil.instance().setIoUtilFrequency(ioUtilFrequency);
        executorService.schedule(()-> {
            try {
                checkDiskLoad();
            } catch (Exception e) {
                log.error("internal error: ", e);
            }
        }, ioUtilFrequency, TimeUnit.MILLISECONDS);

        // start task for check unreplica active status. if not active remove it
        executorService.scheduleWithFixedDelay(()-> {
            try {
                cleanUnreplica();
            } catch (Exception e) {
                log.error("internal error: ", e);
            }
        }, cleanUnreplicaFrequncy, cleanUnreplicaFrequncy, TimeUnit.MILLISECONDS);
    }

    void buildRules(int maintainWeight, int consumerWeight, int replicaWeight) {
        int total = maintainWeight + consumerWeight + replicaWeight;
        //init rules by weight of roles
        for (int i=0; i < rules.length; i++) {
            int v = random.nextInt(total);
            if ( v < maintainWeight) {
                rules[i] = Role.maintainReplica;
            } else if (v < maintainWeight + consumerWeight) {
                rules[i] = Role.consumer;
            } else {
                rules[i] = Role.normalReplica;
            }
        }
    }

    private void createMetrics() {
        String group = "kafka.server";
        String type = "DiskLoadProtector";

        // using scala metric help class to keep the naming of metric is the same as kafka metrics.
        var maintainTopics = new Gauge<String>() {
            @Override
            public String value() {
                var elements = maintainTopicPartitions.stream().map(TopicPartitionAndReplica::toString);
                return String.join(",", elements.collect(Collectors.toList()));
            }
        };
        var tag = new LinkedHashMap<String,String>();
        MetricName metricsName = KafkaExMetrics.createMetricsName(group, type, "MaintainTopics", tag);
        KafkaExMetrics.createGauge(metricsName,  maintainTopics).mark();

        var unreplicaTopics = new Gauge<String>() {
            @Override
            public String value() {
                var elements = unReplicaTopicPartitions.keySet().stream().map(TopicPartitionAndReplica::toString);
                return String.join(",", elements.collect(Collectors.toList()));
            }
        };
        metricsName = KafkaExMetrics.createMetricsName(group, type, "UnreplicaTopics", tag);
        KafkaExMetrics.createGauge(metricsName,  unreplicaTopics).mark();

        var highPriorityTopic = new Gauge<String>() {
            @Override
            public String value() {
                return highPrioryTopicPartition != null ?
                    highPrioryTopicPartition.toString(): "";
            }
        };
        metricsName = KafkaExMetrics.createMetricsName(group, type, "HighPriorityTopic", tag);
        KafkaExMetrics.createGauge(metricsName,  highPriorityTopic).mark();

        List<String> disks = OSUtil.instance().getAllDisks();
        metrics = new HashMap<>();
        for (String disk : disks) {
            tag.put("disk", disk);
            DiskLoadProtectMetrics diskLoadProtectMetrics = new DiskLoadProtectMetrics();
            metricsName = KafkaExMetrics.createMetricsName(group, type, "DiskProctectTotal", tag);
            diskLoadProtectMetrics.total = KafkaExMetrics.createMeter(metricsName, "disk protect total per second", TimeUnit.SECONDS);

            metricsName = KafkaExMetrics.createMetricsName(group, type, "DiskProctectActive", tag);
            diskLoadProtectMetrics.active = KafkaExMetrics.createMeter(metricsName, "disk protect perform per second", TimeUnit.SECONDS);
            metrics.put(disk, diskLoadProtectMetrics);
        }
    }

    // This function check all the disk current ioutil return token.
    private void checkDiskLoad() {
        List<String> disks = OSUtil.instance().getAllDisks();
        int rate = (int)Math.round(maxIoCapacity/(1000.0/ioUtilFrequency));
        rate = rate == 0 ? 1 : rate;
        // return token by diffrence ioutil.
        // if ioutil is hight return less token, otherwise returen more tokens
        for (String disk : disks) {
            double value = OSUtil.instance().getIoUtil(disk);
            if (value < 0.44) {
                diskTokens.get(disk).returnToken(4*rate);
            } else if (value < 0.66) {
                diskTokens.get(disk).returnToken(3*rate);
            }else if (value < 0.88) {
                diskTokens.get(disk).returnToken(2*rate);
            } else if (value < 0.98)  {
                diskTokens.get(disk).returnToken(rate);
            }
        }
        log.trace("current disk token: {}, io status: {}", diskTokens, OSUtil.instance().getAllDiskStatus());

        executorService.schedule(()-> {
            try {
                checkDiskLoad();
            } catch (Exception e) {
                log.error("internal error: ", e);
            }
        }, ioUtilFrequency, TimeUnit.MILLISECONDS);
    }

    // This function check all the unreplica topic partitions. it remove all the inactive replica from unreplicas
    synchronized private void cleanUnreplica() {
        var cleanUnreplicaTopicPartitions = new ArrayList<UnreplicaTopicPartition>();
        for (UnreplicaTopicPartition unreplicaTopicPartition : unReplicaTopicPartitions.values()) {
            if (System.currentTimeMillis() - unreplicaTopicPartition.timestamp > 60000) {
                cleanUnreplicaTopicPartitions.add(unreplicaTopicPartition);
            }
        }

        for (UnreplicaTopicPartition unreplicaTopicPartition : cleanUnreplicaTopicPartitions) {
            log.info("disk protector remove unused unreplica topic: {}, lag: {}",
                    unreplicaTopicPartition.topicPartitionAndReplica, unreplicaTopicPartition.lag);
            removeUnreplicaTopicPartition(unreplicaTopicPartition.topicPartitionAndReplica);
        }
    }

    public FetchInfo checkRead(FetchInfo fetchInfo) {
        // empty read
        if (fetchInfo.getPolicy() == FetchInfo.ReadPolicy.Empty) {
            return fetchInfo;
        }

        //disk protector disable or os is not support
        if (!enable) {
            fetchInfo.setPolicy(FetchInfo.ReadPolicy.Normal);
            return fetchInfo;
        }

        // check weather the read cause physical io, if the data in page cache return. otherwise continue
        boolean hotdata = isHotdata(fetchInfo);
        if (hotdata) {
            fetchInfo.setPolicy(FetchInfo.ReadPolicy.Normal);
            return fetchInfo;
        }
        // check read by policy
        return doDiskProtect(fetchInfo);
    }

    private FetchInfo doDiskProtect(FetchInfo fetchInfo) {
        assert !diskTokens.isEmpty();

        String diskname = fetchInfo.getDiskname();
        TopicPartitionAndReplica topicPartitionAndReplica = fetchInfo.getTopicPartitionAndReplica();
        int replicaId = topicPartitionAndReplica.replicaId();
        DiskToken token = diskTokens.get(diskname);

        // each read allowd by get the token by role. when the token is empty it return Evade to give up the reading
        // it the topic partition is hgih priory topic partiition return Greedy to read more data.
        if (replicaId < 0) {
            // the caller role is Consumer
            boolean success = token.takeToken(Role.consumer);
            DiskLoadProtectMetrics diskLoadProtectMetrics = metrics.get(diskname);
            diskLoadProtectMetrics.total.meter().mark();
            log.trace("disk protector perform action for consumer. topic: {}, disk: {}, token: {}, success: {}",
                    topicPartitionAndReplica.topicPartition(), diskname, token, success);
            if (!success) {
                fetchInfo.setPolicy(FetchInfo.ReadPolicy.Evade);
                diskLoadProtectMetrics.active.meter().mark();
                return fetchInfo;
            } else {
                OSUtil.instance().loadPageCache(
                        fetchInfo.getFilename(), fetchInfo.getLogOffset(), fetchInfo.getLength());
                fetchInfo.setPolicy(FetchInfo.ReadPolicy.Normal);
                return fetchInfo;
            }
        } else {
            if (topicPartitionAndReplica.equals(highPrioryTopicPartition)) {
                // the caller role is HightPriory replica
                log.trace("{} topic: {}, replica: {}, disk: {}, token: {}, success: true",
                        "disk protector perform action for high priory replica.",
                        topicPartitionAndReplica.topicPartition(), replicaId, diskname, token);
                token.takeToken(Role.highPrioryReplica);
                fetchInfo.setPolicy(FetchInfo.ReadPolicy.Greedy);
                fetchInfo.setLength(fetchInfo.getLength()*4);
                OSUtil.instance().loadPageCache(
                        fetchInfo.getFilename(), fetchInfo.getLogOffset(), fetchInfo.getLength());
                return fetchInfo;
            }

            // the caller role is noraml replica or maitain replica
            boolean maintainTopicPartition = maintainTopicPartitions.contains(topicPartitionAndReplica);
            boolean success = token.takeToken(maintainTopicPartition ? Role.maintainReplica : Role.normalReplica);
            log.trace("disk protector perform action for {}replica. topic: {}, replica: {}, disk: {}, token: {}, success: {}",
                    maintainTopicPartition ? "maintain ": "", topicPartitionAndReplica.topicPartition(),
                    replicaId, diskname, token, success);
            DiskLoadProtectMetrics diskLoadProtectMetrics = metrics.get(diskname);
            diskLoadProtectMetrics.total.meter().mark();
            if (!success) {
                fetchInfo.setPolicy(FetchInfo.ReadPolicy.Evade);
                diskLoadProtectMetrics.active.meter().mark();
                return fetchInfo;
            } else {
                OSUtil.instance().loadPageCache(
                        fetchInfo.getFilename(), fetchInfo.getLogOffset(), fetchInfo.getLength());
                fetchInfo.setPolicy(FetchInfo.ReadPolicy.Normal);
                return fetchInfo;
            }
        }
    }

    private boolean isHotdata(FetchInfo fetchInfo) {
        String diskName = fetchInfo.getDiskname();
        assert !diskName.isEmpty();

        //normally the hotdata should be active log segment. if accurateHotdataCheck is enable check it by the mincore util
        if (accurateHotdataCheck) {
            boolean hotdata = OSUtil.instance().isCached(
                    fetchInfo.getFilename(), fetchInfo.getLogOffset(), fetchInfo.getLength());
            fetchInfo.setHotdata(hotdata);
        }

        if (fetchInfo.getTopicPartitionAndReplica().replicaId() >= 0) {
            //caller is replica, need to check the replica status is unreplica or not
            UnreplicaTopicPartition unreplicaTopicPartition = unReplicaTopicPartitions.get(fetchInfo.getTopicPartitionAndReplica());
            if (unreplicaTopicPartition != null) {
                // the replica in unreplicas.
                // 1. update timestamp
                // 2. if lag small enough, reomve it from unreplicas
                unreplicaTopicPartition.timestamp = System.currentTimeMillis();
                if (fetchInfo.getLag() < unreplicaLagMin) {
                    log.info("disk protector remove unreplica topic: {}, lag: {}",
                            fetchInfo.getTopicPartitionAndReplica(), fetchInfo.getLag());
                    removeUnreplicaTopicPartition(fetchInfo.getTopicPartitionAndReplica());
                }
            } else {
                // the replica not in unpeplicas. check wether it's read from disk and the lag is big enough to add to unreplicas
                if (!fetchInfo.isHotdata() && fetchInfo.getLag() > unreplicaLagMin) {
                    log.info("disk protector add unreplica topic: {}, lag: {}",
                            fetchInfo.getTopicPartitionAndReplica(), fetchInfo.getLag());
                    addUnreplicaTopicPartition(fetchInfo.getTopicPartitionAndReplica(), fetchInfo.getLag());
                }
            }
        }
        return fetchInfo.isHotdata();
    }

    synchronized public void addMaintainTopicPartitions(TopicPartitionAndReplica topicPartitionAndReplica) {
        log.info("disk protector add maintain topic: {}", topicPartitionAndReplica);
        if (maintainTopicPartitions.contains(topicPartitionAndReplica)) {
            removeUnreplicaTopicPartition(topicPartitionAndReplica);
        }
        UnreplicaTopicPartition maintainTopicPartition = new UnreplicaTopicPartition(topicPartitionAndReplica, -1, true);
        maintainTopicPartitions.add(topicPartitionAndReplica);
        unReplicaTopicPartitions.put(topicPartitionAndReplica, maintainTopicPartition);
    }

    synchronized private void removeUnreplicaTopicPartition(TopicPartitionAndReplica topicPartitionAndReplica) {
        maintainTopicPartitions.remove(topicPartitionAndReplica);
        unReplicaTopicPartitions.remove(topicPartitionAndReplica);
        priorityQueue.removeIf(
            unreplicaTopicPartition -> unreplicaTopicPartition.topicPartitionAndReplica.equals(topicPartitionAndReplica)
        );
        if (topicPartitionAndReplica.equals(highPrioryTopicPartition)) {
            setHightPrioryTopicPartition();
        }
    }

    synchronized private void addUnreplicaTopicPartition(TopicPartitionAndReplica topicPartitionAndReplica, long lag) {
        if (unReplicaTopicPartitions.containsKey(topicPartitionAndReplica)) {
            removeUnreplicaTopicPartition(topicPartitionAndReplica);
        }

        UnreplicaTopicPartition unreplicaTopicPartition = new UnreplicaTopicPartition(topicPartitionAndReplica, lag, false);
        unReplicaTopicPartitions.put(topicPartitionAndReplica, unreplicaTopicPartition);
        priorityQueue.add(unreplicaTopicPartition);
        if (highPrioryTopicPartition == null) {
            setHightPrioryTopicPartition();
        }
    }

    private void setHightPrioryTopicPartition() {
        UnreplicaTopicPartition unreplicaTopicPartition = priorityQueue.peek();
        if (unreplicaTopicPartition != null) {
            highPrioryTopicPartition = unreplicaTopicPartition.topicPartitionAndReplica;
            log.info("disk protector set hight priory topic: {}", highPrioryTopicPartition);
        } else {
            highPrioryTopicPartition = null;
            log.info("disk protector set hight priory topic: None");
        }
    }

    public int getIoUtilFrequency() {
        return ioUtilFrequency;
    }

    @Override
    public void configure(Map<String, ?> configs) {
        if (configs.containsKey("enable")) {
            enable = Boolean.parseBoolean((String) configs.get("enable"));
        }
        if (configs.containsKey("accurate.hotdata.enable")) {
            accurateHotdataCheck = Boolean.parseBoolean((String) configs.get("accurate.hotdata.enable"));
        }
        if (configs.containsKey("unreplica.lag.min")) {
            unreplicaLagMin = Integer.parseInt((String) configs.get("unreplica.lag.min"));
        }
        if (configs.containsKey("ioutil.frequency")) {
            ioUtilFrequency = Integer.parseInt((String)configs.get("ioutil.frequency"));
        }
        OSUtil.instance().setIoUtilFrequency(ioUtilFrequency);

        if (configs.containsKey("io.weight.normalreplica")) {
            normalReplicaWeight = Integer.parseInt((String)configs.get("io.weight.normalreplica"));
        }
        if (configs.containsKey("io.weight.consumer")) {
            consumerWeight= Integer.parseInt((String)configs.get("io.weight.consumer"));
        }
        if (configs.containsKey("io.weight.maintainreplcia")) {
            maintainReplicaWeight = Integer.parseInt((String)configs.get("io.weight.maintainreplcia"));
        }
        if (configs.containsKey("io.max.concurrent")) {
            maxIoConcurrent = Integer.parseInt((String)configs.get("io.max.concurrent"));
        }
        if (configs.containsKey("io.max.capacity")) {
            maxIoCapacity = Integer.parseInt((String)configs.get("io.max.capacity"));
        }

        String configString = "Disk load protector config:\n";
        configString += String.format("\tenable=%b\n", enable);
        configString += String.format("\taccurate.hotdata.enable=%b\n", accurateHotdataCheck);
        configString += String.format("\tunreplica.lag.min=%d\n", unreplicaLagMin);
        configString += String.format("\tioutil.frequency=%d\n", ioUtilFrequency);
        configString += String.format("\tio.weight.normalreplica=%d\n", normalReplicaWeight);
        configString += String.format("\tio.weight.consumer=%d\n", consumerWeight);
        configString += String.format("\tio.weight.maintainreplcia=%d\n", maintainReplicaWeight);
        configString += String.format("\tio.max.concurrent=%d\n", maxIoConcurrent);
        configString += String.format("\tio.max.capacity=%d\n", maxIoCapacity);
        log.info(configString);

        accurateHotdataCheck = accurateHotdataCheck && OSUtil.instance().supportCheckFileInCache();
        enable = enable && OSUtil.instance().supportIoUtil();
        buildRules(maintainReplicaWeight, consumerWeight, normalReplicaWeight);
        log.info("Disk load protector is {}, accurrate hot data check is {}, ioutil is {} supported",
                enable ? "enable" : "disable", accurateHotdataCheck? "enable" : "disable",
                OSUtil.instance().supportIoUtil() ? "" : "not");
    }

    public Properties getConfig() {
        Properties properties = new Properties();
        properties.setProperty("enable", String.valueOf(enable));
        properties.setProperty("accurate.hotdata.enable", String.valueOf(enable));
        properties.setProperty("unreplica.lag.min", String.valueOf(unreplicaLagMin));
        properties.setProperty("ioutil.frequency", String.valueOf(ioUtilFrequency));
        properties.setProperty("io.weight.normalreplica", String.valueOf(normalReplicaWeight));
        properties.setProperty("io.weight.consumer", String.valueOf(consumerWeight));
        properties.setProperty("io.weight.maintainreplcia", String.valueOf(maintainReplicaWeight));
        properties.setProperty("io.max.concurrent", String.valueOf(maxIoConcurrent));
        properties.setProperty("io.max.capacity", String.valueOf(maxIoCapacity));
        return properties;
    }

    static class UnreplicaTopicPartition implements Comparable<UnreplicaTopicPartition>{
        private TopicPartitionAndReplica topicPartitionAndReplica;
        private boolean maintain;
        private long lag;
        private long timestamp;

        UnreplicaTopicPartition(TopicPartitionAndReplica topicPartitionAndReplica, long lag, boolean maintain) {
            this.topicPartitionAndReplica = topicPartitionAndReplica;
            this.maintain = maintain;
            this.lag = lag;
            this.timestamp = System.currentTimeMillis();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof UnreplicaTopicPartition) {
                UnreplicaTopicPartition utp = (UnreplicaTopicPartition)obj;
                return topicPartitionAndReplica.equals(utp.topicPartitionAndReplica);
            }
            return false;
        }

        @Override
        public int compareTo(UnreplicaTopicPartition o) {
            if (maintain) {
                if (maintain != o.maintain) {
                    return 1;
                }
            } else {
                if (o.maintain) {
                    return -1;
                }
            }
            return (int)(lag - o.lag);
        }
    }

    static class DiskLoadProtectMetrics {
        ExMeter total;
        ExMeter active;
    }

    class DiskToken {
        private String diskname;
        //token array that indicate left token by role
        private int[] tokens;

        // the currentIndex indicate of rules than use for determine which role should be filled.
        private int currentIndex;

        //the timestap for the last time of get token by role
        private long[] lastGetTokenTimestamp;

        DiskToken(String diskname) {
            this.diskname = diskname;
            // init token for roles
            tokens = new int[Role.MAX.ordinal()];
            lastGetTokenTimestamp = new long[Role.MAX.ordinal()];
            for (int i = 0; i< Role.MAX.ordinal(); i++) {
                tokens[i] = 0;
            }
        }

        boolean takeToken(Role role) {
            boolean success = false;
            synchronized (this) {
                if (tokens[role.ordinal()] > 0 &&
                        System.currentTimeMillis() - lastGetTokenTimestamp[role.ordinal()] > ioUtilFrequency/maxIoConcurrent) {
                    tokens[role.ordinal()] -= 1;
                    log.trace("disk protector take token for role: {}, disk: {}", role, diskname);
                    lastGetTokenTimestamp[role.ordinal()] = System.currentTimeMillis();
                    success = true;
                }
            }
            return success;
        }

        void returnToken(int value) {
            synchronized (this) {
                // always fill high priory token if it is consumed
                if (tokens[Role.highPrioryReplica.ordinal()] < 1) {
                    tokens[Role.highPrioryReplica.ordinal()] = 1;
                    log.trace("disk protector return token for role: highPrioryReplica, diskname: {}", diskname);
                    value--;
                }
                int totalToken = 0;
                for (int v : tokens) {
                    totalToken += v;
                }
                // fill token by rules if it is consumed
                while(value > 0 && totalToken < 3 * maxIoConcurrent + 1) {
                    Role t = rules[currentIndex++];
                    if (tokens[t.ordinal()] < maxIoConcurrent) {
                        log.trace("disk protector return token for role: {}, diskname: {}", t, diskname);
                        tokens[t.ordinal()]++;
                        value--;
                        totalToken++;
                    }
                    if (currentIndex == rules.length) {
                        currentIndex = 0;
                    }
                }
            }
        }

        public String toString() {
            return String.format("%d, %d, %d, %d", tokens[0], tokens[1], tokens[2], tokens[3]);
        }
    }
}


