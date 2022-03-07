package com.xiaojukeji.kafka.manager.bpm.order;

import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.OrderDetailDeleteClusterDTO;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.bpm.config.BaseTest;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.cluster.ClusterNameDTO;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author xuguang
 * @Date 2021/12/30
 */
public class DeleteClusterOrderTest extends BaseTest {

    @Value("${test.app.id}")
    private String APP_ID;

    private final static String IDC = "国内";

    private final static String EXTENSIONS = "{\"clusterId\":\"7\"}";

    private final static String INVALID_IDC = "xxx";

    @Value("${test.admin}")
    private String ADMIN;

    private final static Long REAL_CLUSTER_ID_IN_MYSQL = 1L;

    @Autowired
    @Qualifier("deleteClusterOrder")
    @InjectMocks
    private AbstractOrder deleteClusterOrder;

    @Mock
    private AppService appService;

    @Mock
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @Mock
    private ClusterService clusterService;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void checkExtensionFieldsAndGenerateTitleTest() {
        // paramIllegal
        checkExtensionFieldsAndGenerateTitle2ParamIllegalTest();
        // cluster not exist
        checkExtensionFieldsAndGenerateTitle2ClusterNotExistTest();
        // success
        checkExtensionFieldsAndGenerateTitle2SuccessTest();
    }

    private void checkExtensionFieldsAndGenerateTitle2ParamIllegalTest() {
        Result<String> result = deleteClusterOrder.checkExtensionFieldsAndGenerateTitle("{}");
        Assert.assertEquals(result.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2ClusterNotExistTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(Mockito.any())).thenReturn(null);
        Result<String> result = deleteClusterOrder.checkExtensionFieldsAndGenerateTitle(EXTENSIONS);
        Assert.assertEquals(result.getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2SuccessTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(Mockito.any())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);
        Result<String> result = deleteClusterOrder.checkExtensionFieldsAndGenerateTitle(EXTENSIONS);
        Assert.assertEquals(result.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test
    public void handleOrderDetailTest() {
        // operation forbidden
        handleOrderDetail2OperationForbiddenTest();
        // success
        handleOrderDetail2SuccessTest();
    }

    private void handleOrderDetail2OperationForbiddenTest() {
        Set<String> topics = new HashSet<>();
        topics.add("");
        Mockito.when(logicalClusterMetadataManager.getTopicNameSet(Mockito.any())).thenReturn(topics);
        OrderDO orderDO = new OrderDO();
        orderDO.setExtensions(EXTENSIONS);
        ResultStatus resultStatus = deleteClusterOrder.handleOrderDetail(orderDO, new OrderHandleBaseDTO(), ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.OPERATION_FORBIDDEN.getCode());
    }

    private void handleOrderDetail2SuccessTest() {
        Mockito.when(logicalClusterMetadataManager.getTopicNameSet(Mockito.any())).thenReturn(Collections.emptySet());
        OrderDO orderDO = new OrderDO();
        orderDO.setExtensions(EXTENSIONS);
        ResultStatus resultStatus = deleteClusterOrder.handleOrderDetail(orderDO, new OrderHandleBaseDTO(), ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test
    public void getOrderExtensionDetailDataTest() {
        ClusterNameDTO clusterNameDTO = new ClusterNameDTO();
        clusterNameDTO.setPhysicalClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        clusterNameDTO.setLogicalClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        clusterNameDTO.setLogicalClusterName("");
        clusterNameDTO.setPhysicalClusterName("");

        Mockito.when(clusterService.getClusterName(Mockito.any())).thenReturn(clusterNameDTO);
        Mockito.when(logicalClusterMetadataManager.getTopicNameSet(Mockito.any())).thenReturn(Collections.emptySet());

        OrderDetailDeleteClusterDTO data = (OrderDetailDeleteClusterDTO) deleteClusterOrder.getOrderExtensionDetailData(EXTENSIONS);
        Assert.assertEquals(data.getLogicalClusterId(), REAL_CLUSTER_ID_IN_MYSQL);
        Assert.assertEquals(data.getPhysicalClusterId(), REAL_CLUSTER_ID_IN_MYSQL);
    }
}
