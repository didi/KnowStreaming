package com.didichuxing.datachannel.kafka.report;

import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.kafka.security.authorizer.DidiAuthorizer;
import com.didichuxing.datachannel.kafka.security.authorizer.SessionManager;
import com.didichuxing.datachannel.kafka.security.login.LoginManager;
import kafka.network.RequestChannel;
import kafka.security.auth.*;
import kafka.zk.ZooKeeperTestHarness;
import org.apache.kafka.common.security.auth.KafkaPrincipal;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static org.junit.Assert.assertTrue;

public class SessionReportTest extends ZooKeeperTestHarness {

    private DidiAuthorizer didiAuthorizer;

    @Before
    public void setUp() {
        super.setUp();
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        LoginManager.getInstance().start("0", 0, zkClient(), scheduledThreadPoolExecutor, "",
                List.of("kafka:*:false"));
        SessionManager.getInstance().start(scheduledThreadPoolExecutor);

        didiAuthorizer = new DidiAuthorizer();
        didiAuthorizer.start("0", 0, zkClient(), scheduledThreadPoolExecutor, "",
                List.of("*:kafka:Read:Allow", "*:kafka:Write:Allow"));
    }

    @Test
    public void testGetTopicHeartBeat() throws Exception {
        RequestChannel.Session session =
                new RequestChannel.Session(
                        new KafkaPrincipal(KafkaPrincipal.USER_TYPE, "kafka"), InetAddress.getLocalHost());

        Operation operation1 = Read$.MODULE$;
        Resource resource1 = new Resource(Topic$.MODULE$, "test");
        didiAuthorizer.authorize(session, operation1, resource1);

        Operation operation2 = Write$.MODULE$;
        Resource resource2 = new Resource(Topic$.MODULE$, "test");
        didiAuthorizer.authorize(session, operation2, resource2);

        String key = "kafka#" + InetAddress.getLocalHost().getHostAddress() + "#unknown";
        SessionReport sessionReport = SessionReport.getInstance();
        JSONObject jsonObject = JSONObject.parseObject(sessionReport.getTopicHeartBeat());
        String produceArray = JSONObject.parseObject(jsonObject.get("produce").toString()).get("test").toString();
        String fetchArray = JSONObject.parseObject(jsonObject.get("fetch").toString()).get("test").toString();
        assertTrue("produce size should be one", JSONObject.parseArray(produceArray).size() == 1 && JSONObject.parseArray(produceArray).get(0).toString().equals(key));
        assertTrue("fetch size should be one", JSONObject.parseArray(produceArray).size() == 1 && JSONObject.parseArray(fetchArray).get(0).toString().equals(key));
    }


    @After
    public void stop() throws Exception{
        SessionManager.getInstance().shutdown();
        didiAuthorizer.stop();
    }
}
