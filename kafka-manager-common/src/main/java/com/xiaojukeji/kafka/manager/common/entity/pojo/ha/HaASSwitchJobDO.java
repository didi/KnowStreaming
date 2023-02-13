package com.xiaojukeji.kafka.manager.common.entity.pojo.ha;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.kafka.manager.common.entity.pojo.BaseDO;
import lombok.Data;
import lombok.NoArgsConstructor;


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
     * 操作人
     */
    private String operator;

    public HaASSwitchJobDO(Long activeClusterPhyId, Long standbyClusterPhyId, Integer jobStatus, String operator) {
        this.activeClusterPhyId = activeClusterPhyId;
        this.standbyClusterPhyId = standbyClusterPhyId;
        this.jobStatus = jobStatus;
        this.operator = operator;
    }
}
