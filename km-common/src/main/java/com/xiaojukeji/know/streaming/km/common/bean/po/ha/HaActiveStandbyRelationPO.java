package com.xiaojukeji.know.streaming.km.common.bean.po.ha;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.know.streaming.km.common.bean.po.BasePO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName(Constant.MYSQL_HA_TABLE_NAME_PREFIX + "active_standby_relation")
public class HaActiveStandbyRelationPO extends BasePO {
    private Long activeClusterPhyId;

    private Long standbyClusterPhyId;

    /**
     * 资源名称
     */
    private String resName;

    /**
     * 资源类型，0：集群，1：镜像Topic，2：主备Topic
     */
    private Integer resType;

    public HaActiveStandbyRelationPO(Long activeClusterPhyId, Long standbyClusterPhyId, String resName, Integer resType) {
        this.activeClusterPhyId = activeClusterPhyId;
        this.standbyClusterPhyId = standbyClusterPhyId;
        this.resName = resName;
        this.resType = resType;
    }
}
