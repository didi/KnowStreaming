package com.xiaojukeji.kafka.manager.dao;

import com.xiaojukeji.kafka.manager.common.entity.po.OperationHistoryDO;

/**
 * @author arthur
 * @date 2017/7/20.
 */
public interface OperationHistoryDao {
    int insert(OperationHistoryDO operationHistoryDO);
}
