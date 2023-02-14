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

package com.didichuxing.datachannel.kafka.cache;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 *  This class use by watch reload status changed.
 *  if Datacache has some problem that cannot sync data normally.
 *  user can write the zookeeper path to force reload the Datacache
 *  when the watcher recieve the event. it
 */
class ReloadWatcher implements Watcher {

    private static final Logger log = LoggerFactory.getLogger(ReloadWatcher.class);

    final private ZkUtil zkUtil;
    final private DataCache dataCache;
    final private String path;

    private AtomicBoolean isReloading = new AtomicBoolean();

    public ReloadWatcher(ZkUtil zkUtil, DataCache dataCache) throws Exception{
        this.zkUtil = zkUtil;
        this.dataCache = dataCache;
        this.path = zkUtil.getReloadPath(dataCache.getName());
        zkUtil.getZooKeeper().exists(path, this);
    }


    @Override
    public void process(WatchedEvent event) {
        try {
            if (event.getType() == Event.EventType.NodeDataChanged || event.getType() == Event.EventType.NodeCreated) {

                String path = event.getPath();
                int nodeId = Integer.parseInt(new String(zkUtil.getZooKeeper().getData(path, this, null)));
                if (nodeId < 0) {
                    log.error("zkpath: {} invalid value: {}.", path, nodeId);
                    return;
                }
                if (!isReloading.compareAndSet(false, true)) {
                    return;
                }
                log.info("zkpath: {} value changed. value: {}.", path, nodeId);
                if (nodeId == dataCache.getNodeId()) {
                    dataCache.reload();
                }
            } else if (event.getType() != Event.EventType.None && event.getType() != Event.EventType.ChildWatchRemoved){
                zkUtil.getZooKeeper().exists(path, this);
            }
        } catch (Exception e) {
            log.error("internal zk error: ", e);
        } finally {
            isReloading.set(false);
        }
    }

    public void removeWatcher() {
        try {
            zkUtil.getZooKeeper().removeWatches(path, this, WatcherType.Data, true);
        } catch (Exception e) {
            log.error("internal zk error: ", e);
        }
    }
}
