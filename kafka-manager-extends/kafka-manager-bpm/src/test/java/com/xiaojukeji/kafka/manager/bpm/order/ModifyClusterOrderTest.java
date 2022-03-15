package com.xiaojukeji.kafka.manager.bpm.order;

import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.OrderDetailModifyClusterDTO;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.bpm.config.BaseTest;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.cluster.ClusterNameDTO;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import org.junit.Assert;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author xuguang
 * @Date 2021/12/30
 */
public class ModifyClusterOrderTest extends BaseTest {

    @Value("${test.admin}")
    private String ADMIN;

    private static final String INVALID_USER_NAME = "xxxxx";

    private static final Integer INVALID_ORDER_TYPE = -1;

    private static final Integer APPLY_TOPIC_TYPE = 0;

    private static final Long ORDER_ID = 1L;

    private static final Long INVALID_ORDER_ID = -1L;

    private static final String EXTENSIONS = "{\"clusterId\":7}";

    @Value("${test.phyCluster.id}")
    private Long REAL_CLUSTER_ID_IN_MYSQL;

    @Autowired
    @Qualifier("modifyClusterOrder")
    @InjectMocks
    private AbstractOrder modifyClusterOrder;

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
        checkExtensionFieldsAndGenerateTitle2ZSuccessTest();
    }

    private void checkExtensionFieldsAndGenerateTitle2ParamIllegalTest() {
        Result<String> result = modifyClusterOrder.checkExtensionFieldsAndGenerateTitle("{}");
        Assert.assertEquals(result.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2ClusterNotExistTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(Mockito.anyLong())).thenReturn(null);

        Result<String> result = modifyClusterOrder.checkExtensionFieldsAndGenerateTitle(EXTENSIONS);
        Assert.assertEquals(result.getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    private void checkExtensionFieldsAndGenerateTitle2ZSuccessTest() {
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(
                Mockito.anyLong())).thenReturn(REAL_CLUSTER_ID_IN_MYSQL);

        Result<String> result = modifyClusterOrder.checkExtensionFieldsAndGenerateTitle(EXTENSIONS);
        Assert.assertEquals(result.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test
    public void handleOrderDetailTest() {
        ResultStatus resultStatus = modifyClusterOrder.handleOrderDetail(new OrderDO(), new OrderHandleBaseDTO(), ADMIN);
        Assert.assertEquals(resultStatus.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test
    public void getOrderExtensionDetailDataTest() {
        ClusterNameDTO clusterNameDTO = new ClusterNameDTO();
        clusterNameDTO.setPhysicalClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        clusterNameDTO.setPhysicalClusterName("");
        clusterNameDTO.setLogicalClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        clusterNameDTO.setLogicalClusterName("");
        Mockito.when(clusterService.getClusterName(Mockito.any())).thenReturn(clusterNameDTO);
        OrderDetailModifyClusterDTO data = (OrderDetailModifyClusterDTO) modifyClusterOrder.getOrderExtensionDetailData(EXTENSIONS);
        Assert.assertEquals(data.getPhysicalClusterId(), REAL_CLUSTER_ID_IN_MYSQL);
        Assert.assertEquals(data.getLogicalClusterId(), REAL_CLUSTER_ID_IN_MYSQL);
    }

}
