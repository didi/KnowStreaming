package com.xiaojukeji.kafka.manager.monitor;

import com.xiaojukeji.kafka.manager.monitor.common.entry.Strategy;
import com.xiaojukeji.kafka.manager.monitor.component.AbstractMonitorService;
import com.xiaojukeji.kafka.manager.monitor.config.BaseTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * @author wyc
 * @date 2022/1/5
 */
public class AbstractMonitorServiceTest extends BaseTest {
    @Autowired
    @InjectMocks
    private AbstractMonitorService abstractMonitorService;

    @Mock
    private HttpURLConnection conn;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    private Strategy getStrategy() {
        Strategy strategy = new Strategy();
        strategy.setName("test_strategy");
        strategy.setId(1L);
        strategy.setPeriodDaysOfWeek("1");
        strategy.setPeriodHoursOfDay("24");
        strategy.setPriority(0);
        strategy.setStrategyFilterList(new ArrayList<>());
        strategy.setStrategyExpressionList(new ArrayList<>());
        strategy.setStrategyActionList(new ArrayList<>());
        return strategy;
    }
    @Test
    public void createStrategyTest() throws IOException {
        Strategy strategy = getStrategy();
        Integer i = abstractMonitorService.createStrategy(strategy);
        System.out.println(i);
    }
}
