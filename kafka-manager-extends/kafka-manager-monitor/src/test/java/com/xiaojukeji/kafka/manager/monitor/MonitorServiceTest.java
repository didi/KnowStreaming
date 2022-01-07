package com.xiaojukeji.kafka.manager.monitor;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.MonitorRuleDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.dao.MonitorRuleDao;
import com.xiaojukeji.kafka.manager.dao.gateway.AppDao;
import com.xiaojukeji.kafka.manager.monitor.common.entry.Alert;
import com.xiaojukeji.kafka.manager.monitor.common.entry.Metric;
import com.xiaojukeji.kafka.manager.monitor.common.entry.Silence;
import com.xiaojukeji.kafka.manager.monitor.common.entry.Strategy;
import com.xiaojukeji.kafka.manager.monitor.common.entry.dto.MonitorRuleDTO;
import com.xiaojukeji.kafka.manager.monitor.common.entry.dto.MonitorSilenceDTO;
import com.xiaojukeji.kafka.manager.monitor.common.monitor.MonitorAlertDetail;
import com.xiaojukeji.kafka.manager.monitor.common.monitor.MonitorRuleSummary;
import com.xiaojukeji.kafka.manager.monitor.component.AbstractMonitorService;
import com.xiaojukeji.kafka.manager.monitor.config.BaseTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author wyc
 * @Date 2022/01/05
 */
public class MonitorServiceTest extends BaseTest {
    @Autowired
    @InjectMocks
    private MonitorService monitorService;

    @Mock
    private AbstractMonitorService abstractMonitorService;

    @Mock
    private MonitorRuleDao monitorRuleDao;

    @Mock
    private AppDao appDao;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    private MonitorRuleDTO getMonitorRuleDTO() {
        MonitorRuleDTO monitorRuleDTO = new MonitorRuleDTO();
        monitorRuleDTO.setAppId("appId");
        monitorRuleDTO.setName("name");
        monitorRuleDTO.setStrategyExpressionList(new ArrayList<>());
        monitorRuleDTO.setStrategyFilterList(new ArrayList<>());
        monitorRuleDTO.setStrategyActionList(new ArrayList<>());
        monitorRuleDTO.setId(1L);
        return monitorRuleDTO;
    }

    private MonitorRuleDO getMonitorRuleDO() {
        MonitorRuleDO monitorRuleDO = new MonitorRuleDO();
        monitorRuleDO.setAppId("appId");
        monitorRuleDO.setName("name");
        monitorRuleDO.setCreateTime(new Date());
        monitorRuleDO.setStrategyId(1L);
        monitorRuleDO.setId(1L);
        return monitorRuleDO;
    }

    private MonitorRuleSummary getMonitorRuleSummary() {
        MonitorRuleSummary summary = new MonitorRuleSummary();
        summary.setAppId("appId");
        summary.setAppName("appName");
        return summary;
    }

    private AppDO getAppDO() {
        AppDO appDO = new AppDO();
        appDO.setAppId("appId");
        appDO.setName("appName");
        return appDO;
    }

    private Strategy getStrategy() {
        Strategy strategy = new Strategy();
        strategy.setName("name");
        strategy.setStrategyActionList(new ArrayList<>());
        strategy.setStrategyExpressionList(new ArrayList<>());
        strategy.setStrategyFilterList(new ArrayList<>());
        return strategy;
    }

    private Alert getAlert() {
        Alert alert = new Alert();
        alert.setId(1L);
        alert.setStartTime(3700L);
        alert.setEndTime(3800L);
        return alert;
    }

    private MonitorAlertDetail getMonitorAlertDetail() {
        MonitorAlertDetail detail = new MonitorAlertDetail(null, null);
        return detail;
    }

    private Metric getMetric() {
        Metric metric = new Metric();
        return metric;
    }

    private MonitorSilenceDTO getMonitorSilenceDTO() {
        MonitorSilenceDTO dto = new MonitorSilenceDTO();
        dto.setId(1L);
        return dto;
    }

    private Silence getSilence() {
        Silence silence = new Silence();
        silence.setSilenceId(1L);
        return silence;
    }


    @Test
    public void createMonitorRuleTest() {
        // CALL_MONITOR_SYSTEM_ERROR
        createMonitorRule2CallMonitorSystemErrorTest();

        // 成功测试
        createMonitorRule2SuccessTest();

        // MYSQL_ERROR
        createMonitorRule2MySQLErrorTest();
    }

    private void createMonitorRule2CallMonitorSystemErrorTest() {
        MonitorRuleDTO dto = getMonitorRuleDTO();
        Mockito.when(abstractMonitorService.createStrategy(Mockito.any())).thenReturn(null);
        Assert.assertEquals(monitorService.createMonitorRule(dto, "admin"), ResultStatus.CALL_MONITOR_SYSTEM_ERROR);
    }

    private void createMonitorRule2SuccessTest() {
        MonitorRuleDTO dto = getMonitorRuleDTO();
        Mockito.when(abstractMonitorService.createStrategy(Mockito.any())).thenReturn(1);
        Mockito.when(monitorRuleDao.insert(Mockito.any())).thenReturn(1);
        Assert.assertEquals(monitorService.createMonitorRule(dto, "admin"), ResultStatus.SUCCESS);
    }

    private void createMonitorRule2MySQLErrorTest() {
        MonitorRuleDTO dto = getMonitorRuleDTO();
        Mockito.when(abstractMonitorService.createStrategy(Mockito.any())).thenReturn(1);
        Mockito.when(monitorRuleDao.insert(Mockito.any())).thenReturn(-1);
        Assert.assertEquals(monitorService.createMonitorRule(dto, "admin"), ResultStatus.MYSQL_ERROR);
    }


    @Test
    public void deleteMonitorRuleTest() {
        // MONITOR_NOT_EXIST
        deleteMonitorRule2MonitorNotExistTest();

        // CALL_MONITOR_SYSTEM_ERROR
        deleteMonitorRule2CallMonitorSystemErrorTest();

        // 成功测试
        deleteMonitorRule2SuccessTest();

        // MYSQL_ERROR
        deleteMonitorRule2MySQLErrorTest();
    }

    private void deleteMonitorRule2MonitorNotExistTest() {
        Mockito.when(monitorRuleDao.getById(Mockito.anyLong())).thenReturn(null);
        Assert.assertEquals(monitorService.deleteMonitorRule(1L, "admin"), ResultStatus.MONITOR_NOT_EXIST);
    }

    private void deleteMonitorRule2CallMonitorSystemErrorTest() {
        MonitorRuleDO monitorRuleDO = getMonitorRuleDO();
        Mockito.when(monitorRuleDao.getById(Mockito.anyLong())).thenReturn(monitorRuleDO);
        Mockito.when(abstractMonitorService.deleteStrategyById(Mockito.anyLong())).thenReturn(false);
        Assert.assertEquals(monitorService.deleteMonitorRule(1L, "admin"), ResultStatus.CALL_MONITOR_SYSTEM_ERROR);
    }

    private void deleteMonitorRule2SuccessTest() {
        MonitorRuleDO monitorRuleDO = getMonitorRuleDO();
        Mockito.when(monitorRuleDao.getById(Mockito.anyLong())).thenReturn(monitorRuleDO);
        Mockito.when(abstractMonitorService.deleteStrategyById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(monitorRuleDao.deleteById(Mockito.any())).thenReturn(1);
        Assert.assertEquals(monitorService.deleteMonitorRule(1L, "admin"), ResultStatus.SUCCESS);
    }

    private void deleteMonitorRule2MySQLErrorTest() {
        MonitorRuleDO monitorRuleDO = getMonitorRuleDO();
        Mockito.when(monitorRuleDao.getById(Mockito.anyLong())).thenReturn(monitorRuleDO);
        Mockito.when(abstractMonitorService.deleteStrategyById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(monitorRuleDao.deleteById(Mockito.any())).thenReturn(-1);
        Assert.assertEquals(monitorService.deleteMonitorRule(1L, "admin"), ResultStatus.MYSQL_ERROR);
    }


    @Test
    public void modifyMonitorRuleTest() {
        // MONITOR_NOT_EXIST
        modifyMonitorRule2MonitorNotExistTest();

        // CALL_MONITOR_SYSTEM_ERROR
        modifyMonitorRule2CallMonitorSystemErrorTest();

        // 成功测试
        modifyMonitorRule2SuccessTest();

        // MYSQL_ERROR
        modifyMonitorRule2MySQLErrorTest();
    }

    private void modifyMonitorRule2MonitorNotExistTest() {
        MonitorRuleDTO dto = getMonitorRuleDTO();
        Mockito.when(monitorRuleDao.getById(Mockito.anyLong())).thenReturn(null);
        Assert.assertEquals(monitorService.modifyMonitorRule(dto, "admin"), ResultStatus.MONITOR_NOT_EXIST);
    }

    private void modifyMonitorRule2CallMonitorSystemErrorTest() {
        MonitorRuleDO monitorRuleDO = getMonitorRuleDO();
        MonitorRuleDTO dto = getMonitorRuleDTO();
        Mockito.when(monitorRuleDao.getById(Mockito.anyLong())).thenReturn(monitorRuleDO);
        Mockito.when(abstractMonitorService.modifyStrategy(Mockito.any())).thenReturn(false);
        Assert.assertEquals(monitorService.modifyMonitorRule(dto, "admin"), ResultStatus.CALL_MONITOR_SYSTEM_ERROR);
    }

    private void modifyMonitorRule2SuccessTest() {
        MonitorRuleDO monitorRuleDO = getMonitorRuleDO();
        MonitorRuleDTO dto = getMonitorRuleDTO();
        Mockito.when(monitorRuleDao.getById(Mockito.anyLong())).thenReturn(monitorRuleDO);
        Mockito.when(abstractMonitorService.modifyStrategy(Mockito.any())).thenReturn(true);
        Mockito.when(monitorRuleDao.updateById(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(1);
        Assert.assertEquals(monitorService.modifyMonitorRule(dto, "admin"), ResultStatus.SUCCESS);
    }

    private void modifyMonitorRule2MySQLErrorTest() {
        MonitorRuleDO monitorRuleDO = getMonitorRuleDO();
        MonitorRuleDTO dto = getMonitorRuleDTO();
        Mockito.when(monitorRuleDao.getById(Mockito.anyLong())).thenReturn(monitorRuleDO);
        Mockito.when(abstractMonitorService.modifyStrategy(Mockito.any())).thenReturn(true);
        Mockito.when(monitorRuleDao.updateById(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(-1);
        Assert.assertEquals(monitorService.modifyMonitorRule(dto, "admin"), ResultStatus.MYSQL_ERROR);
    }


    @Test
    public void getMonitorRulesTest() {
        // 返回空集合测试1
        getMonitorRules2EmptyListTest1();

        // 返回空集合测试2
        getMonitorRules2EmptyListTest2();

        // 成功测试
        getMonitorRules2SuccessTest();

    }

    private void getMonitorRules2EmptyListTest1() {
        Mockito.when(monitorRuleDao.listAll()).thenReturn(new ArrayList<>());
        Assert.assertTrue(monitorService.getMonitorRules("admin").isEmpty());
    }

    private void getMonitorRules2EmptyListTest2() {
        MonitorRuleDO ruleDO = getMonitorRuleDO();
        List<MonitorRuleDO> monitorRuleDOList = new ArrayList<>(Arrays.asList(ruleDO));
        Mockito.when(monitorRuleDao.listAll()).thenReturn(monitorRuleDOList);
        Mockito.when(appDao.getByPrincipal(Mockito.anyString())).thenReturn(new ArrayList<>());
        Assert.assertTrue(monitorService.getMonitorRules("admin").isEmpty());
    }

    private void getMonitorRules2SuccessTest() {
        MonitorRuleDO ruleDO = getMonitorRuleDO();
        List<MonitorRuleDO> monitorRuleDOList = new ArrayList<>(Arrays.asList(ruleDO));
        Mockito.when(monitorRuleDao.listAll()).thenReturn(monitorRuleDOList);

        AppDO appDO = getAppDO();
        List<AppDO> appDOList = new ArrayList<>(Arrays.asList(appDO));
        Mockito.when(appDao.getByPrincipal(Mockito.anyString())).thenReturn(appDOList);
        List<MonitorRuleSummary> result = monitorService.getMonitorRules("admin");
        Assert.assertTrue(!result.isEmpty() && result.stream().allMatch(monitorRuleSummary -> monitorRuleSummary.getAppId().equals(appDO.getAppId()) &&
                monitorRuleSummary.getAppName().equals(appDO.getName())));
    }


    @Test
    public void getMonitorRuleDetailTest() {
        // MONITOR_NOT_EXIST
        getMonitorRuleDetail2MonitorNotExist();

        // CALL_MONITOR_SYSTEM_ERROR
        getMonitorRuleDetail2CallMonitorSystemErrorTest();

        // 成功测试
        getMonitorRuleDetail2Success();
    }

    private void getMonitorRuleDetail2MonitorNotExist() {
        Assert.assertEquals(monitorService.getMonitorRuleDetail(null).toString(), Result.buildFrom(ResultStatus.MONITOR_NOT_EXIST).toString());
    }

    private void getMonitorRuleDetail2CallMonitorSystemErrorTest() {
        MonitorRuleDO ruleDO = getMonitorRuleDO();
        Mockito.when(abstractMonitorService.getStrategyById(Mockito.anyLong())).thenReturn(null);
        Assert.assertEquals(monitorService.getMonitorRuleDetail(ruleDO).toString(), Result.buildFrom(ResultStatus.CALL_MONITOR_SYSTEM_ERROR).toString());
    }

    private void getMonitorRuleDetail2Success() {
        MonitorRuleDO ruleDO = getMonitorRuleDO();
        Strategy strategy = getStrategy();
        Mockito.when(abstractMonitorService.getStrategyById(Mockito.anyLong())).thenReturn(strategy);
        MonitorRuleDTO result = monitorService.getMonitorRuleDetail(ruleDO).getData();
        Assert.assertTrue(result.getAppId().equals(ruleDO.getAppId()) &&
                result.getName().equals(ruleDO.getName()));
    }


    @Test
    public void getMonitorAlertHistoryTest() {
        // MONITOR_NOT_EXIST
        getMonitorAlertHistory2MonitorNotExistTest();

        // CALL_MONITOR_SYSTEM_ERROR
        getMonitorAlertHistory2CallMonitorSystemErrorTest();

        // 成功测试
        getMonitorAlertHistory2SuccessTest();
    }

    private void getMonitorAlertHistory2MonitorNotExistTest() {
        Mockito.when(monitorRuleDao.getById(Mockito.anyLong())).thenReturn(null);
        Assert.assertEquals(monitorService.getMonitorAlertHistory(1L, 1L, 1L).toString(), Result.buildFrom(ResultStatus.MONITOR_NOT_EXIST).toString());
    }

    private void getMonitorAlertHistory2CallMonitorSystemErrorTest() {
        MonitorRuleDO ruleDO = getMonitorRuleDO();
        Mockito.when(monitorRuleDao.getById(Mockito.anyLong())).thenReturn(ruleDO);
        Mockito.when(abstractMonitorService.getAlerts(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(null);
        Assert.assertEquals(monitorService.getMonitorAlertHistory(1L, 1L, 1L).toString(), Result.buildFrom(ResultStatus.CALL_MONITOR_SYSTEM_ERROR).toString());
    }

    private void getMonitorAlertHistory2SuccessTest() {
        MonitorRuleDO ruleDO = getMonitorRuleDO();
        Mockito.when(monitorRuleDao.getById(Mockito.anyLong())).thenReturn(ruleDO);
        Alert alert = getAlert();
        List<Alert> alertList = new ArrayList<>(Arrays.asList(alert));
        Mockito.when(abstractMonitorService.getAlerts(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(alertList);
        List<Alert> result = monitorService.getMonitorAlertHistory(1L, 1L, 1L).getData();
        Assert.assertTrue(!result.isEmpty() && result.stream().allMatch(alert1 -> alert1.getId().equals(alert.getId())));
    }

    @Test
    public void getMonitorAlertDetailTest() {
        // CALL_MONITOR_SYSTEM_ERROR
        getMonitorAlertDetail2CallMonitorSystemErrorTest();

        // MONITOR_NOT_EXIST
        getMonitorAlertDetail2MonitorNotExistTest();

        // 成功测试
        getMonitorAlertDetail2SuccessTest();
    }

    private void getMonitorAlertDetail2CallMonitorSystemErrorTest() {
        Mockito.when(abstractMonitorService.getAlertById(Mockito.anyLong())).thenReturn(null);
        Assert.assertEquals(monitorService.getMonitorAlertDetail(1L).toString(), Result.buildFrom(ResultStatus.CALL_MONITOR_SYSTEM_ERROR).toString());
    }

    private void getMonitorAlertDetail2MonitorNotExistTest() {
        Alert alert = getAlert();
        Mockito.when(abstractMonitorService.getAlertById(Mockito.anyLong())).thenReturn(alert);
        Mockito.when(monitorRuleDao.getByStrategyId(Mockito.anyLong())).thenReturn(null);
        Assert.assertEquals(monitorService.getMonitorAlertDetail(1L).toString(), Result.buildFrom(ResultStatus.MONITOR_NOT_EXIST).toString());
    }

    private void getMonitorAlertDetail2SuccessTest() {
        Alert alert = getAlert();
        Mockito.when(abstractMonitorService.getAlertById(Mockito.anyLong())).thenReturn(alert);
        MonitorRuleDO ruleDO = getMonitorRuleDO();
        Mockito.when(monitorRuleDao.getByStrategyId(Mockito.any())).thenReturn(ruleDO);
        Metric metric = getMetric();
        Mockito.when(abstractMonitorService.getMetrics(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(metric);
        MonitorAlertDetail data = monitorService.getMonitorAlertDetail(1L).getData();
        MonitorAlertDetail detail = getMonitorAlertDetail();
        detail.setAlert(alert);
        detail.setMetric(metric);

        Assert.assertEquals(data.toString(), detail.toString());

    }

    @Test
    public void createSilenceTest() {
        // MONITOR_NOT_EXIST
        createSilence2MonitorNotExistTest();

        // 未实现测试
        createSilence2EmptyTest();

        // CALL_MONITOR_SYSTEM_ERROR
        createSilence2CallMonitorSystemErrorTest();
    }

    private void createSilence2MonitorNotExistTest() {
        MonitorSilenceDTO monitorSilenceDTO = getMonitorSilenceDTO();
        Mockito.when(monitorRuleDao.getById(Mockito.any())).thenReturn(null);
        Assert.assertEquals(monitorService.createSilence(monitorSilenceDTO, "admin").toString(), Result.buildFrom(ResultStatus.MONITOR_NOT_EXIST).toString());
    }

    private void createSilence2EmptyTest() {
        MonitorSilenceDTO monitorSilenceDTO = getMonitorSilenceDTO();
        MonitorRuleDO monitorRuleDO = getMonitorRuleDO();
        Mockito.when(monitorRuleDao.getById(Mockito.any())).thenReturn(monitorRuleDO);
        Mockito.when(abstractMonitorService.createSilence(Mockito.any())).thenReturn(true);
        Assert.assertNull(monitorService.createSilence(monitorSilenceDTO, "admin").getData());
    }

    private void createSilence2CallMonitorSystemErrorTest() {
        MonitorSilenceDTO monitorSilenceDTO = getMonitorSilenceDTO();
        MonitorRuleDO monitorRuleDO = getMonitorRuleDO();
        Mockito.when(monitorRuleDao.getById(Mockito.any())).thenReturn(monitorRuleDO);
        Mockito.when(abstractMonitorService.createSilence(Mockito.any())).thenReturn(false);
        Assert.assertEquals(monitorService.createSilence(monitorSilenceDTO, "admin").toString(), Result.buildFrom(ResultStatus.CALL_MONITOR_SYSTEM_ERROR).toString());
    }


    @Test
    public void getSilencesTest() {
        // CALL_MONITOR_SYSTEM_ERROR
        getSilence2CallMonitorSystemErrorTest();

        // 成功测试
        getSilence2SuccessTest();
    }

    private void getSilence2CallMonitorSystemErrorTest() {
        Mockito.when(abstractMonitorService.getSilences(Mockito.any())).thenReturn(null);
        Assert.assertEquals(monitorService.getSilences(1L).toString(), Result.buildFrom(ResultStatus.CALL_MONITOR_SYSTEM_ERROR).toString());
    }

    private void getSilence2SuccessTest() {
        Silence silence = getSilence();
        List<Silence> silenceList = new ArrayList<>(Arrays.asList(silence));
        Mockito.when(abstractMonitorService.getSilences(Mockito.any())).thenReturn(silenceList);
        List<Silence> data = monitorService.getSilences(1L).getData();
        Assert.assertTrue(!data.isEmpty() && data.stream().allMatch(silence1 -> silence1.getSilenceId().equals(silence.getSilenceId())));
    }
}
