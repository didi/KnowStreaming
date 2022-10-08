package com.xiaojukeji.know.streaming.km.common.bean.po.cluster;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.po.BasePO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import lombok.Data;

@Data
@TableName(Constant.MYSQL_TABLE_NAME_PREFIX + "physical_cluster")
public class ClusterPhyPO extends BasePO {
    /**
     * 集群名字
     */
    private String name;

    /**
     * 集群服务地址
     */
    private String bootstrapServers;

    /**
     * 版本信息
     */
    private String kafkaVersion;

    /**
     * ZK地址
     */
    private String zookeeper;

    /**
     * 集群客户端配置
     */
    private String clientProperties;

    /**
     * jmx配置
     */
    private String jmxProperties;

    /**
     * zk配置
     */
    private String zkProperties;

    /**
     * 认证类型
     * @see com.xiaojukeji.know.streaming.km.common.enums.cluster.ClusterAuthTypeEnum
     */
    private Integer authType;

    /**
     * 运行状态
     * @see com.xiaojukeji.know.streaming.km.common.enums.cluster.ClusterRunStateEnum
     */
    private Integer runState;

    /**
     * 备注
     */
    private String description;

    public static Result<Void> checkFieldAddToDBLegal(ClusterPhyPO clusterPhyPO, String operator) {
        if (clusterPhyPO == null) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "clusterPhyPO不允许为空");
        }
        if (ValidateUtils.isBlank(operator)) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "operator不允许为空");
        }
        if (ValidateUtils.isBlank(clusterPhyPO.name)) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "name不允许为空");
        }
        if (ValidateUtils.isBlank(clusterPhyPO.bootstrapServers)) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "bootstrapServers不允许为空");
        }
        if (ValidateUtils.isBlank(clusterPhyPO.kafkaVersion)) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "kafkaVersion不允许为空");
        }
        if (ValidateUtils.isNull(clusterPhyPO.zookeeper)) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "zookeeper不允许为null");
        }
        if (ValidateUtils.isBlank(clusterPhyPO.clientProperties)) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "clientProperties不允许为空");
        }
        if (ValidateUtils.isBlank(clusterPhyPO.jmxProperties)) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "jmxProperties不允许为空");
        }
        if (ValidateUtils.isNull(clusterPhyPO.authType)) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "authType不允许为空");
        }
        if (ValidateUtils.isNull(clusterPhyPO.runState)) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "runState不允许为空");
        }
        if (ValidateUtils.isNull(clusterPhyPO.description)) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "description不允许为null");
        }
        return Result.buildSuc();
    }

    public static Result<Void> checkFieldModifyToDBLegal(ClusterPhyPO clusterPhyPO, String operator) {
        Result<Void> rv = ClusterPhyPO.checkFieldAddToDBLegal(clusterPhyPO, operator);
        if (rv.failed()) {
            return rv;
        }

        if (ValidateUtils.isNullOrLessThanZero(clusterPhyPO.id)) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "id不允许为空或小于0");
        }

        return Result.buildSuc();
    }
}
