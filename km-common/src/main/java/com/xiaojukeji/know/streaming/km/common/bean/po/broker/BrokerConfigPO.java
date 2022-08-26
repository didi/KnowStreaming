package com.xiaojukeji.know.streaming.km.common.bean.po.broker;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.know.streaming.km.common.bean.po.BasePO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@TableName(Constant.MYSQL_TABLE_NAME_PREFIX + "broker_config")
public class BrokerConfigPO extends BasePO {
    /**
     * 集群Id
     */
    private Long clusterPhyId;

    /**
     * BrokerId
     */
    private Integer brokerId;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 配置值
     */
    private String configValue;

    /**
     * 差异类型
     */
    private Integer diffType;

    public BrokerConfigPO(Long clusterPhyId, Integer brokerId, String configName, String configValue, Integer diffType, Date updateTime) {
        this.clusterPhyId = clusterPhyId;
        this.brokerId = brokerId;
        this.configName = configName;
        this.configValue = configValue;
        this.diffType = diffType;
        this.updateTime = updateTime;
    }
}
