package com.xiaojukeji.know.streaming.km.common.bean.po.health;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.know.streaming.km.common.bean.po.BasePO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import lombok.Data;

@Data
@TableName(Constant.MYSQL_TABLE_NAME_PREFIX + "health_check_result")
public class HealthCheckResultPO extends BasePO {
    /**
     * 检查维度(0:未知，1:Cluster，2:Broker，3:Topic，4:Group)
     */
    private Integer dimension;

    /**
     * 配置ID
     */
    private String configName;

    /**
     * 物理集群ID
     */
    private Long clusterPhyId;

    /**
     * 资源名称
     */
    private String resName;

    /**
     * 是否通过
     */
    private Integer passed;
}
