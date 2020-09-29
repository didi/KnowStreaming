package com.xiaojukeji.kafka.manager.task.component;

import com.xiaojukeji.kafka.manager.common.constant.LogConstant;
import com.xiaojukeji.kafka.manager.common.utils.NetUtils;
import com.xiaojukeji.kafka.manager.dao.HeartbeatDao;
import com.xiaojukeji.kafka.manager.common.entity.pojo.HeartbeatDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author limeng
 * @date 20/8/10
 */
@Component
public class Heartbeat {
    private final static Logger LOGGER = LoggerFactory.getLogger(LogConstant.SCHEDULED_TASK_LOGGER);

    @Autowired
    private HeartbeatDao heartbeatDao;

    @Scheduled(cron = ScheduledTaskConstant.HEARTBEAT_CRON)
    public void ipFlush() {
        try {
            // 随机回退0-100ms, 增加随机性. 后续在select任务那块, 可以引入时间戳, 增大任务随机的概率
            Thread.sleep(Math.round(Math.random() * 1000));

            HeartbeatDO heartbeatDO = new HeartbeatDO();
            heartbeatDO.setIp(NetUtils.localIp());
            heartbeatDO.setHostname(NetUtils.localHostname());
            heartbeatDao.replace(heartbeatDO);
        } catch (Exception e) {
            LOGGER.error("flush heartbeat failed.", e);
        }
    }
}