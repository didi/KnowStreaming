package com.xiaojukeji.kafka.manager.task.component;

import com.google.common.collect.Lists;
import com.xiaojukeji.kafka.manager.common.constant.LogConstant;
import com.xiaojukeji.kafka.manager.common.utils.factory.DefaultThreadFactory;
import com.xiaojukeji.kafka.manager.common.utils.JsonUtils;
import com.xiaojukeji.kafka.manager.common.utils.NetUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.dao.HeartbeatDao;
import com.xiaojukeji.kafka.manager.common.entity.pojo.HeartbeatDO;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author limeng
 * @date 20/8/10
 */
public abstract class AbstractScheduledTask<E extends Comparable> implements SchedulingConfigurer {
    private final static Logger LOGGER = LoggerFactory.getLogger(LogConstant.SCHEDULED_TASK_LOGGER);

    @Autowired
    private HeartbeatDao heartbeatDao;

    private ExecutorService executorService;

    private volatile String scheduledCron = "0 0/1 * * * *";

    private volatile String scheduledName;

    public String getCron() {
        return this.scheduledCron;
    }

    public boolean modifyCron(String name, String cron) {
        return checkAndModifyCron(name, cron, false);
    }

    public String getScheduledName() {
        return scheduledName;
    }

    @PostConstruct
    void init() {
        CustomScheduled customSchedule = this.getClass().getAnnotation(CustomScheduled.class);
        if (ValidateUtils.isNull(customSchedule)) {
            LOGGER.error("extends AbstractScheduledTask must use CustomScheduled annotation.");
            System.exit(0);
        }
        this.scheduledName = customSchedule.name();

        checkAndModifyCron(customSchedule.name(), customSchedule.cron(), true);
        this.executorService = new ThreadPoolExecutor(
                customSchedule.threadNum(),
                customSchedule.threadNum(),
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new DefaultThreadFactory("CustomScheduled-" + customSchedule.name())
        );
        LOGGER.info("init custom scheduled finished, scheduledName:{} scheduledCron:{}.", scheduledName, scheduledCron);
    }

    private boolean checkAndModifyCron(String scheduledName, String scheduledCron, boolean existIfIllegal) {
        if (scheduledCron.matches(ScheduledTaskConstant.CRON_REG_EX)) {
            this.scheduledCron = scheduledCron;
            LOGGER.info("modify scheduledCron success, scheduledName:{} scheduledCron:{}."
                    , scheduledName, scheduledCron);
            return true;
        }

        LOGGER.error("modify scheduledCron failed, format invalid, scheduledName:{} scheduledCron:{}."
                , scheduledName, scheduledCron);
        if (existIfIllegal) {
            System.exit(0);
        }
        return false;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(20));
        taskRegistrar.addTriggerTask(new Runnable() {
            @Override
            public void run() {
                // 任务逻辑
                scheduleFunction();
            }
        }, new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                // 任务触发，可修改任务的执行周期
                CronTrigger trigger = new CronTrigger(scheduledCron);
                Date nextExec = trigger.nextExecutionTime(triggerContext);
                return nextExec;
            }
        });
    }

    public void scheduleAllTaskFunction() {
        List<E> taskList = this.listAllTasks();
        for (E elem: taskList) {
            executorService.submit(new BaseBizTask(elem, this));
        }
    }

    public void scheduleFunction() {
        LOGGER.info("customScheduled task start, scheduledName:{} scheduledCron:{}.", scheduledName, scheduledCron);

        List<E> tasks = this.listAllTasks();
        if (CollectionUtils.isEmpty(tasks)) {
            LOGGER.info("customScheduled task finished, empty task, scheduledName:{}.", scheduledName);
            return;
        }

        List<E> selectTasks = this.select(tasks);
        if (ValidateUtils.isEmptyList(selectTasks)) {
            LOGGER.info("customScheduled task finished, empty selected task, scheduledName:{}.", scheduledName);
            return;
        }
        LOGGER.info("customScheduled task running, selected tasks, IP:{} selectedTasks:{}.",
                NetUtils.localIp(), JsonUtils.toJSONString(selectTasks)
        );

        for (E elem : selectTasks) {
            executorService.submit(new BaseBizTask(elem, this));
        }
        LOGGER.info("customScheduled task finished, scheduledName:{}.", scheduledName);
    }

    private List<E> select(List<E> allTaskList) {
        long now = System.currentTimeMillis();

        if(ValidateUtils.isEmptyList(allTaskList)){
            return Lists.newArrayList();
        }
        Collections.sort(allTaskList);

        List<HeartbeatDO> hostList = heartbeatDao.selectActiveHosts(new Date(now - ScheduledTaskConstant.HEARTBEAT_TIME));
        if (ValidateUtils.isEmptyList(hostList)) {
            // 当前无机器注册，导致周期任务(Topic指标存DB等任务)不可被触发执行。
            // 大概率原因可能是：DB的时区不对，注册的时间错误导致查询不出来。
            // 如果是单台方式部署的Logi-KM，那么也可能是服务新上线，或者是服务不正常导致的。
            LOGGER.error("customScheduled task running, but without registrant, and so scheduled tasks can't execute, scheduledName:{}.", scheduledName);
            return Lists.newArrayList();
        }

        int idx = 0;
        while (idx < hostList.size()) {
            if (hostList.get(idx).getIp().equals(NetUtils.localIp())) {
                break;
            }
            idx++;
        }
        if (idx == hostList.size()) {
            // 当前机器未注册, 原因可能是：
            // 1、当前服务新上线，确实暂未注册到DB中。
            // 2、当前服务异常，比如进行FGC等，导致注册任务停止了。
            LOGGER.warn("customScheduled task running, registrants not conclude present machine, scheduledName:{}.", scheduledName);
            return Lists.newArrayList();
        }

        int count = allTaskList.size() / hostList.size();
        if (allTaskList.size() % hostList.size() != 0) {
            count += 1;
        }
        if (idx * count >= allTaskList.size()) {
            return Lists.newArrayList();
        }
        return allTaskList.subList(idx * count, Math.min(idx * count + count, allTaskList.size()));
    }

    protected abstract <E extends Comparable> List<E> listAllTasks();

    protected abstract void processTask(E task);
}