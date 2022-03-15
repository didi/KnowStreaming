package com.xiaojukeji.kafka.manager.account.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;

@SpringBootTest(classes = CoreSpringBootStartUp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = CoreSpringBootStartUp.class)
public class BaseTest extends AbstractTransactionalTestNGSpringContextTests {

}
