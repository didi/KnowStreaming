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

import org.apache.zookeeper.ZooKeeper;

/**
 * This class use to and manager DataCache metadata in zookeeper.
 * it contains uncommit timestamp, commit timestamp and reload flag.
 * the zookeeper structure
 * /DataCache/                          all cache rootpath
 * /DataCache/{name}                    cache rootpath by one cache
 * /DataCache/{name}/commit             store the commit timestamp
 * /DataCache/{name}/sync               store all node which identify a client.
 * /DataCache/{name}/sync/{nodeId}      store one node uncommit timestamp
 * /DataCache/{name}/reload            store the node id which will be force reloaded.
 * name : cache name , nodeId : client nodeId
 */

public interface ZkClientCreator {
    public ZooKeeper zookeeper();
}
