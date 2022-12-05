package com.xiaojukeji.know.streaming.km.persistence.mysql.topic;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojukeji.know.streaming.km.common.bean.po.topic.TopicPO;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicDAO extends BaseMapper<TopicPO> {
    int replaceAll(TopicPO topicPO);

    int updateConfigById(TopicPO topicPO);
}
