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

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 *  This class use by watch nodes changed.
 *  it select the first node as Datacache leader
 */
class LeaderWatcher implements Watcher {

    private static final Logger log = LoggerFactory.getLogger(LeaderWatcher.class);

    final private ZkUtil zkUtil;
    final private String nodeId;
    final private String path;

    private boolean isLeader;
    private boolean stop;

    public LeaderWatcher(ZkUtil zkUtil, DataCache dataCache) throws Exception {
        this.zkUtil = zkUtil;
        this.nodeId = String.valueOf(dataCache.getNodeId());

        this.path = zkUtil.getUncommitTimestampParentPath(dataCache.getName());
        Stat exists = zkUtil.getZooKeeper().exists(path, this);
        if (exists != null) {
            zkUtil.getZooKeeper().getChildren(path, this);
        }
    }

    public boolean isLeader() {
        return isLeader;
    }

    @Override
    public void process(WatchedEvent event) {
        try {
            if (event.getType() == Event.EventType.NodeChildrenChanged) {
                List<String> currentChilds = zkUtil.getZooKeeper().getChildren(path, this);

                log.info("zkpath: {} child changed. value: {}.", path, currentChilds);
                if (currentChilds.isEmpty()) {
                    log.error("zkpath: {}  unkown exception", path);
                    return;
                }
                String id = currentChilds.get(0);
                if (nodeId.equals(id) && !isLeader) {
                    isLeader = true;
                    log.info("zkpath: {} node {} become leader.", path, nodeId);
                }

                if (!nodeId.equals(id) && isLeader) {
                    isLeader = false;
                    log.info("zkpath: {} node {} become not leader.", path, nodeId);
                }
            } else if (event.getType() != Event.EventType.None && event.getType() != Event.EventType.ChildWatchRemoved){
                zkUtil.getZooKeeper().getChildren(path, this);
            }
        } catch(KeeperException.SessionExpiredException | KeeperException.ConnectionLossException e){
            log.info("ZooKeeper is lost connection", e);
        } catch(Exception e){
            log.error("internal zk error: ", e);
        }
    }


    public void removeWatcher() {
        try {
            zkUtil.getZooKeeper().removeWatches(path, this, WatcherType.Children, true);
        } catch (Exception e) {
            log.error("internal zk error: ", e);
        }
    }
}
