package com.xiaojukeji.kafka.manager.dao.ha;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.JobLogDO;
import org.springframework.stereotype.Repository;

/**
 * Job的Log, 正常来说应该与TopicDao等放在一起的，但是因为使用了mybatis-plus，因此零时放在这个地方
 */
@Repository
public interface JobLogDao extends BaseMapper<JobLogDO> {
}
