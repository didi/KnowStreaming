package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.bizenum.ModuleEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.OperateEnum;
import com.xiaojukeji.kafka.manager.common.entity.dto.rd.OperateRecordDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OperateRecordDO;

import java.util.List;
import java.util.Map;

/**
 * @author zhongyuankai
 * @date 2020/09/03
 */
public interface OperateRecordService {
    int insert(OperateRecordDO operateRecordDO);

    int insert(String operator, ModuleEnum module, String resourceName, OperateEnum operate, Map<String, String> content);

    List<OperateRecordDO> queryByCondition(OperateRecordDTO dto);
}
