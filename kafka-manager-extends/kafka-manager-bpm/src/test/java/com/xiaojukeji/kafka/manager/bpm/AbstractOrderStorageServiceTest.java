package com.xiaojukeji.kafka.manager.bpm;

import com.xiaojukeji.kafka.manager.bpm.component.AbstractOrderStorageService;
import com.xiaojukeji.kafka.manager.bpm.config.BaseTest;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.dao.OrderDao;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author wyc
 * @date 2022/1/5
 */
public class AbstractOrderStorageServiceTest extends BaseTest {
    @Autowired
    @InjectMocks
    private AbstractOrderStorageService abstractOrderStorageService;

    @Mock
    private OrderDao orderDao;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    private OrderDO getOrderDO() {
        OrderDO orderDO = new OrderDO();
        orderDO.setApplicant("applicant");
        return orderDO;
    }


    @Test
    public void cancelTest() {
        // 返回null测试
        cancel2ReturnNullTest();

        // 无权限测试
        cancel2WithoutAuthority();

        // 成功测试
        cancel2SuccessTest();

        // 数据库错误测试
        cancel2MySQLErrorTest();
    }

    private void cancel2ReturnNullTest() {
        Mockito.when(orderDao.getById(Mockito.anyLong())).thenReturn(null);
        Assert.assertEquals(abstractOrderStorageService.cancel(1L, "applicant"), ResultStatus.ORDER_NOT_EXIST);
    }

    private void cancel2WithoutAuthority() {
        OrderDO orderDO = getOrderDO();
        Mockito.when(orderDao.getById(Mockito.any())).thenReturn(orderDO);
        Assert.assertEquals(abstractOrderStorageService.cancel(1L, "username"), ResultStatus.USER_WITHOUT_AUTHORITY);
    }

    private void cancel2SuccessTest() {
        OrderDO orderDO = getOrderDO();
        Mockito.when(orderDao.getById(Mockito.anyLong())).thenReturn(orderDO);
        Mockito.when(orderDao.updateOrderStatusById(Mockito.anyLong(), Mockito.anyInt())).thenReturn(1);
        Assert.assertEquals(abstractOrderStorageService.cancel(1L, "applicant"), ResultStatus.SUCCESS);
    }

    private void cancel2MySQLErrorTest() {
        OrderDO orderDO = getOrderDO();
        Mockito.when(orderDao.getById(Mockito.anyLong())).thenReturn(orderDO);
        Mockito.when(orderDao.updateOrderStatusById(Mockito.anyLong(), Mockito.anyInt())).thenReturn(0);
        Assert.assertEquals(abstractOrderStorageService.cancel(1L, "applicant"), ResultStatus.MYSQL_ERROR);
    }

}
