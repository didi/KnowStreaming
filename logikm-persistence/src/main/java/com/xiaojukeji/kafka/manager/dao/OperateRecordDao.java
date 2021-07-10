package com.xiaojukeji.kafka.manager.dao;

import com.xiaojukeji.kafka.manager.common.entity.pojo.OperateRecordDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;

import java.util.Date;
import java.util.List;

/**
 * @author zhongyuankai
 * @date 2020/09/03
 */
public interface OperateRecordDao {

    int insert(OperateRecordDO operateRecordDO);

    List<OperateRecordDO> queryByCondition(Integer moduleId, Integer operateId, String operator, Date startTime, Date endTime);
}
