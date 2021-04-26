package com.xiaojukeji.kafka.manager.service.service.impl;

import com.xiaojukeji.kafka.manager.common.bizenum.ModuleEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.OperateEnum;
import com.xiaojukeji.kafka.manager.common.entity.dto.rd.OperateRecordDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OperateRecordDO;
import com.xiaojukeji.kafka.manager.common.utils.JsonUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.dao.OperateRecordDao;
import com.xiaojukeji.kafka.manager.service.service.OperateRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhongyuankai
 * @date 2020/09/03
 */
@Service("operateRecordService")
public class OperateRecordServiceImpl implements OperateRecordService {
    @Autowired
    private OperateRecordDao operateRecordDao;

    @Override
    public int insert(OperateRecordDO operateRecordDO) {
        return operateRecordDao.insert(operateRecordDO);
    }

    @Override
    public int insert(String operator, ModuleEnum module, String resourceName, OperateEnum operate, Map<String, String> content) {
        OperateRecordDO operateRecordDO = new OperateRecordDO();
        operateRecordDO.setOperator(operator);
        operateRecordDO.setModuleId(module.getCode());
        operateRecordDO.setResource(resourceName);
        operateRecordDO.setOperateId(operate.getCode());
        operateRecordDO.setContent(JsonUtils.toJSONString(content));
        return insert(operateRecordDO);
    }

    @Override
    public List<OperateRecordDO> queryByCondition(OperateRecordDTO dto) {
        return operateRecordDao.queryByCondition(
                dto.getModuleId(),
                dto.getOperateId(),
                dto.getOperator(),
                ValidateUtils.isNull(dto.getStartTime()) ? null : new Date(dto.getStartTime()),
                ValidateUtils.isNull(dto.getEndTime()) ? null : new Date(dto.getEndTime())
                );
    }
}
