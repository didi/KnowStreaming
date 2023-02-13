package com.xiaojukeji.kafka.manager.common.entity.pojo.ha;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.kafka.manager.common.bizenum.ha.HaResTypeEnum;
import com.xiaojukeji.kafka.manager.common.entity.pojo.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * HA-主备关系表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("ha_active_standby_relation")
public class HaASRelationDO extends BaseDO {
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
     * @see HaResTypeEnum
     */
    private Integer resType;

    /**
     * 主备状态
     */
    private Integer status;

    /**
     * 主备关系中的唯一性字段
     */
    private String uniqueField;

    public HaASRelationDO(Long id, Integer status) {
        this.id = id;
        this.status = status;
    }

    public HaASRelationDO(Long activeClusterPhyId, String activeResName, Long standbyClusterPhyId, String standbyResName, Integer resType, Integer status) {
        this.activeClusterPhyId = activeClusterPhyId;
        this.activeResName = activeResName;
        this.standbyClusterPhyId = standbyClusterPhyId;
        this.standbyResName = standbyResName;
        this.resType = resType;
        this.status = status;

        // 主备两个资源之间唯一，但是不保证两个资源之间，只存在主备关系，也可能存在双活关系，及各自都为对方的主备
        this.uniqueField = String.format("%d_%s||%d_%s||%d", activeClusterPhyId, activeResName, standbyClusterPhyId, standbyResName, resType);
    }
}
