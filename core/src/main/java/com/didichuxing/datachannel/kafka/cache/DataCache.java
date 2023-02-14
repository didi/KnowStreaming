/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.didichuxing.datachannel.kafka.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Datacache is a distribution data mainCache utility for distribute system. it can be deployment in every node.
 * The data soruce of Datacache can be sql server and other. Datacache retrieve the data by DataProvider.
 * When Datacache is loading. it retrieve all the data from data source. then it schedule a task to incremental
 * update from data source to keep the data newest.
 * The mechanism of data synchronous using zookeeper. it store sync timestamp and commit timestap in zookeeper.
 * When the update data recieved. it store data in uncommitCache that the data can not accessed by user and write
 * the uncommitTimestamp to the zookeeper. it will trigger commit timestamp update. if the commit timestamp updated.
 * it will commit upncommitCache to main mainCache that user can access the data.
 * DataCache mainCache data using HashMap. you can specify the Keytype and the Value type,
 * and access data like a normal hashmap.
 * @param <KeyType>
 * @param <ValueType>
 */
public class DataCache<KeyType, ValueType> {

    private static final Logger log = LoggerFactory.getLogger(DataCache.class);

    //datacache name.
    final private String name;
    //unique id for instance of Datacache.
    final private int nodeId;

    //retrieve data from datasource
    final private DataProvider dataProvider;

    //run sync data task
    final private ScheduledExecutorService schedule;

    //sync data frequnce
    final private int syncFrequnceMs;

    final private int checkFrequnceMs;

    //main mainCache
    final private AtomicReference<HashMap<KeyType, ValueType>> mainCache;

    //uncommit mainCache.
    final private Deque<DataRecord> uncommitCache;

    final private ZkUtil zkUtil;
    final private AtomicReference<ScheduledFuture<?>> future;

    // sync timestamp, when retrieve new data will update it.
    private long uncommitTimestamp;
    // commit timestamp. when commit will update it.
    private long commitTimestamp;

    private CommitTimeWatcher commitTimeWatcher;
    private ReloadWatcher reloadWatcher;
    private LeaderWatcher leaderdWatcher;

    //make commit,sync and load opration mutual exclusion
    private Lock lock = new ReentrantLock();

    private long lastCheckTimestamp;
    private long lastSyncTimestamp;
    private long lastLogTimestamp;


    public DataCache(String name, int nodeId, DataProvider dataProvider, ScheduledExecutorService schedule,
                     ZkUtil zkUtil, int syncFrequnceMs, int checkFrequnceMs) {
        this.name = name;
        this.nodeId = nodeId;
        this.dataProvider = dataProvider;
        this.schedule = schedule;
        this.syncFrequnceMs = syncFrequnceMs;
        mainCache = new AtomicReference<>(null);
        uncommitCache = new ArrayDeque<>();
        this.checkFrequnceMs = checkFrequnceMs;
        this.zkUtil = zkUtil;
        future = new AtomicReference<>();
        this.lastCheckTimestamp = System.currentTimeMillis();
        this.lastSyncTimestamp = System.currentTimeMillis();
        this.lastLogTimestamp = System.currentTimeMillis();

        lock.lock();
        if (name == null || name.isEmpty()) {
            throw new CacheException(String.format("DataCache:%s node %d invalid mainCache name", name, nodeId));
        }

        if (nodeId < 0) {
            throw new CacheException(String.format("DataCache:%s node %d invalid mainCache nodeId", name, nodeId));
        }
        try {
            //add zk watcher for watch commit timestamp change
            commitTimeWatcher = new CommitTimeWatcher(zkUtil, this);
            //add zk watcher for watch force reload
            reloadWatcher = new ReloadWatcher(zkUtil, this);
            //add zk watcher for check isleader
            leaderdWatcher = new LeaderWatcher(zkUtil, this);

            // init mainCache sync info in zookeeper
            zkUtil.initCache(nodeId, name);
        } catch (Exception e) {
            log.error("DataCache:{} node {} zookeeper init exception: ", name, nodeId, e);
            throw new CacheException(String.format("DataCache:%s node %d zookeeper init exception: %s", name,
                    nodeId, e.getMessage()));
        }

        this.commitTimestamp = loadCommitTimestamp();
        if (commitTimestamp == 0) {
            throw new CacheException(String.format("DataCache:%s node %d load timestamp failed", name, nodeId));
        }

        //load mainCache from data provider
        boolean loaded = load();
        if (!loaded) {
            throw new CacheException(String.format("DataCache:%s node %d load data failed", name, nodeId));
        }

        lock.unlock();
        //schedule auto incremental update and self check
        startAutoTask();

        log.info("DataCache:{} node {} cache initialized.", name, nodeId);
    }

    private long loadCommitTimestamp() {
        return zkUtil.getCommitTimestamp(name);
    }

    private boolean load() {
        log.info("DataCache:{} node {} load cache start.", name, nodeId);

        HashMap<KeyType, ValueType> hashmap = new HashMap<>();
        long timestamp;
        List<DataRecord> uncommitEntries = new ArrayList<>();
        try {
            //get all date from now
            Dataset dataset = dataProvider.fetchData(0, System.currentTimeMillis());
            if (dataset == null) {
                throw new CacheException(String.format("DataCache:%s node %d Fetch new data should not return null",
                        name , nodeId));
            }

            List<DataRecord> entries = dataset.getEntries();
            // add all the data from data provider into the mainCache
            // check weather the timestamp of record.
            // if the record's timestamp is less then the commit timestamp
            // add it into mainCache. otherwise add it into uncommit mainCache.
            for (DataRecord<KeyType, ValueType> entry : entries) {
                KeyType key = entry.getKey();
                ValueType value = entry.getValue();
                if (entry.getTimestamp() < commitTimestamp) {
                    if (entry.getOperation() == DataRecord.Operation.delete) {
                        hashmap.remove(key);
                    } else {
                        hashmap.put(key, value);
                    }
                } else {
                    uncommitEntries.add(entry);
                }
            }
            timestamp = dataset.getTimestamp();
        } catch (Exception e) {
            log.error("DataCache:{} node {} load cache exception: ", name, nodeId, e);
            return false;
        }

        //load data successfull, update data and status.
        uncommitTimestamp = uncommitEntries.isEmpty() ? commitTimestamp : timestamp;
        uncommitCache.clear();
        uncommitCache.addAll(uncommitEntries);
        mainCache.set(hashmap);

        //set uncommitTimestmap to zookeeper, this operation will trigger commit timestamp update.
        zkUtil.setUncommitTimestmap(nodeId, name, uncommitTimestamp);
        log.info("DataCache:{} node {} load cache finished. main cache entries: {}, " +
                        "uncommit cache entries: {}, uncommit time {}, commit time {}",
                name, nodeId, mainCache.get().size(), uncommitCache.size(), uncommitTimestamp, commitTimestamp);
        return true;
    }


    private void sync() {
        log.info("DataCache:{} node {} sync cache start.", name, nodeId);
        try {
            int entries = fetchNewData();
            log.info("DataCache:{} node {} sync cache finished, uncommit time {},  {} entries.",
                    name, nodeId, uncommitTimestamp, entries);
            if (leaderdWatcher.isLeader()) {
                if (commitTimestamp < uncommitTimestamp) {
                    log.info("DataCache:{} node {} leader update commit time.", name, nodeId);
                    zkUtil.updateCommitTimestamp(nodeId, name, uncommitTimestamp, commitTimestamp);
                }
            }
        } catch (Exception e) {
            log.error("DataCache:{} node {} sync cache exception: ", name, nodeId, e);
        }
    }

    private int fetchNewData() throws Exception{
        //get the newest data from uncommitTimestamp.
        Dataset dataset = dataProvider.fetchData(uncommitTimestamp, System.currentTimeMillis());
        if (dataset == null) {
            throw new CacheException("Fetch new data should not return null");
        }
        if (dataset.getTimestamp() < uncommitTimestamp) {
            throw new CacheException("Dataset timestamp should greater equal than uncommit time");
        }

        List<DataRecord> entries = dataset.getEntries();

        lastSyncTimestamp = dataset.getTimestamp();

        //it's no data to update
        if (entries.isEmpty()) {
            if (uncommitCache.isEmpty()) {
                uncommitTimestamp = uncommitCache.isEmpty() ? commitTimestamp : uncommitTimestamp;
            }
            return 0;
        }

        //all sync data should add into uncommit mainCache. it will be add into main mainCache
        // when the commit function called.
        lock.lock();
        try {
            uncommitCache.addAll(entries);
        } finally {
            lock.unlock();
        }

        //set uncommitTimestmap to zookeeper, this operation will trigger commit timestamp update.
        zkUtil.setUncommitTimestmap(nodeId, name, dataset.getTimestamp());
        uncommitTimestamp = dataset.getTimestamp();
        return entries.size();
    }

    private void check() {
        long currentTimestamp = System.currentTimeMillis();
        //check sync data is working
        long behind = currentTimestamp - lastSyncTimestamp;
        if (behind > syncFrequnceMs+1000) {
            log.error("DataCache:{} node {} sync is far behind now, behind {}s, last synctime {}",
                name, nodeId, behind / 1000, lastSyncTimestamp);
        }

        //check commit is working
        behind = uncommitTimestamp - commitTimestamp;
        if (behind > 2 * syncFrequnceMs+1000 && lastSyncTimestamp-uncommitTimestamp > 2 * syncFrequnceMs+1000) {
            log.warn("DataCache:{} node {} commit is far behind now, behind {}s, uncommit time {}, commit time {}",
                name, nodeId, behind / 1000, uncommitTimestamp, commitTimestamp);
        }

        //check zk
        if (!zkUtil.isAvailable()) {
            log.error("DataCache:{} node {} zookeeper connenct is not good.", name, nodeId);
            zkUtil.cleanAvailableStatus();
        }

        if (currentTimestamp - lastLogTimestamp > 15 * syncFrequnceMs) {
            log.info("DataCache:{} node {} main cache: [{}]", name, nodeId, mainCachetoString());
            lastLogTimestamp = currentTimestamp;
        }

        if (currentTimestamp - lastCheckTimestamp < checkFrequnceMs) {
            return;
        }

        try {
            // check the main mainCache is match with data provider by the commit timestamp.
            // if missmatch force reload this mainCache
            long currentCommitTimestamp = commitTimestamp;
            log.info("DataCache:{} node {} check cache fetch data. commit timestamp: {}", name, nodeId, currentCommitTimestamp);
            Dataset dataset = dataProvider.fetchData(0, currentCommitTimestamp);
            if (dataset == null) {
                throw new CacheException("Fetch new data should not return null");
            }
            List<DataRecord> entries = dataset.getEntries();
            HashMap<KeyType, ValueType> hashmap = new HashMap<>();
            for (DataRecord<KeyType, ValueType> entry : entries) {
                if (entry.getOperation() == DataRecord.Operation.delete) {
                    hashmap.remove(entry.getKey());
                } else {
                    hashmap.put(entry.getKey(), entry.getValue());
                }
            }

            lock.lock();
            try {
                if (currentCommitTimestamp == commitTimestamp) {
                    log.info("DataCache:{} node {} check cache data. commit timestamp: {}", name, nodeId, commitTimestamp);
                    for (Map.Entry<KeyType, ValueType> entry : hashmap.entrySet()) {
                        KeyType key = entry.getKey();
                        ValueType value = entry.getValue();
                        ValueType cahcedValue = mainCache.get().get(key);
                        if (cahcedValue == null || !cahcedValue.equals(value)) {
                            //mainCache is miss match for datasource ,shuold reload data
                            log.error("DataCache:{} node {} check cache faild. key: {},  map value: [{}], db value: [{}]",
                                    name, nodeId, key, cahcedValue == null ? "null" : cahcedValue.toString(),
                                    value == null ? "null" : value.toString());
                            load();
                            break;
                        }
                    }
                    lastCheckTimestamp = currentTimestamp;
                }else {
                    log.info("DataCache:{} node {} ignore check cache, check time: {}, committimestamp: {} ",
                            name, nodeId, currentCommitTimestamp, commitTimestamp);
                }
            }finally {
                lock.unlock();
            }
        } catch (Exception e) {
            log.error("DataCache:{} node {} check cache exception:{} ", name, nodeId, e);
        }

        log.info("DataCache:{} node {} check cache finished.", name, nodeId);
    }

    private void startAutoTask() {
        // 1. sync data from data provider
        // 2. check main mainCache
        if (future.get() != null && future.get().isCancelled()) {
            return;
        }

        log.trace("DataCache:{} node {} schedule auto task.", name, nodeId);
        future.set(schedule.schedule(() -> {
            try {
                sync();
                check();
            } catch (Exception e) {
                log.error("DataCache:{} node {} auto task run unknown exception: ", name, nodeId, e);
            } catch (Throwable throwable) {
                log.error("DataCache:{} node {} auto task run unknown exception: ", name, nodeId, throwable);
            } finally {
                startAutoTask();
            }
        }, syncFrequnceMs, TimeUnit.MILLISECONDS));
    }

    // This function triggered by zookeeper watcher.  when the commit timestamp changed.
    // it get all the date less than the commit timestamp from uncommit mainCache. and put
    // them into main mainCache.
    private void commit(long timestamp) {
        log.info("DataCache:{} node {} commit begin.", name, nodeId);
        if (commitTimestamp > timestamp) {
            log.warn("DataCache:{} node {} the time should greater than commit time", name, nodeId);
            return;
        }

        //no data to update
        if (uncommitCache.isEmpty()) {
            commitTimestamp = timestamp;
            log.info("DataCache:{} node {} commit cache finished, commitTimestamp:{}", name, nodeId, timestamp);
            return;
        }

        List<DataRecord> entries = new ArrayList<>();
        int numEntries = 0;
        Iterator<DataRecord> iterator = uncommitCache.iterator();
        while (iterator.hasNext()) {
            DataRecord<KeyType, ValueType>  entry = iterator.next();
            if (entry.getTimestamp() < timestamp) {
                entries.add(entry);
                iterator.remove();
                numEntries++;
            } else {
                break;
            }
        }

        //nothing to be commit
        if (numEntries == 0) {
            commitTimestamp = timestamp;
            log.info("DataCache:{} node {} commit cache finished, commitTimestamp:{}", name, nodeId, timestamp);
            return;
        }

        HashMap<KeyType, ValueType> hashmap = new HashMap<>(mainCache.get().size() + entries.size());
        hashmap.putAll(mainCache.get());
        for (DataRecord<KeyType, ValueType> entry : entries) {
            KeyType key = entry.getKey();
            ValueType value = entry.getValue();
            if (DataRecord.Operation.delete == entry.getOperation()) {
                hashmap.remove(key);
            } else {
                hashmap.put(key, value);
            }
        }
        mainCache.set(hashmap);
        commitTimestamp = timestamp;
        log.info("DataCache:{} node {} commit cache finished, commitTimestamp:{}, {} entries.",
                name, nodeId, timestamp, numEntries);
    }

    public int getNodeId() {
        return nodeId;
    }

    public String getName() {
        return name;
    }

    public int size() {
        return mainCache.get().size();
    }

    public long getCommitTimestamp() {
        return commitTimestamp;
    }

    // Get value from main mainCache by given key.
    public ValueType get(KeyType key) {
        return mainCache.get().get(key);
    }

    // This function triggered by zookeeper watcher when relaod status set.
    // it's the way for manually update the mainCache when the auto sync task has problems.
    public void reload() {
        log.info("DataCache:{} node {} reload data begin.", name, nodeId);
        //shutdown schedule task
        future.get().cancel(true);
        future.set(null);

        //load all date from data provider by the commit timestamp in zk
        long timestamp = loadCommitTimestamp();
        if (timestamp == 0) {
            log.error("DataCache:{} node {} load commit timestamp failed.", name, nodeId);
            return;
        }
        commitTimestamp = timestamp;

        log.info("DataCache:{} node {} old main cache: [{}]", nodeId, name, mainCachetoString());

        lock.lock();
        try {
            load();
        } finally {
            lock.unlock();
        }

        log.info("DataCache:{} node {} new main cache: [{}]", name, nodeId, mainCache.toString());

        //start the schedue task
        startAutoTask();
        log.info("DataCache:{} node {} reload data completed.", name, nodeId);
    }


    public void commitCache(long timestamp) {
        lock.lock();
        try {
            commit(timestamp);
        } finally {
            lock.unlock();
        }
    }


    //shutdown mainCache. clean resource
    public void stop() {
        commitTimeWatcher.removeWatcher();
        reloadWatcher.removeWatcher();
        leaderdWatcher.removeWatcher();
        future.get().cancel(true);
        uncommitCache.clear();
        mainCache.get().clear();
        log.info("DataCache:{} node {} cache shutdown.", name, nodeId);
    }

    private String mainCachetoString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<KeyType, ValueType> entry : mainCache.get().entrySet()) {
            sb.append(String.format("[key: [%s], value: [%s]] ", entry.getKey(), entry.getValue()));
        }
        return sb.toString();
    }
}
