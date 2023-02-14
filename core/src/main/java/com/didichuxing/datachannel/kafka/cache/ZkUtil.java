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

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


/**
 * This class use to and manager DataCache metadata in zookeeper.
 * it contains uncommit timestamp, commit timestamp and reload flag.
 * the zookeeper structure
 *  /DataCache/                          all cache rootpath
 *  /DataCache/{name}                    cache rootpath by one cache
 *  /DataCache/{name}/commit             store the commit timestamp
 *  /DataCache/{name}/sync               store all node which identify a client.
 *  /DataCache/{name}/sync/{nodeId}      store one node uncommit timestamp
 *  /DataCache/{name}/reload            store the node id which will be force reloaded.
 *  name : cache name , nodeId : client nodeId
 */
public class ZkUtil {
    private static final Logger log = LoggerFactory.getLogger(ZkUtil.class);

    static final String PATH_SEPRATOR = "/";
    static final String ROOT_PATH = "/DataCache";
    static final String COMMIT_PATH = "commit";
    static final String SYNC_PATH = "sync";
    static final String RELOAD_PATH = "reload";

    final private ZkClientCreator zkClientCreator;
    private ZooKeeper zooKeeper = null;
    private List<String> dataCacheWatches = new ArrayList<>();
    private String name = "";

    public ZkUtil(ZkClientCreator zkClientCreator) {
        //init zookeeper connect. all the cache share this connection
        this.zkClientCreator = zkClientCreator;
        refreshzookeeper();
    }

    private String getRootPath(String name) {
        return ROOT_PATH + PATH_SEPRATOR + name;
    }

    public String getCommitTimestampPath(String name) {
        return ROOT_PATH + PATH_SEPRATOR + name + PATH_SEPRATOR + COMMIT_PATH;
    }

    private String getUncommitTimestampPath(int id, String name) {
        return ROOT_PATH + PATH_SEPRATOR + name + PATH_SEPRATOR + SYNC_PATH + PATH_SEPRATOR + id;
    }

    public String getUncommitTimestampParentPath(String name) {
        return ROOT_PATH + PATH_SEPRATOR + name + PATH_SEPRATOR + SYNC_PATH;
    }

    public String getReloadPath(String name) {
        return ROOT_PATH + PATH_SEPRATOR + name + PATH_SEPRATOR + RELOAD_PATH;
    }

    private void setCommitTimestamp(String name, long timestamp) throws Exception {
        //set commit timestamp to zk path: /DataCache/{name}/commit
        String commitTimestampPath = getCommitTimestampPath(name);
        Stat node = zooKeeper.exists(commitTimestampPath, false);
        if (node == null) {
            zooKeeper.create(commitTimestampPath, String.valueOf(timestamp).getBytes(StandardCharsets.UTF_8),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } else {
            zooKeeper.setData(commitTimestampPath,
                    String.valueOf(timestamp).getBytes(StandardCharsets.UTF_8), node.getVersion());
        }
        log.info("DataCache {} set commit timestmap: {}", name, timestamp);
    }

    private long getTimestamp(String path) throws Exception {
        Stat node = zooKeeper.exists(path, false);
        if (node == null) {
            return 0;
        } else {
            return Long.parseLong(new String(zooKeeper.getData(path, false, node)));
        }
    }

    private long getUncommitTimestamp(int id, String name) throws Exception {
        //read uncommit timestamp to zk path: /DataCache/{name}/sync/{nodeId}
        String uncommitTimestampPath = getUncommitTimestampPath(id, name);
        long timestamp = getTimestamp(uncommitTimestampPath);
        log.debug("DataCache {} get uncommit timestmap: {}", name, timestamp);
        return timestamp;
    }

    public void updateCommitTimestamp(int id, String name, long uncommitTimestamp,
                                      long lastCommitTimestamp) {
        //lookup the minimal uncommit timestamp from all the node.
        // and set the commit timestamp to the minimal value.
        try {
            String path = getUncommitTimestampParentPath(name);
            List<String> list = zooKeeper.getChildren(path, false);
            if (list.isEmpty()) {
                log.error("DataCache {} update commit timestmap exception: no nodes found ", name);
                return;
            }

            long commitTimestamp = uncommitTimestamp;
            if (list.size() > 1) {
                for (String entry : list) {
                    int nodeId = Integer.parseInt(entry);
                    if (nodeId == id) {
                        continue;
                    }
                    long timestamp = getUncommitTimestamp(nodeId, name);
                    if (timestamp < commitTimestamp) {
                        commitTimestamp = timestamp;
                    }
                }
            }

            if (commitTimestamp > lastCommitTimestamp) {
                log.info("DataCache {} update commit timestmap: {}", name, commitTimestamp);
                setCommitTimestamp(name, commitTimestamp);
            }
        } catch (Exception e) {
            log.error("DataCache {} update commit timestmap exception: {} ", name, e);
        }
    }

    public void initCache(int id, String name) throws Exception {
        try {
            log.info("DataCache {} node {} init cache.", name, id);
            dataCacheWatches.add("/DataCache/" + name + "/sync");
            dataCacheWatches.add("/DataCache/" + name + "/commit");
            dataCacheWatches.add("/DataCache/" + name + "/reload");
            this.name = name;
            //init root
            if (zooKeeper.exists(ROOT_PATH, false) == null) {
                try {
                    zooKeeper.create(ROOT_PATH, "".getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                            CreateMode.PERSISTENT);

                } catch (KeeperException.NodeExistsException exception) {
                    Thread.sleep(1000);
                }
            }
            // init commit time
            long timestatmp = System.currentTimeMillis();
            String cacheRootPath = getRootPath(name);
            if (zooKeeper.exists(cacheRootPath, false) == null) {
                try {
                    zooKeeper.create(cacheRootPath, "".getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                            CreateMode.PERSISTENT);
                } catch (KeeperException.NodeExistsException exception) {
                    Thread.sleep(1000);
                }
            }

            String uncommitTimestampParentPath = getUncommitTimestampParentPath(name);
            if (zooKeeper.exists(uncommitTimestampParentPath, false) == null) {
                try {
                    zooKeeper.create(uncommitTimestampParentPath,
                            "".getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                } catch (KeeperException.NodeExistsException exception) {
                    Thread.sleep(1000);
                }
            }

            //not have connection. reset commit time
            List<String> list = zooKeeper.getChildren(uncommitTimestampParentPath, false);
            if (list.isEmpty()) {
                setCommitTimestamp(name, timestatmp);
            } else {
                if (getCommitTimestamp(name) == 0) {
                    setCommitTimestamp(name, timestatmp);
                }
            }

            String uncommitTimestampPath = getUncommitTimestampPath(id, name);
            if (zooKeeper.exists(uncommitTimestampPath, false) != null) {
                throw new CacheException(String.format("DataCache %s node %d node Id is dupelicate name", name, id));
            } else {
                //avoid commit time chagned in Datacache loading
                setUncommentTimestamp(id, name, timestatmp);
            }
            log.info("DataCache {} node {} init cache finished.", name, id);
        } catch (CacheException e) {
            throw e;
        } catch (Exception e) {
            log.error("DataCache {} node {} init cache exception {}: ", name, id, e);
            throw new CacheException(String.format("DataCache {} init cache exception: %s", name, e.getMessage()));
        }
    }

    public void setUncommentTimestamp(int id, String name, long uncommitTimestamp) {
        //write uncommit timestamp to zk path: /DataCache/{name}/sync/{nodeId}
        try {
            long oldUncommitTimestamp = getUncommitTimestamp(id, name);
            if (oldUncommitTimestamp < uncommitTimestamp) {
                String uncommitTimestampPath = getUncommitTimestampPath(id, name);

                Stat node = zooKeeper.exists(uncommitTimestampPath, false);
                if (node == null) {
                    zooKeeper.create(uncommitTimestampPath, String.valueOf(uncommitTimestamp).getBytes(StandardCharsets.UTF_8),
                            ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                } else {
                    zooKeeper.setData(uncommitTimestampPath,
                            String.valueOf(uncommitTimestamp).getBytes(StandardCharsets.UTF_8), node.getVersion());
                }
                log.info("DataCache {} node {} set uncommit timestamp: {}", name, id, uncommitTimestamp);
            }

        } catch (Exception e) {
            log.error("DataCache {} node {} set uncommit time exception: {}", name, id, e);
        }
    }

    public long getCommitTimestamp(String name) {
        //get commit timestamp to zk path: /DataCache/{name}/commit
        try {
            String commitTimestampPath = getCommitTimestampPath(name);
            long timestamp = getTimestamp(commitTimestampPath);
            log.debug("DataCache {} get commit timestamp: {}", name, timestamp);
            return timestamp;
        } catch (Exception e) {
            log.error("DataCache {} get commit timestamp exception: {}", name, e);
        }
        return 0;
    }

    public boolean isAvailable() {
        return zooKeeper.getState() == ZooKeeper.States.CONNECTED;
    }

    public boolean isExistWatches() {
        try {
            List<String> allWatches = new ArrayList<>();
            Method dataWatchesMethod = zooKeeper.getClass().getDeclaredMethod("getDataWatches");
            dataWatchesMethod.setAccessible(true);
            allWatches.addAll((List) dataWatchesMethod.invoke(zooKeeper));
            Method existWatchesMethod = zooKeeper.getClass().getDeclaredMethod("getExistWatches");
            existWatchesMethod.setAccessible(true);
            allWatches.addAll((List) existWatchesMethod.invoke(zooKeeper));
            if (allWatches.containsAll(dataCacheWatches)) {
                log.info("DataCache:{} zk contains all watches", this.name);
                return true;
            }
            log.info("DataCache:{} zk not contains all watches", this.name);
        } catch (Exception e) {
            log.error("DataCache:{} isExistWatches exception: ", this.name, e);
        }
        return false;
    }

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void refreshzookeeper() {
        if (zooKeeper == null || !isAvailable()) {
            zooKeeper = zkClientCreator.zookeeper();
            assert zooKeeper != null;
        }
    }
}
