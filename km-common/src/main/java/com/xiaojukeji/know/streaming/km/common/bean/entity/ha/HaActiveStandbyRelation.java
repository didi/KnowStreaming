package com.xiaojukeji.know.streaming.km.common.bean.entity.ha;

import com.xiaojukeji.know.streaming.km.common.bean.po.BasePO;
import com.xiaojukeji.know.streaming.km.common.enums.ha.HaResTypeEnum;
import lombok.Data;

@Data
public class HaActiveStandbyRelation extends BasePO {
    private Long activeClusterPhyId;

    private Long standbyClusterPhyId;

    /**
     * 资源名称
     */
    private String resName;

    /**
     * 资源类型，0：集群，1：镜像Topic，2：主备Topic
     * @see HaResTypeEnum
     */
    private Integer resType;
}
