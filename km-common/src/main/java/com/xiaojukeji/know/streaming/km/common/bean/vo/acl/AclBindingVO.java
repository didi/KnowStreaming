package com.xiaojukeji.know.streaming.km.common.bean.vo.acl;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author zengqiao
 * @date 22/02/28
 */
@Data
@ApiModel(description = "ACL绑定信息")
public class AclBindingVO {
    @ApiModelProperty(value = "kafkaUser名称", example = "know-streaming")
    private String kafkaUser;

    /**
     * 定义操作 —— 操作类型
     * @see org.apache.kafka.common.acl.AclOperation
     */
    @ApiModelProperty(value = "操作类型，读/写/任意等", example = "1")
    private Integer aclOperation;

    /**
     * 定义操作 — 权限状态，允许或者拒绝
     * @see org.apache.kafka.common.acl.AclPermissionType
     */
    @ApiModelProperty(value = "权限状态，允许/拒绝等", example = "2")
    private Integer aclPermissionType;

    /**
     * 定义操作 — 客户端主机
     */
    @ApiModelProperty(value = "客户端主机", example = "127.0.0.1")
    private String aclClientHost;

    /**
     * 定义资源 —— 资源类型
     * @see org.apache.kafka.common.resource.ResourceType
     */
    @ApiModelProperty(value = "资源类型, Topic/Group等", example = "2")
    private Integer resourceType;

    /**
     * 定义资源 —— 资源名称
     */
    @ApiModelProperty(value = "资源名称", example = "know-streaming")
    private String resourceName;

    /**
     * 定义资源 —— 资源匹配方式
     * @see org.apache.kafka.common.resource.PatternType
     */
    @ApiModelProperty(value = "资源匹配方式", example = "2")
    private Integer resourcePatternType;

}
