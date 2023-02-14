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

package com.didichuxing.datachannel.kafka.security.authorizer;

import com.didichuxing.datachannel.kafka.security.login.LoginManager;
import com.didichuxing.datachannel.kafka.security.login.User;
import com.didichuxing.datachannel.kafka.util.KafkaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Session class use to manager a user client access the server.
 * it use to cache topic access status.
 */
public class Session {

    final static private int SESSION_EXPIRD_TIMEMS = 15*60*1000;
    final static private int ACCESS_STATUS_STORE_SIZE = 2;

    private static final Logger log = LoggerFactory.getLogger("kafka.authorizer.logger");

    public static final Session AdminSession = new Session(LoginManager.admin_USER, "localhost");
    public static final Session UnkonwnSession = new Session(LoginManager.unknown_USER, "localhost");

    //session user
    final private User user;
    //session clinet hostaddress
    final private String hostAddress;
    //session active time
    volatile private long activeTimestamp;
    // client version   0.8/0.9 0, 0.10.0 1, 0.10.1/0.10.2 2
    private String clientVersion;
    //map topic name to AccessStatusAndTimestamp. the value value is array. index 0 is read. index 1 is write.
    private ConcurrentHashMap<String, AccessStatusAndTimestamp[]> accessTopics = new ConcurrentHashMap<>();

    public Session(User user, String hostAddress) {
        this.clientVersion = "unknown";
        this.user = user;
        this.hostAddress = hostAddress;
        this.activeTimestamp = System.currentTimeMillis();
    }

    public User getUser() {
        return user;
    }

    public ConcurrentHashMap<String, AccessStatusAndTimestamp[]> getAccessTopics() {
        return accessTopics;
    }

    public void setActiveTimestamp(long activeTimestamp) {
        this.activeTimestamp = activeTimestamp;
    }

    //Get access check status in session
    public AccessStatusAndTimestamp getAccessStatus(String topicname, Operation operation) {
        AccessStatusAndTimestamp[] accessTopic = accessTopics.get(topicname);
        if (accessTopic != null) {
            AccessStatusAndTimestamp accessStatusAndTimestamp = accessTopics.get(topicname)[operation.ordinal()];
            if (accessStatusAndTimestamp == null) {
                return null;
            }

            //update active time
            accessStatusAndTimestamp.setActiveTimestamp(System.currentTimeMillis());
            return accessStatusAndTimestamp;
        } else {
            return null;
        }
    }

    //Cache access check status in session
    public void setAccessStatus(String topicName, Operation operation,
                                AccessStatusAndTimestamp accessStatusAndTimestamp) {
        AccessStatusAndTimestamp[] status =  accessTopics.get(topicName);
        if (status == null) {
            //ACCESS_STATUS_STORE_SIZE is 2. AccessStatusAndTimestamp store access status. 0 use for read.
            // 1 use for write.
            status = new AccessStatusAndTimestamp[ACCESS_STATUS_STORE_SIZE];
            status[operation.ordinal()] = accessStatusAndTimestamp;
            accessTopics.putIfAbsent(topicName, status);
        } else {
            status[operation.ordinal()] = accessStatusAndTimestamp;
        }

    }

    public boolean checkExpierd() {
        //check activetime
        if (System.currentTimeMillis() - activeTimestamp > SESSION_EXPIRD_TIMEMS) {
            return true;
        }

        //check all the topics. if it's expire set access status is null
        Iterator<Map.Entry<String, AccessStatusAndTimestamp[]>> iterator = accessTopics.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<String, AccessStatusAndTimestamp[]> entry = iterator.next();
            AccessStatusAndTimestamp[] status = entry.getValue();
            boolean allNull = true;
            //topic read or write expired
            for (int i=0; i<ACCESS_STATUS_STORE_SIZE; i++) {
                if (status[i] != null) {
                    if (System.currentTimeMillis() - status[i].getActiveTimestamp() > SESSION_EXPIRD_TIMEMS) {
                        log.debug("Session manager delete session User = {}, Host = {}, Topic = {} Operation = {}",
                                user.getUsername(), hostAddress, entry.getKey(), i==0 ? "Read" : "Write");
                        status[i] = null;
                    } else {
                        allNull = false;
                    }
                }
            }

            //topic is expired
            if (allNull) {
                log.debug("Session manager delete session User = {}, Host = {}, Topic = {}", user.getUsername(),
                        hostAddress, entry.getKey());
                iterator.remove();
            }
        }
        return false;
    }

    public void setClientVersion(Short apiKey, Short apiVersion) {
        this.clientVersion = KafkaUtils.apiVersionToKafkaVersion(apiKey, apiVersion);
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public String getUsername() {
        return user.getUsername();
    }
}
