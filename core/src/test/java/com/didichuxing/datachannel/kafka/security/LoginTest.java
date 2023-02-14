package com.didichuxing.datachannel.kafka.security;

import com.didichuxing.datachannel.kafka.security.login.LoginManager;
import com.didichuxing.datachannel.kafka.security.login.User;
import kafka.zk.ZooKeeperTestHarness;
import org.junit.*;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static org.junit.Assert.*;

public class LoginTest extends ZooKeeperTestHarness {


    @Before
    public void setUp() {
        super.setUp();
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        LoginManager.getInstance().start("0", 0, zkClient(), scheduledThreadPoolExecutor, "",
            List.of("kafka-admin:diditest@bdt:true", "kafka:12345:false"));
    }

    @Test
    public void testUsers() throws Exception {
        assertFalse(LoginManager.getInstance().login("admin", "12345", ""));
        User user = LoginManager.getInstance().getUser("admin");
        assertTrue(user.isSuperUser());

        assertTrue(LoginManager.getInstance().login("kafka-admin", "diditest@bdt", ""));
        user = LoginManager.getInstance().getUser("kafka-admin");
        assertTrue(user.isSuperUser());

        user = LoginManager.getInstance().getUser("ANONYMOUS");
        assertTrue(user.isSuperUser());

        assertTrue(LoginManager.getInstance().login("kafka", "12345", ""));
        assertFalse(LoginManager.getInstance().login("kafka", "123456", ""));
        user = LoginManager.getInstance().getUser("kafka");
        assertFalse(user.isSuperUser());
    }

    @After
    public void stop() throws Exception{
        LoginManager.getInstance().shutdown();
    }
}
