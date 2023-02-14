/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
* file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.didichuxing.datachannel.kafka.security.authorizer;

import com.didichuxing.datachannel.kafka.report.SessionReport;
import com.didichuxing.datachannel.kafka.security.login.LoginManager;
import com.didichuxing.datachannel.kafka.security.login.User;
import kafka.network.RequestChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 *  This class use by mananger sessions.
 *  Each appId and ip address have one session
 *  There are two layer index for manger session. the first layer index by aapid,
 *  the second layer index by address
 *  it start a schedule task to check and remove expired sessions
 */
public class SessionManager {

    final static private int CHECK_SESSION_TIMEMS = 60 * 1000;
    final static private int PRINT_SESSION_TIMEMS = 15 * 60 * 1000;

    private static final Logger log = LoggerFactory.getLogger("kafka.authorizer.logger");

    // map username to submap that map hostname to seesion.
    final private ConcurrentHashMap<String, ConcurrentHashMap<String, Session>> sessions = new ConcurrentHashMap<>();

    private ScheduledFuture<?> scheduledFuture;

    private SessionManager() {
    }

    static public SessionManager getInstance() {
        return SessionMangerHolder.INSTANCE;
    }

    public void start(ScheduledExecutorService scheduledExecutorService) {
        log.info("Session manager startup");
        scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                cleanSession();
            } catch (Throwable throwable) {
                log.error("Session manager exception: ", throwable);
            }
        }, CHECK_SESSION_TIMEMS, CHECK_SESSION_TIMEMS, TimeUnit.MILLISECONDS);

        scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                printSession();
            } catch (Throwable throwable) {
                log.error("Session manager exception: ", throwable);
            }
        }, PRINT_SESSION_TIMEMS, PRINT_SESSION_TIMEMS, TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        log.info("Session manager shutdown");
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
        sessions.clear();
    }

    private void cleanSession() {
        log.trace("Session manager clean session");
        Iterator<Map.Entry<String, ConcurrentHashMap<String, Session>>> mapIterator = sessions.entrySet().iterator();
        while (mapIterator.hasNext()) {
            Map.Entry<String, ConcurrentHashMap<String, Session>> mapEntry = mapIterator.next();
            ConcurrentHashMap<String, Session> map = mapEntry.getValue();

            Iterator<Map.Entry<String, Session>> sessionIterator = map.entrySet().iterator();
            while (sessionIterator.hasNext()) {
                Map.Entry<String, Session> sessionEntry = sessionIterator.next();
                Session session = sessionEntry.getValue();
                if (session.checkExpierd()) {
                    sessionIterator.remove();
                    log.info("Session manager clean session User = {}, Host = {}",
                            session.getUsername(), sessionEntry.getKey());
                }
            }

            if (map.isEmpty()) {
                mapIterator.remove();
            }
        }
    }

    private void printSession() {
        StringBuilder sb = new StringBuilder();
        sb.append("Sessions: [");
        sessions.forEach((userName, sessions)->{
            sb.append(String.format("[userName: %s, hosts: [", userName));
            sessions.forEach((host, session)->{
                sb.append(String.format("[host: %s, topics: [", host));
                ConcurrentHashMap<String, AccessStatusAndTimestamp[]> accessTopics = session.getAccessTopics();
                accessTopics.forEach((topic, statusAndTimestamps)->{
                    String operation = "produce+fetch";
                    if (statusAndTimestamps[0] == null) {
                        operation = "produce";
                    } else if (statusAndTimestamps[1] == null){
                        operation = "consume";
                    }
                    sb.append(String.format("[topic: %s, operation: %s]", topic, operation));
                });
                if (sb.charAt(sb.length()-1) == ',') {
                    sb.deleteCharAt(sb.length()-1);
                }
                sb.append("],");
            });
            if (sb.charAt(sb.length()-1) == ',') {
                sb.deleteCharAt(sb.length()-1);
            }
            sb.append("],");
        });
        if (sb.charAt(sb.length()-1) == ',') {
            sb.deleteCharAt(sb.length()-1);
        }
        sb.append("]");
        log.info(sb.toString());
    }

    public Session getSession(RequestChannel.Session kafkaSession) {
        String userName = kafkaSession.principal().getName();
        String hostAddress = kafkaSession.clientAddress().getHostAddress();
        ConcurrentHashMap<String, Session> map = sessions.get(userName);
        if (map == null) {
            map = new ConcurrentHashMap<>();
            sessions.putIfAbsent(userName,map);
        }

        Session session = map.get(hostAddress);
        if (session == null) {
            User user = LoginManager.getInstance().getUser(userName);
            if (user == null) {
                //log.warn("Session manager can't fount the user User = {}, Host = {}, generate tmp session", userName, hostAddress);
                session = new Session(new User(userName, "", false), hostAddress);
                return session;
            }
            session = new Session(user, hostAddress);
            log.info("Session manager create session User = {}, Host = {}", userName, hostAddress);
            map.putIfAbsent(hostAddress, session);
        }

        session.setActiveTimestamp(System.currentTimeMillis());
        return session;
    }

    public ConcurrentHashMap<String, ConcurrentHashMap<String, Session>> getSessions() {
        return sessions;
    }

    private static class SessionMangerHolder {

        private static final SessionManager INSTANCE = new SessionManager();

    }
}
