package com.xiaojukeji.know.streaming.km.core.service.group;

import com.xiaojukeji.know.streaming.km.common.bean.entity.param.group.DeleteGroupParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;

public interface OpGroupService {
    /**
     * 删除Topic
     */
    Result<Void> deleteGroupOffset(DeleteGroupParam param, String operator);
}
