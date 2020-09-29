package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.dto.rd.OperateRecordDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OperateRecordDO;

import java.util.List;

/**
 * @author zhongyuankai
 * @date 2020/09/03
 */
public interface OperateRecordService {
    int insert(OperateRecordDO operateRecordDO);

    List<OperateRecordDO> queryByCondt(OperateRecordDTO dto);
}
