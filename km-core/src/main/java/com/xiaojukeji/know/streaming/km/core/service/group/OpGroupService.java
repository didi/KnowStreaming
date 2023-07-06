package com.xiaojukeji.know.streaming.km.core.service.group;

import com.xiaojukeji.know.streaming.km.common.bean.entity.param.group.DeleteGroupParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.group.DeleteGroupTopicParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.group.DeleteGroupTopicPartitionParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;

public interface OpGroupService {
    /**
     * 删除Offset
     */
    Result<Void> deleteGroupOffset(DeleteGroupParam param, String operator);
    Result<Void> deleteGroupTopicOffset(DeleteGroupTopicParam param, String operator);
    Result<Void> deleteGroupTopicPartitionOffset(DeleteGroupTopicPartitionParam param, String operator);
}
