package com.xiaojukeji.kafka.manager.common.entity.pojo.ha;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.kafka.manager.common.entity.pojo.BaseDO;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * HA-主备关系切换子任务表
 */
@Data
@NoArgsConstructor
@TableName("ha_active_standby_switch_sub_job")
public class HaASSwitchSubJobDO extends BaseDO {
    /**
     * 任务ID
     */
    private Long jobId;

    /**
     * 主集群ID
     */
    private Long activeClusterPhyId;

    /**
     * 主集群资源名称
     */
    private String activeResName;

    /**
     * 备集群ID
     */
    private Long standbyClusterPhyId;

    /**
     * 备集群资源名称
     */
    private String standbyResName;

    /**
     * 资源类型
     */
    private Integer resType;

    /**
     * 任务状态
     */
    private Integer jobStatus;

    /**
     * 扩展数据
     * @see com.xiaojukeji.kafka.manager.common.entity.ao.ha.job.HaSubJobExtendData
     */
    private String extendData;

    public HaASSwitchSubJobDO(Long jobId, Long activeClusterPhyId, String activeResName, Long standbyClusterPhyId, String standbyResName, Integer resType, Integer jobStatus, String extendData) {
        this.jobId = jobId;
        this.activeClusterPhyId = activeClusterPhyId;
        this.activeResName = activeResName;
        this.standbyClusterPhyId = standbyClusterPhyId;
        this.standbyResName = standbyResName;
        this.resType = resType;
        this.jobStatus = jobStatus;
        this.extendData = extendData;
    }
}
