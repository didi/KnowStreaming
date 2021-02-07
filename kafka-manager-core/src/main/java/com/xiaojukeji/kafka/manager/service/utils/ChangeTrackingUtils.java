package com.xiaojukeji.kafka.manager.service.utils;

import com.xiaojukeji.kafka.manager.common.bizenum.ModuleEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.OperateEnum;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OperateRecordDO;
import com.xiaojukeji.kafka.manager.common.utils.JsonUtils;
import com.xiaojukeji.kafka.manager.service.service.OperateRecordService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Track changes applied to Kafka.
 */
public class ChangeTrackingUtils {
    private final OperateRecordService operateRecordService;

    @Autowired
    public ChangeTrackingUtils(OperateRecordService operateRecordService) {
        this.operateRecordService = operateRecordService;
    }

    /**
     * Saving operate record to database.
     */
    public void saveOperateRecord(String operator, ModuleEnum module, String resourceName, OperateEnum operate, Map<String, String> content) {
        OperateRecordDO operateRecordDO = new OperateRecordDO();
        operateRecordDO.setOperator(operator);
        operateRecordDO.setModuleId(module.getCode());
        operateRecordDO.setResource(resourceName);
        operateRecordDO.setOperateId(operate.getCode());
        operateRecordDO.setContent(JsonUtils.toJSONString(content));
        operateRecordService.insert(operateRecordDO);
    }
}
