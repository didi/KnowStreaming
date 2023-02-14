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

package com.didichuxing.datachannel.kafka.cache;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  This class use by watch commit timestamp changed.
 *  if it get data change event. it should notify Datacache commit cached data.
 */
class CommitTimeWatcher implements Watcher {

    private static final Logger log = LoggerFactory.getLogger(CommitTimeWatcher.class);

    final private ZkUtil zkUtil;
    final private DataCache dataCache;
    final String path;

    public CommitTimeWatcher(ZkUtil zkUtil, DataCache dateCache) throws Exception {
        this.zkUtil = zkUtil;
        this.path = zkUtil.getCommitTimestampPath(dateCache.getName());
        this.dataCache = dateCache;
        watch();
    }

    public void watch() throws Exception {
        zkUtil.getZooKeeper().exists(path, this);
    }

    @Override
    public void process(WatchedEvent event) {
        try {
            if (event.getType() == Event.EventType.NodeDataChanged) {
                long timestamp = Long.parseLong(new String(zkUtil.getZooKeeper().getData(path, this, null)));
                if (timestamp <= 0) {
                    log.error("zkpath: {} invalid value: {}.", path, timestamp);
                    return;
                }

                log.info("zkpath: {} value changed. value: {}.", path, timestamp);
                this.dataCache.commitCache(timestamp);
            } else if (event.getType() != Event.EventType.None && event.getType() != Event.EventType.ChildWatchRemoved) {
                zkUtil.getZooKeeper().exists(path, this);
            }
        } catch (Exception e) {
            log.error("internal zk error: ", e);
        }
    }

    public void unwatch() {
        try {
            zkUtil.getZooKeeper().removeWatches(path, this, WatcherType.Data, true);
        } catch (Exception e) {
            log.error("internal zk error: ", e);
        }
    }
}
