package com.xiaojukeji.kafka.manager.common.entity.pojo.ha;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.kafka.manager.common.entity.dto.ha.KafkaUserAndClientDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.BaseDO;
import com.xiaojukeji.kafka.manager.common.utils.ConvertUtil;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


/**
 * HA-主备关系切换任务表
 */
@Data
@NoArgsConstructor
@TableName("ha_active_standby_switch_job")
public class HaASSwitchJobDO extends BaseDO {
    /**
     * 主集群ID
     */
    private Long activeClusterPhyId;

    /**
     * 备集群ID
     */
    private Long standbyClusterPhyId;

    /**
     * 主备状态
     */
    private Integer jobStatus;

    /**
     * 类型，0：kafkaUser 1：kafkaUser+Client
     */
    private Integer type;

    /**
     * 扩展数据
     */
    private String extendData;

    /**
     * 操作人
     */
    private String operator;

    public HaASSwitchJobDO(Long activeClusterPhyId, Long standbyClusterPhyId, Integer type, List<KafkaUserAndClientDTO> extendDataObj, Integer jobStatus, String operator) {
        this.activeClusterPhyId = activeClusterPhyId;
        this.standbyClusterPhyId = standbyClusterPhyId;
        this.type = type;
        this.extendData = ValidateUtils.isEmptyList(extendDataObj)? "": ConvertUtil.obj2Json(extendDataObj);
        this.jobStatus = jobStatus;
        this.operator = operator;
    }

    public List<KafkaUserAndClientDTO> getExtendRawData() {
        if (ValidateUtils.isBlank(extendData)) {
            return new ArrayList<>();
        }

        return ConvertUtil.str2ObjArrayByJson(extendData, KafkaUserAndClientDTO.class);
    }
}
