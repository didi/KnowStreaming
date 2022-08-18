package com.xiaojukeji.know.streaming.km.core.service.job.handler;


import com.xiaojukeji.know.streaming.km.common.constant.JobConstant;
import com.xiaojukeji.know.streaming.km.common.enums.job.JobTypeEnum;
import org.springframework.stereotype.Component;

@Component(JobConstant.TOPIC_REPLICA_MOVE)
public class TopicReplicaMoveJobHandler extends AbstractReassignJobHandler {

    @Override
    public JobTypeEnum type() {
        return JobTypeEnum.TOPIC_REPLICA_MOVE;
    }
}
