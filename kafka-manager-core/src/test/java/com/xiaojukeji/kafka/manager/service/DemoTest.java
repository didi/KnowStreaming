package com.xiaojukeji.kafka.manager.service;

import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.service.config.BaseTest;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.when;

public class DemoTest extends BaseTest {

    @Autowired
    private ClusterService clusterService;

    @Mock
    private AppService appServiceMock;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test() {
        Assert.assertNull(clusterService.getById(100L));
    }

    @Test
    public void testMock() {
        when(appServiceMock.getByAppId("100")).thenReturn(new AppDO());
        Assert.assertNotNull(appServiceMock.getByAppId("100"));
    }


}
