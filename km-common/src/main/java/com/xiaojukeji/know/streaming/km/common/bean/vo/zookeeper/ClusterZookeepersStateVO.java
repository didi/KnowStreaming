package com.xiaojukeji.know.streaming.km.common.bean.vo.zookeeper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author wyc
 * @date 2022/9/23
 */
@Data
@ApiModel(description = "ZK状态信息")
public class ClusterZookeepersStateVO {
    @ApiModelProperty(value = "健康检查状态", example = "1")
    private Integer healthState;

    @ApiModelProperty(value = "健康检查通过数", example = "1")
    private Integer healthCheckPassed;

    @ApiModelProperty(value = "健康检查总数", example = "1")
    private Integer healthCheckTotal;

    @ApiModelProperty(value = "ZK的Leader机器", example = "127.0.0.1")
    private String leaderNode;

    @ApiModelProperty(value = "Watch数", example = "123456")
    private Integer watchCount;

    @ApiModelProperty(value = "节点存活数", example = "8")
    private Integer aliveServerCount;

    @ApiModelProperty(value = "总节点数", example = "10")
    private Integer totalServerCount;

    @ApiModelProperty(value = "Follower角色存活数", example = "8")
    private Integer aliveFollowerCount;

    @ApiModelProperty(value = "Follower角色总数", example = "10")
    private Integer totalFollowerCount;

    @ApiModelProperty(value = "Observer角色存活数", example = "3")
    private Integer aliveObserverCount;

    @ApiModelProperty(value = "Observer角色总数", example = "3")
    private Integer totalObserverCount;
}
