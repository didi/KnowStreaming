package com.xiaojukeji.know.streaming.km.common.bean.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import lombok.Data;

@Data
@TableName(Constant.MYSQL_TABLE_NAME_PREFIX + "kafka_acl")
public class KafkaAclPO extends BasePO {
    /**
     * 集群Id
     */
    private Long clusterPhyId;

    /**
     * KafkaUser
     */
    private String principal;

    /**
     * '操作'
     */
    private Integer operation;

    /**
     * 权限类型(0:未知，1:任意，2:拒绝，3:允许)
     */
    private Integer permissionType;

    /**
     * 机器
     */
    private String host;

    /**
     * 资源类型(0:未知，1:任意，2:Topic，3:Group，4:Cluster，5:事务，6:Token)
     */
    private Integer resourceType;

    /**
     * 资源名称
     */
    private String resourceName;

    /**
     * 匹配类型(0:未知，1:任意，2:Match，3:Literal，4:prefixed)
     */
    private Integer patternType;

    /**
     * 唯一字段，由cluster_phy_id等字段组成
     */
    private String uniqueField;

    public void initUniqueField() {
        this.uniqueField = String.format(
                "%d_%s_%d_%d_%s_%d_%s_%d",
                clusterPhyId, principal, operation, permissionType, host, resourceType, resourceName, permissionType
        );
    }
}
