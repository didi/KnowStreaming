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

package com.didichuxing.datachannel.kafka.security.authorizer;

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
    private int maxSessionsPerUser;

    private SessionManager() {
    }

    static public SessionManager getInstance() {
        return SessionMangerHolder.INSTANCE;
    }

    public void start(ScheduledExecutorService scheduledExecutorService) {
        this.start(scheduledExecutorService, 2000);
    }

    public void start(ScheduledExecutorService scheduledExecutorService, int maxSessionsPerUser) {
        log.info("Session manager startup");
        this.maxSessionsPerUser = maxSessionsPerUser;
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

            // clean session
            int out = map.size() - this.maxSessionsPerUser;
            if (out > 0) {
                log.info("The number of user {} sessions {} has exceeded the maximum value of {}, will be clean.", mapEntry.getKey(), map.size(), this.maxSessionsPerUser);
                Comparator<Session> comparator =
                        Comparator.<Session>comparingDouble(Session::getActiveTimestamp)
                                .thenComparingInt(Session::hashCode);
                TreeSet<Session> sortSet = new TreeSet<>(comparator);
                sortSet.addAll(map.values());
                for (Session oldleast : sortSet) {
                    map.remove(getSessionKey(oldleast));
                    log.info("Session manager clean out of max sessions for User = {}, Host = {}, Port = {}",
                            oldleast.getUsername(), oldleast.getHostAddress(), oldleast.getClientPort());
                    out --;
                    if (out <= 0) {
                        break;
                    }
                }
            }

            Iterator<Map.Entry<String, Session>> sessionIterator = map.entrySet().iterator();
            while (sessionIterator.hasNext()) {
                Map.Entry<String, Session> sessionEntry = sessionIterator.next();
                Session session = sessionEntry.getValue();
                if (session.checkExpierd()) {
                    sessionIterator.remove();
                    log.info("Session manager clean session User = {}, Host = {}, Port = {}",
                            session.getUsername(), session.getHostAddress(), session.getClientPort());
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
        sessions.forEach((userName, sessions) -> {
            sb.append(String.format("[userName: %s, hosts: [", userName));
            sessions.forEach((host, session) -> {
                sb.append(String.format("[host: %s, version: %s, clientid: %s, topics: [",
                        host, session.getClientVersion(), session.getClientId()));
                ConcurrentHashMap<String, AccessStatusAndTimestamp[]> accessTopics = session.getAccessTopics();
                accessTopics.forEach((topic, statusAndTimestamps) -> {
                    String operation = "produce+fetch";
                    if (statusAndTimestamps[0] == null) {
                        operation = "produce";
                    } else if (statusAndTimestamps[1] == null) {
                        operation = "consume";
                    }
                    sb.append(String.format("[topic: %s, operation: %s]", topic, operation));
                });
                if (sb.charAt(sb.length() - 1) == ',') {
                    sb.deleteCharAt(sb.length() - 1);
                }
                sb.append("],");
            });
            if (sb.charAt(sb.length() - 1) == ',') {
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.append("],");
        });
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]");
        log.info(sb.toString());
    }

    private String getSessionKey(Session session) {
        return session.getHostAddress() + ":" + session.getClientPort();
    }

    public Session getSession(RequestChannel.Session kafkaSession) {
        String userName = kafkaSession.principal().getName();
        String hostAddress = kafkaSession.clientAddress().getHostAddress();
        String sessionKey = hostAddress + ":" + kafkaSession.clientPort();
        ConcurrentHashMap<String, Session> map = sessions.get(userName);
        if (map == null) {
            map = new ConcurrentHashMap<>();
            sessions.putIfAbsent(userName, map);
        }

        Session session = map.get(sessionKey);
        if (session == null) {
//            User user = LoginManager.getInstance().getUser(userName);
//            if (user == null) {
//                //log.warn("Session manager can't fount the user User = {}, Host = {}, generate tmp session", userName, hostAddress);
//                session = new Session(new User(userName, "", false), hostAddress, kafkaSession.clientPort());
//                return session;
//            }
            User user = new User(userName, "", false);
            session = new Session(user, hostAddress, kafkaSession.clientPort());
            log.info("Session manager create session User = {}, Host = {}, Port = {}", userName, hostAddress, kafkaSession.clientPort());
            map.putIfAbsent(sessionKey, session);
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
