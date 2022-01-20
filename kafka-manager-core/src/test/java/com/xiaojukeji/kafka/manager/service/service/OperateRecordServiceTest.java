package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.bizenum.ModuleEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.OperateEnum;
import com.xiaojukeji.kafka.manager.common.entity.dto.rd.OperateRecordDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OperateRecordDO;
import com.xiaojukeji.kafka.manager.service.config.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wyc
 * @date 2021/12/8
 */
public class OperateRecordServiceTest extends BaseTest {
    @Autowired
    private OperateRecordService operateRecordService;

    @DataProvider(name = "operateRecordDO")
    public Object[][] provideOperateRecordDO() {
        OperateRecordDO operateRecordDO = new OperateRecordDO();
        operateRecordDO.setId(3L);
        // 0:topic, 1:应用, 2:配额, 3:权限, 4:集群, 5:分区, 6:Gateway配置, -1:未知
        operateRecordDO.setModuleId(ModuleEnum.CLUSTER.getCode());
        // 0:新增, 1:删除, 2:修改
        operateRecordDO.setOperateId(OperateEnum.ADD.getCode());
        // topic名称、app名称
        operateRecordDO.setResource("testOpRecord");
        operateRecordDO.setContent("testContent");
        operateRecordDO.setOperator("admin");
        return new Object[][] {{operateRecordDO}};
    }


    private OperateRecordDTO getOperateRecordDTO() {
        OperateRecordDTO dto = new OperateRecordDTO();
        dto.setModuleId(ModuleEnum.CLUSTER.getCode());
        dto.setOperateId(OperateEnum.ADD.getCode());
        dto.setOperator("admin");
        return dto;
    }


    @Test(dataProvider = "operateRecordDO", description = "插入操作记录成功测试")
    public void insert2SuccessTest(OperateRecordDO operateRecordDO) {
        int result = operateRecordService.insert(operateRecordDO);
        Assert.assertEquals(result, 1);
    }



    @Test(description = "插入的重载方法操作成功测试")
    public void insert2SuccessTest1() {
        Map<String, String> content = new HashMap<>();
        content.put("key", "value");
        int result = operateRecordService.insert("admin", ModuleEnum.CLUSTER, "testOpRecord", OperateEnum.ADD, content);
        Assert.assertEquals(result, 1);
    }


    @Test(dataProvider = "operateRecordDO")
    public void queryByConditionTest(OperateRecordDO operateRecordDO) {
        operateRecordService.insert(operateRecordDO);
        // endTime和startTime都是null
        queryByConditionTest3(operateRecordDO);

        // startTime是null
        queryByConditionTest1(operateRecordDO);

        // endTime是null
        queryByConditionTest2(operateRecordDO);

        // endTime和startTime都不是null
        queryByConditionTest4(operateRecordDO);

    }


    private void queryByConditionTest1(OperateRecordDO operateRecordDO) {
        OperateRecordDTO dto = getOperateRecordDTO();
        dto.setEndTime(new Date().getTime());
        List<OperateRecordDO> queryResult = operateRecordService.queryByCondition(dto);
        Assert.assertFalse(queryResult.isEmpty());
        // 判断查询得到的OperateRecordDO中日期是否符合要求
        Assert.assertTrue(queryResult.stream().allMatch(operateRecordDO1 ->
                operateRecordDO1.getCreateTime().after(new Date(0L)) &&
                        operateRecordDO1.getCreateTime().before(new Date()) &&
                        operateRecordDO1.getModuleId().equals(dto.getModuleId()) &&
                        operateRecordDO1.getOperateId().equals(dto.getOperateId()) &&
                        operateRecordDO1.getOperator().equals(dto.getOperator())));
    }

    private void queryByConditionTest2(OperateRecordDO operateRecordDO) {
        OperateRecordDTO dto = getOperateRecordDTO();
        dto.setStartTime(new Date().getTime());
        // 查询的是create_time >= startTime, 因为创建时间在当前时间之前，因此查到的数据是空的
        List<OperateRecordDO> queryResult = operateRecordService.queryByCondition(dto);
        Assert.assertTrue(queryResult.isEmpty());
    }


    private void queryByConditionTest3(OperateRecordDO operateRecordDO) {
        OperateRecordDTO dto = getOperateRecordDTO();
        List<OperateRecordDO> queryResult = operateRecordService.queryByCondition(dto);
        Assert.assertFalse(queryResult.isEmpty());

        Assert.assertTrue(queryResult.stream().allMatch(operateRecordDO1 ->
                operateRecordDO1.getCreateTime().after(new Date(0L)) &&
                        operateRecordDO1.getCreateTime().before(new Date()) &&
                        operateRecordDO1.getModuleId().equals(dto.getModuleId()) &&
                        operateRecordDO1.getOperateId().equals(dto.getOperateId()) &&
                        operateRecordDO1.getOperator().equals(dto.getOperator())));
    }

    private void queryByConditionTest4(OperateRecordDO operateRecordDO) {
        OperateRecordDTO dto = getOperateRecordDTO();
        dto.setStartTime(0L);
        dto.setEndTime(1649036393371L);
        List<OperateRecordDO> queryResult = operateRecordService.queryByCondition(dto);
        Assert.assertFalse(queryResult.isEmpty());

        Assert.assertTrue(queryResult.stream().allMatch(operateRecordDO1 ->
                operateRecordDO1.getCreateTime().after(new Date(0L)) &&
                        operateRecordDO1.getCreateTime().before(new Date()) &&
                        operateRecordDO1.getModuleId().equals(dto.getModuleId()) &&
                        operateRecordDO1.getOperateId().equals(dto.getOperateId()) &&
                        operateRecordDO1.getOperator().equals(dto.getOperator())));
    }
}
