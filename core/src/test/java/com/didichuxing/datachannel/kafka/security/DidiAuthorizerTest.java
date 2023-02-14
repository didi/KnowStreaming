package com.didichuxing.datachannel.kafka.security;

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

import java.io.FileReader;
import java.net.InetAddress;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static org.junit.Assert.*;

public class DidiAuthorizerTest extends ZooKeeperTestHarness  {

    private DidiAuthorizer didiAuthorizer;

    @Before
    public void setUp() {
        super.setUp();
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        SessionManager.getInstance().start(scheduledThreadPoolExecutor);
        didiAuthorizer = new DidiAuthorizer();
        didiAuthorizer.start("0", 0, zkClient(), scheduledThreadPoolExecutor, "",
                List.of("*:kafka:Read:Allow", "*:kafka:Write:Allow"));
    }

    @Test
    public void testAuthorize() throws Exception {
        RequestChannel.Session session =
                new RequestChannel.Session(
                        new KafkaPrincipal(KafkaPrincipal.USER_TYPE, "kafka"), InetAddress.getByName("localhost"), -1);
        Operation operation = Read$.MODULE$;
        Resource resource = new Resource(Topic$.MODULE$, "test");
        assertTrue(didiAuthorizer.authorize(session, operation, resource));
    }

    @After
    public void stop() throws Exception{
        LoginManager.getInstance().shutdown();
        SessionManager.getInstance().shutdown();
        didiAuthorizer.stop();
    }
}
